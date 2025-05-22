/**
 * Représente les préférences de voyage définies par un utilisateur.
 * Ces préférences sont utilisées pour générer des offres personnalisées.
 */
package com.openclassrooms.tourguide.user;

public class UserPreferences {

    /** Proximité maximale des attractions touristiques en miles. */
    private int attractionProximity = Integer.MAX_VALUE;

    /** Durée du voyage en jours. */
    private int tripDuration = 1;

    /** Nombre de tickets souhaités. */
    private int ticketQuantity = 1;

    /** Nombre d'adultes dans le groupe de voyage. */
    private int numberOfAdults = 1;

    /** Nombre d'enfants dans le groupe de voyage. */
    private int numberOfChildren = 0;

    /**
     * Constructeur par défaut.
     */
    public UserPreferences() {
    }

    /**
     * Définit la proximité maximale pour les attractions touristiques.
     * @param attractionProximity distance maximale en miles.
     */
    public void setAttractionProximity(int attractionProximity) {
        this.attractionProximity = attractionProximity;
    }

    /**
     * Retourne la distance maximale autorisée pour une attraction touristique.
     * @return distance en miles.
     */
    public int getAttractionProximity() {
        return attractionProximity;
    }

    /**
     * Retourne la durée souhaitée du voyage.
     * @return durée en jours.
     */
    public int getTripDuration() {
        return tripDuration;
    }

    /**
     * Définit la durée du voyage.
     * @param tripDuration durée en jours.
     */
    public void setTripDuration(int tripDuration) {
        this.tripDuration = tripDuration;
    }

    /**
     * Retourne le nombre de tickets souhaités.
     * @return nombre de billets.
     */
    public int getTicketQuantity() {
        return ticketQuantity;
    }

    /**
     * Définit le nombre de tickets souhaités.
     * @param ticketQuantity nombre de billets.
     */
    public void setTicketQuantity(int ticketQuantity) {
        this.ticketQuantity = ticketQuantity;
    }

    /**
     * Retourne le nombre d'adultes dans le voyage.
     * @return nombre d'adultes.
     */
    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    /**
     * Définit le nombre d'adultes dans le voyage.
     * @param numberOfAdults nombre d'adultes.
     */
    public void setNumberOfAdults(int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    /**
     * Retourne le nombre d'enfants dans le voyage.
     * @return nombre d'enfants.
     */
    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    /**
     * Définit le nombre d'enfants dans le voyage.
     * @param numberOfChildren nombre d'enfants.
     */
    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }
}
