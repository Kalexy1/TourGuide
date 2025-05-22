/**
 * DTO (Data Transfer Object) représentant une attraction touristique proche d'un utilisateur.
 * Contient les coordonnées de l'attraction et de l'utilisateur, la distance entre les deux
 * et les points de récompense attribués.
 */
package com.openclassrooms.tourguide.dto;

public class NearbyAttractionDTO {

    /** Nom de l'attraction touristique. */
    public String attractionName;

    /** Latitude de l'attraction. */
    public double attractionLatitude;

    /** Longitude de l'attraction. */
    public double attractionLongitude;

    /** Latitude de l'utilisateur. */
    public double userLatitude;

    /** Longitude de l'utilisateur. */
    public double userLongitude;

    /** Distance entre l'utilisateur et l'attraction en miles. */
    public double distanceInMiles;

    /** Points de récompense attribués pour cette attraction. */
    public int rewardPoints;

    /**
     * Constructeur du DTO NearbyAttractionDTO.
     * 
     * @param attractionName Nom de l'attraction.
     * @param attractionLatitude Latitude de l'attraction.
     * @param attractionLongitude Longitude de l'attraction.
     * @param userLatitude Latitude de l'utilisateur.
     * @param userLongitude Longitude de l'utilisateur.
     * @param distanceInMiles Distance calculée en miles.
     * @param rewardPoints Points de récompense attribués.
     */
    public NearbyAttractionDTO(String attractionName, double attractionLatitude, double attractionLongitude,
                               double userLatitude, double userLongitude,
                               double distanceInMiles, int rewardPoints) {
        this.attractionName = attractionName;
        this.attractionLatitude = attractionLatitude;
        this.attractionLongitude = attractionLongitude;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.distanceInMiles = distanceInMiles;
        this.rewardPoints = rewardPoints;
    }
}
