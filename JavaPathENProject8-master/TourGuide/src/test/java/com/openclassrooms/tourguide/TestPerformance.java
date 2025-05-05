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

    /**
     * Performance targets:
     * - highVolumeTrackLocation: 100,000 users within 15 minutes
     * - highVolumeGetRewards: 100,000 users within 20 minutes
     */
    @Test
    public void highVolumeTrackLocation() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

        InternalTestHelper.setInternalUserNumber(100); // test progressif : 100 -> 1000 -> 10000 -> 100000
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

        InternalTestHelper.setInternalUserNumber(100000); // test progressif : 100 -> 1000 -> 10000 -> 100000
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

        System.out.println("highVolumeGetRewards: Time Elapsed: "
                + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");

        for (User user : allUsers) {
            assertTrue(user.getUserRewards().size() > 0);
        }

        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }
}
