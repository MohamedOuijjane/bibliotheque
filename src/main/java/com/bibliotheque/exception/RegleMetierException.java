package com.bibliotheque.exception;

/** Levée quand une règle métier est violée : ISBN en double, livre déjà emprunté... (HTTP 409). */
public class RegleMetierException extends RuntimeException {

    public RegleMetierException(String message) {
        super(message);
    }
}
