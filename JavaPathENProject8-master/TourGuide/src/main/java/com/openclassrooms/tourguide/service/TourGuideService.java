package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.dto.NearbyAttractionDTO;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.tracker.Tracker;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;
import tripPricer.TripPricer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TourGuideService {

	private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	private final Map<String, User> internalUserMap = new HashMap<>();
	private static final String tripPricerApiKey = "test-server-api-key";
	boolean testMode = true;

	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;

		Locale.setDefault(Locale.US);

		if (testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}

		tracker = new Tracker(this);
		addShutDownHook();
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocation(User user) {
		return user.getVisitedLocations().size() > 0
				? user.getLastVisitedLocation()
				: trackUserLocation(user);
	}

	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	public List<User> getAllUsers() {
		return new ArrayList<>(internalUserMap.values());
	}

	public void addUser(User user) {
		internalUserMap.putIfAbsent(user.getUserName(), user);
	}

	public List<Provider> getTripDeals(User user) {
		int cumulatedRewardPoints = user.getUserRewards().stream()
				.mapToInt(UserReward::getRewardPoints)
				.sum();

		List<Provider> providers = tripPricer.getPrice(
				tripPricerApiKey,
				user.getUserId(),
				user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(),
				cumulatedRewardPoints
		);
		user.setTripDeals(providers);
		return providers;
	}

	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		return gpsUtil.getAttractions().stream()
			.sorted(Comparator.comparingDouble(a ->
				rewardsService.getDistance(visitedLocation.location, a)))
			.limit(5)
			.collect(Collectors.toList());
	}

	public List<NearbyAttractionDTO> getNearbyAttractionsDetailed(User user) {
		VisitedLocation visitedLocation = getUserLocation(user);

		return gpsUtil.getAttractions().stream()
			.sorted(Comparator.comparingDouble(a ->
				rewardsService.getDistance(visitedLocation.location, a)))
			.limit(5)
			.map(attraction -> {
				double distance = rewardsService.getDistance(visitedLocation.location, attraction);
				int points = rewardsService.getRewardPoints(attraction, user);
				return new NearbyAttractionDTO(
					attraction.attractionName,
					attraction.latitude,
					attraction.longitude,
					visitedLocation.location.latitude,
					visitedLocation.location.longitude,
					distance,
					points
				);
			})
			.collect(Collectors.toList());
	}

	public void trackAllUsersAsync(List<User> users) {
		List<CompletableFuture<Void>> futures = users.stream()
			.map(user -> CompletableFuture.runAsync(() -> trackUserLocation(user)))
			.collect(Collectors.toList());

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> tracker.stopTracking()));
	}

	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			User user = new User(
				UUID.randomUUID(),
				userName,
				"000",
				userName + "@tourGuide.com"
			);
			generateUserLocationHistory(user);
			internalUserMap.put(userName, user);
		});
		logger.debug("Created {} internal test users.", InternalTestHelper.getInternalUserNumber());
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i -> {
			user.addToVisitedLocations(new VisitedLocation(
				user.getUserId(),
				new Location(generateRandomLatitude(), generateRandomLongitude()),
				getRandomTime()
			));
		});
	}

	private double generateRandomLongitude() {
		return -180 + new Random().nextDouble() * 360;
	}

	private double generateRandomLatitude() {
		return -85.05112878 + new Random().nextDouble() * 170.10225756;
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
}
