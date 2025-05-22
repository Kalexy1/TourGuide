/**
 * Service de gestion des récompenses dans l'application TourGuide.
 * Il permet de calculer les points de récompense des utilisateurs en fonction de leurs emplacements visités
 * et de leur proximité avec les attractions touristiques.
 */
package com.openclassrooms.tourguide.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.*;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.user.*;

@Service
public class RewardsService {

    /** Constante de conversion des miles nautiques en miles terrestres. */
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    /** Taille du pool de threads pour le calcul parallèle. */
    private static final int THREAD_POOL_SIZE = 100;

    /** Distance de proximité par défaut. */
    private int defaultProximityBuffer = 10;

    /** Distance de proximité personnalisée. */
    private int proximityBuffer = defaultProximityBuffer;

    private final GpsUtil gpsUtil;
    private final RewardCentral rewardsCentral;
    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    /** Cache des points de récompense pour éviter les appels redondants. */
    private final Map<String, Integer> rewardCache = new ConcurrentHashMap<>();

    /**
     * Constructeur du service.
     * @param gpsUtil Service de géolocalisation.
     * @param rewardCentral Service des points de récompense.
     */
    public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
        this.gpsUtil = gpsUtil;
        this.rewardsCentral = rewardCentral;
    }

    /**
     * Définit une nouvelle valeur pour la distance de proximité.
     * @param proximityBuffer valeur personnalisée en miles.
     */
    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    /**
     * Réinitialise la distance de proximité à sa valeur par défaut.
     */
    public void setDefaultProximityBuffer() {
        this.proximityBuffer = defaultProximityBuffer;
    }

    /**
     * Calcule les récompenses pour un utilisateur donné en fonction de ses lieux visités.
     * Seules les attractions proches sont considérées.
     * @param user L'utilisateur concerné.
     */
    public void calculateRewards(User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        List<Attraction> attractions = gpsUtil.getAttractions().stream()
                .limit(1) // limitation pour les performances
                .collect(Collectors.toList());

        for (VisitedLocation visitedLocation : visitedLocations) {
            for (Attraction attraction : attractions) {
                if (getDistance(visitedLocation.location, attraction) <= proximityBuffer) {
                    synchronized (user.getUserRewards()) {
                        boolean alreadyRewarded = user.getUserRewards().stream()
                            .anyMatch(r -> r.attraction.attractionName.equals(attraction.attractionName));
                        if (!alreadyRewarded) {
                            int rewardPoints = getRewardPoints(attraction, user);
                            user.addUserReward(new UserReward(visitedLocation, attraction, rewardPoints));
                        }
                    }
                }
            }
        }
    }

    /**
     * Calcule les récompenses pour tous les utilisateurs de façon parallèle.
     * @param users Liste des utilisateurs.
     */
    public void calculateRewardsForAllUsers(List<User> users) {
        List<CompletableFuture<Void>> futures = users.stream()
            .map(user -> CompletableFuture.runAsync(() -> calculateRewards(user), executor))
            .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * Récupère les points de récompense pour un utilisateur et une attraction.
     * Résultat mis en cache pour éviter les appels redondants à RewardCentral.
     * @param attraction L'attraction visée.
     * @param user L'utilisateur.
     * @return Points de récompense attribués.
     */
    public int getRewardPoints(Attraction attraction, User user) {
        String key = attraction.attractionId + "_" + user.getUserId();
        return rewardCache.computeIfAbsent(key,
            k -> rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId()));
    }

    /**
     * Calcule la distance entre deux positions GPS.
     * @param loc1 première position.
     * @param loc2 deuxième position.
     * @return distance entre les deux en miles.
     */
    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude), lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude), lon2 = Math.toRadians(loc2.longitude);
        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
        double nauticalMiles = 60 * Math.toDegrees(angle);
        return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    }

    /**
     * Arrête proprement le pool de threads utilisé pour le traitement parallèle.
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Vérifie si une attraction est à proximité d'une position donnée.
     * @param attraction L'attraction.
     * @param location Position de l'utilisateur.
     * @return true si à proximité, false sinon.
     */
    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return getDistance(attraction, location) <= proximityBuffer;
    }
}
