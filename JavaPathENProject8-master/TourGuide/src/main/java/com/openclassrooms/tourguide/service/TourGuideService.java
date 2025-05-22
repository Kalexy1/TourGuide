/**
 * Service principal de l'application TourGuide.
 * Gère les utilisateurs, la géolocalisation, les récompenses, les attractions proches
 * et les offres de voyage. Il pilote également le système de tracking.
 */
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

    /**
     * Constructeur du service TourGuide.
     * Initialise les utilisateurs internes si testMode est actif.
     * @param gpsUtil service de géolocalisation
     * @param rewardsService service de récompense
     */
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

    /**
     * Retourne les récompenses d'un utilisateur.
     * @param user utilisateur concerné
     * @return liste des récompenses
     */
    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    /**
     * Retourne la dernière position connue ou la suit si elle est vide.
     * @param user utilisateur concerné
     * @return dernière position visitée
     */
    public VisitedLocation getUserLocation(User user) {
        return user.getVisitedLocations().size() > 0
                ? user.getLastVisitedLocation()
                : trackUserLocation(user);
    }

    /**
     * Récupère un utilisateur par son nom.
     * @param userName nom d'utilisateur
     * @return instance de User
     */
    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }

    /**
     * Retourne tous les utilisateurs connus.
     * @return liste des utilisateurs
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(internalUserMap.values());
    }

    /**
     * Ajoute un nouvel utilisateur si non existant.
     * @param user utilisateur à ajouter
     */
    public void addUser(User user) {
        internalUserMap.putIfAbsent(user.getUserName(), user);
    }

    /**
     * Récupère les offres de voyage personnalisées d'un utilisateur.
     * @param user utilisateur concerné
     * @return liste de fournisseurs avec prix
     */
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

    /**
     * Suit la position d'un utilisateur et déclenche le calcul des récompenses.
     * @param user utilisateur suivi
     * @return nouvelle position visitée
     */
    public VisitedLocation trackUserLocation(User user) {
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    /**
     * Retourne les 5 attractions les plus proches d'une position.
     * @param visitedLocation position utilisateur
     * @return liste des attractions les plus proches
     */
    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        return gpsUtil.getAttractions().stream()
                .sorted(Comparator.comparingDouble(a ->
                        rewardsService.getDistance(visitedLocation.location, a)))
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Retourne les 5 attractions les plus proches enrichies en distance, coordonnées et récompenses.
     * @param user utilisateur concerné
     * @return liste des NearbyAttractionDTO
     */
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

    /**
     * Suit tous les utilisateurs de manière asynchrone.
     * @param users liste des utilisateurs
     */
    public void trackAllUsersAsync(List<User> users) {
        List<CompletableFuture<Void>> futures = users.stream()
                .map(user -> CompletableFuture.runAsync(() -> trackUserLocation(user)))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /** Ajoute un hook pour arrêter le tracker proprement à l'arrêt de l'application. */
    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> tracker.stopTracking()));
    }

    /** Initialise des utilisateurs internes pour les tests. */
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

    /** Génére l'historique de localisation d'un utilisateur. */
    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(
                    user.getUserId(),
                    new Location(generateRandomLatitude(), generateRandomLongitude()),
                    getRandomTime()
            ));
        });
    }

    /**
     * Génère une longitude aléatoire entre -180 et +180 degrés.
     * @return valeur de longitude aléatoire.
     */
    private double generateRandomLongitude() {
        return -180 + new Random().nextDouble() * 360;
    }

    /**
     * Génère une latitude aléatoire entre -85.05112878 et +85.05112878 degrés.
     * Cette plage respecte la limite d'affichage des cartes (Web Mercator).
     * @return valeur de latitude aléatoire.
     */
    private double generateRandomLatitude() {
        return -85.05112878 + new Random().nextDouble() * 170.10225756;
    }

    /**
     * Génère une date aléatoire dans les 30 derniers jours.
     * @return date aléatoire au format java.util.Date.
     */
    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

}
