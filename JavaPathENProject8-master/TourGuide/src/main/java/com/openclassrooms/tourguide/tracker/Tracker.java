/**
 * Service de suivi automatique des utilisateurs.
 * Ce thread exécute périodiquement le tracking de tous les utilisateurs enregistrés dans le service TourGuideService.
 */
package com.openclassrooms.tourguide.tracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;

public class Tracker extends Thread {

    private final Logger logger = LoggerFactory.getLogger(Tracker.class);

    /** Intervalle entre chaque cycle de suivi en secondes (5 minutes). */
    private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);

    /** Thread unique pour exécuter le tracking en arrière-plan. */
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /** Service principal contenant la logique de localisation utilisateur. */
    private final TourGuideService tourGuideService;

    /** Indicateur pour arrêter le thread proprement. */
    private boolean stop = false;

    /**
     * Constructeur du Tracker.
     * Lance automatiquement le thread de suivi à l'initialisation.
     * 
     * @param tourGuideService service TourGuide associé
     */
    public Tracker(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
        executorService.submit(this);
    }

    /**
     * Arrête proprement le thread de tracking.
     * Ferme l'executor associé et interrompt la boucle de suivi.
     */
    public void stopTracking() {
        stop = true;
        executorService.shutdownNow();
    }

    /**
     * Méthode principale du thread.
     * Lance le suivi de localisation pour tous les utilisateurs à intervalles réguliers.
     */
    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();
        while (true) {
            if (Thread.currentThread().isInterrupted() || stop) {
                logger.debug("Tracker stopping");
                break;
            }

            List<User> users = tourGuideService.getAllUsers();
            logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
            stopWatch.start();
            users.forEach(u -> tourGuideService.trackUserLocation(u));
            stopWatch.stop();
            logger.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
            stopWatch.reset();

            try {
                logger.debug("Tracker sleeping");
                TimeUnit.SECONDS.sleep(trackingPollingInterval);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
