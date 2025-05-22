/**
 * Représente une récompense attribuée à un utilisateur
 * pour avoir visité une attraction touristique.
 */
package com.openclassrooms.tourguide.user;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

public class UserReward {

    /** Emplacement visité par l'utilisateur. */
    public final VisitedLocation visitedLocation;

    /** Attraction touristique associée à la récompense. */
    public final Attraction attraction;

    /** Nombre de points de récompense obtenus. */
    private int rewardPoints;

    /**
     * Constructeur avec points de récompense spécifiés.
     * @param visitedLocation lieu visité
     * @param attraction attraction visitée
     * @param rewardPoints points attribués
     */
    public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;
        this.rewardPoints = rewardPoints;
    }

    /**
     * Constructeur sans spécifier les points (peuvent être définis plus tard).
     * @param visitedLocation lieu visité
     * @param attraction attraction visitée
     */
    public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;
    }

    /**
     * Définit les points de récompense.
     * @param rewardPoints nombre de points
     */
    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    /**
     * Retourne le nombre de points de récompense attribués.
     * @return points de récompense
     */
    public int getRewardPoints() {
        return rewardPoints;
    }
}
