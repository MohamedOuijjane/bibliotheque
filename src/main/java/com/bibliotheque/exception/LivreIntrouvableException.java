package com.bibliotheque.exception;

/** Levée quand aucun livre ne correspond à l'identifiant demandé (HTTP 404). */
public class LivreIntrouvableException extends RuntimeException {

    public LivreIntrouvableException(Long id) {
        super("Livre non trouvé avec l'id : " + id);
    }
}
