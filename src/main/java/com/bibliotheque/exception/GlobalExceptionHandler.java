package com.bibliotheque.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Erreurs de validation (@Valid) -> 400 avec le détail par champ
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> erreurs = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String champ = ((FieldError) error).getField();
            erreurs.put(champ, error.getDefaultMessage());
        });
        Map<String, Object> corps = corps(HttpStatus.BAD_REQUEST, "Données invalides");
        corps.put("erreurs", erreurs);
        return ResponseEntity.badRequest().body(corps);
    }

    // Livre inexistant -> 404
    @ExceptionHandler(LivreIntrouvableException.class)
    public ResponseEntity<Map<String, Object>> handleIntrouvable(LivreIntrouvableException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corps(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // Règle métier violée -> 409
    @ExceptionHandler(RegleMetierException.class)
    public ResponseEntity<Map<String, Object>> handleRegleMetier(RegleMetierException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corps(HttpStatus.CONFLICT, ex.getMessage()));
    }

    // Requête mal formée (JSON illisible, paramètre manquant ou mal typé) -> 400
    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> handleRequeteInvalide(Exception ex) {
        return ResponseEntity.badRequest().body(corps(HttpStatus.BAD_REQUEST, "Requête invalide"));
    }

    // URL inconnue -> 404, méthode HTTP non prise en charge -> 405
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUrlInconnue(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corps(HttpStatus.NOT_FOUND, "Ressource introuvable"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethode(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(corps(HttpStatus.METHOD_NOT_ALLOWED, "Méthode HTTP non autorisée"));
    }

    // Toute autre erreur -> 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(corps(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue"));
    }

    private Map<String, Object> corps(HttpStatus statut, String message) {
        Map<String, Object> corps = new LinkedHashMap<>();
        corps.put("horodatage", LocalDateTime.now().toString());
        corps.put("statut", statut.value());
        corps.put("message", message);
        return corps;
    }
}
