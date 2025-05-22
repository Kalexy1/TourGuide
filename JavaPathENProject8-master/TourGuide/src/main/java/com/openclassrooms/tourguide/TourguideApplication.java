/**
 * Classe principale de l'application TourGuide.
 * Lance l'application Spring Boot.
 */
package com.openclassrooms.tourguide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application Spring Boot TourGuide.
 */
@SpringBootApplication
public class TourguideApplication {

    /**
     * Méthode main pour démarrer l'application.
     *
     * @param args arguments passés en ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(TourguideApplication.class, args);
    }
}
