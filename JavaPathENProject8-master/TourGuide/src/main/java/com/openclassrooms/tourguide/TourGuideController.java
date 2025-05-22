/**
 * Contrôleur REST principal de l'application TourGuide.
 * Il fournit des endpoints pour localiser un utilisateur, récupérer ses attractions proches,
 * ses récompenses, et des offres de voyage personnalisées.
 */
package com.openclassrooms.tourguide;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import com.openclassrooms.tourguide.dto.NearbyAttractionDTO;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import tripPricer.Provider;

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;

    /**
     * Point d'entrée racine.
     * @return Un message de bienvenue simple.
     */
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    /**
     * Récupère la dernière position visitée par un utilisateur.
     * @param userName Nom d'utilisateur.
     * @return Dernière position connue de l'utilisateur.
     */
    @RequestMapping("/getLocation")
    public VisitedLocation getLocation(@RequestParam String userName) {
        return tourGuideService.getUserLocation(getUser(userName));
    }

    /**
     * Renvoie les 5 attractions les plus proches de l'utilisateur,
     * accompagnées d'informations enrichies (nom, coordonnées, distance, position utilisateur, points de récompense).
     * @param userName Nom d'utilisateur.
     * @return Liste de NearbyAttractionDTO correspondant aux attractions les plus proches.
     */
    @RequestMapping("/getNearbyAttractions")
    public List<NearbyAttractionDTO> getNearbyAttractions(@RequestParam String userName) {
        return tourGuideService.getNearbyAttractionsDetailed(getUser(userName));
    }

    /**
     * Renvoie les récompenses associées aux attractions visitées par l'utilisateur.
     * @param userName Nom d'utilisateur.
     * @return Liste des UserReward contenant les points attribués par attraction.
     */
    @RequestMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
        return tourGuideService.getUserRewards(getUser(userName));
    }

    /**
     * Renvoie une liste d'offres de voyage proposées à l'utilisateur.
     * @param userName Nom d'utilisateur.
     * @return Liste des offres disponibles depuis des fournisseurs partenaires.
     */
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
        return tourGuideService.getTripDeals(getUser(userName));
    }

    /**
     * Méthode utilitaire pour récupérer un utilisateur par son nom.
     * @param userName Nom de l'utilisateur.
     * @return Instance de l'utilisateur trouvé.
     */
    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }
}
