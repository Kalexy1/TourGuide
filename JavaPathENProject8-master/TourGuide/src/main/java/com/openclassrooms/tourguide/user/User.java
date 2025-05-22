/**
 * Représente un utilisateur de l'application TourGuide.
 * Contient ses informations personnelles, ses lieux visités, ses récompenses,
 * ses préférences de voyage et les offres disponibles.
 */
package com.openclassrooms.tourguide.user;

import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class User {

    /** Identifiant unique de l'utilisateur. */
    private final UUID userId;

    /** Nom d'utilisateur. */
    private final String userName;

    /** Numéro de téléphone. */
    private String phoneNumber;

    /** Adresse email. */
    private String emailAddress;

    /** Horodatage de la dernière position connue. */
    private Date latestLocationTimestamp;

    /** Liste des lieux visités par l'utilisateur. */
    private List<VisitedLocation> visitedLocations = new CopyOnWriteArrayList<>();

    /** Liste des récompenses obtenues par l'utilisateur. */
    private List<UserReward> userRewards = new CopyOnWriteArrayList<>();

    /** Préférences de voyage de l'utilisateur. */
    private UserPreferences userPreferences = new UserPreferences();

    /** Offres de voyage proposées à l'utilisateur. */
    private List<Provider> tripDeals = new CopyOnWriteArrayList<>();

    /**
     * Constructeur de la classe User.
     * @param userId Identifiant unique
     * @param userName Nom d'utilisateur
     * @param phoneNumber Numéro de téléphone
     * @param emailAddress Adresse email
     */
    public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    /**
     * Retourne l'identifiant unique de l'utilisateur.
     * @return UUID de l'utilisateur.
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * Retourne le nom d'utilisateur.
     * @return nom de l'utilisateur.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Met à jour le numéro de téléphone de l'utilisateur.
     * @param phoneNumber nouveau numéro de téléphone.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Retourne le numéro de téléphone de l'utilisateur.
     * @return numéro de téléphone.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Met à jour l'adresse email de l'utilisateur.
     * @param emailAddress nouvelle adresse email.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Retourne l'adresse email de l'utilisateur.
     * @return adresse email.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Met à jour la date de la dernière localisation connue de l'utilisateur.
     * @param latestLocationTimestamp nouvelle date de localisation.
     */
    public void setLatestLocationTimestamp(Date latestLocationTimestamp) {
        this.latestLocationTimestamp = latestLocationTimestamp;
    }

    /**
     * Retourne la date de la dernière localisation connue de l'utilisateur.
     * @return date de localisation.
     */
    public Date getLatestLocationTimestamp() {
        return latestLocationTimestamp;
    }

    /**
     * Ajoute une nouvelle position visitée à l'historique.
     * @param visitedLocation Emplacement visité
     */
    public void addToVisitedLocations(VisitedLocation visitedLocation) {
        visitedLocations.add(visitedLocation);
    }

    /**
     * Retourne la liste des lieux visités.
     * @return liste de VisitedLocation
     */
    public List<VisitedLocation> getVisitedLocations() {
        return visitedLocations;
    }

    /**
     * Supprime l'historique des positions visitées.
     */
    public void clearVisitedLocations() {
        visitedLocations.clear();
    }

    /**
     * Ajoute une récompense utilisateur si elle n'existe pas déjà.
     * @param userReward Récompense à ajouter
     */
    public void addUserReward(UserReward userReward) {
        boolean alreadyExists = userRewards.stream()
            .anyMatch(r -> r.attraction.attractionName.equals(userReward.attraction.attractionName));

        if (!alreadyExists) {
            userRewards.add(userReward);
        }
    }

    /**
     * Retourne la liste des récompenses de l'utilisateur.
     * @return liste de UserReward
     */
    public List<UserReward> getUserRewards() {
        return userRewards;
    }

    /**
     * Retourne les préférences de voyage de l'utilisateur.
     * @return objet {@link UserPreferences} représentant les préférences.
     */
    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    /**
     * Définit les préférences de voyage de l'utilisateur.
     * @param userPreferences nouvelles préférences à associer à l'utilisateur.
     */
    public void setUserPreferences(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    /**
     * Récupère le dernier lieu visité.
     * @return VisitedLocation le plus récent
     */
    public VisitedLocation getLastVisitedLocation() {
        return visitedLocations.get(visitedLocations.size() - 1);
    }

    /**
     * Définit la liste des offres de voyage disponibles pour l'utilisateur.
     * @param tripDeals liste d'offres de voyage (fournisseurs).
     */
    public void setTripDeals(List<Provider> tripDeals) {
        this.tripDeals = tripDeals;
    }

    /**
     * Retourne la liste des offres de voyage associées à l'utilisateur.
     * @return liste de fournisseurs (`Provider`).
     */
    public List<Provider> getTripDeals() {
        return tripDeals;
    }

}
