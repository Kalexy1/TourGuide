/**
 * Classe utilitaire pour configurer le nombre d'utilisateurs utilisés dans les tests internes de performance.
 * Elle permet de fixer dynamiquement le volume simulé pour les tests de charge.
 */
package com.openclassrooms.tourguide.helper;

public class InternalTestHelper {

    /** Nombre d'utilisateurs simulés pour les tests internes (par défaut à 100). */
    private static int internalUserNumber = 100;

    /**
     * Définit le nombre d'utilisateurs internes à générer pour les tests.
     * @param internalUserNumber le nombre d'utilisateurs à simuler.
     */
    public static void setInternalUserNumber(int internalUserNumber) {
        InternalTestHelper.internalUserNumber = internalUserNumber;
    }

    /**
     * Récupère le nombre d'utilisateurs internes configuré pour les tests.
     * @return le nombre d'utilisateurs simulés.
     */
    public static int getInternalUserNumber() {
        return internalUserNumber;
    }
}
