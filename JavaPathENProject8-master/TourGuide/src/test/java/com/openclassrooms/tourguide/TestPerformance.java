package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;

public class TestPerformance {

    @Test
    public void highVolumeTrackLocation() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

        InternalTestHelper.setInternalUserNumber(100000);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
        List<User> allUsers = tourGuideService.getAllUsers();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        tourGuideService.trackAllUsersAsync(allUsers);

        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeTrackLocation: Time Elapsed: "
                + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");

        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    @Test
    public void highVolumeGetRewards() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        rewardsService.setProximityBuffer(Integer.MAX_VALUE);

        InternalTestHelper.setInternalUserNumber(100);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
        List<User> allUsers = tourGuideService.getAllUsers();

        Attraction attraction = gpsUtil.getAttractions().get(0);
        Date now = new Date();
        allUsers.forEach(user ->
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, now))
        );

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        rewardsService.calculateRewardsForAllUsers(allUsers);

        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        long elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime());
        System.out.println("highVolumeGetRewards: Time Elapsed: " + elapsedSeconds + " seconds.");

        for (User user : allUsers) {
            assertTrue(user.getUserRewards().size() > 0, "User should have at least one reward");
        }

        assertTrue(elapsedSeconds <= TimeUnit.MINUTES.toSeconds(20),
            "Performance goal not met: took " + elapsedSeconds + " seconds");
    }
}
