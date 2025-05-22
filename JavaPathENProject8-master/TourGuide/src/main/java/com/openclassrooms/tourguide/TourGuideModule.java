/**
 * Configuration Spring de l'application TourGuide.
 * Déclare les beans nécessaires à l'injection de dépendances : GpsUtil, RewardCentral, RewardsService.
 */
package com.openclassrooms.tourguide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.service.RewardsService;

@Configuration
public class TourGuideModule {

    /**
     * Bean Spring pour l'outil GPS permettant de récupérer la position des utilisateurs et les attractions.
     * @return instance de GpsUtil.
     */
    @Bean
    public GpsUtil getGpsUtil() {
        return new GpsUtil();
    }

    /**
     * Bean Spring pour le service de calcul des récompenses.
     * Il est initialisé avec les beans GpsUtil et RewardCentral.
     * @return instance de RewardsService.
     */
    @Bean
    public RewardsService getRewardsService() {
        return new RewardsService(getGpsUtil(), getRewardCentral());
    }

    /**
     * Bean Spring pour l'outil RewardCentral, permettant de récupérer les points de récompense.
     * @return instance de RewardCentral.
     */
    @Bean
    public RewardCentral getRewardCentral() {
        return new RewardCentral();
    }
}
