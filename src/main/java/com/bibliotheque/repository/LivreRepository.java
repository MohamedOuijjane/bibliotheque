package com.bibliotheque.repository;

import com.bibliotheque.model.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {

    // Recherche par titre (contient)
    List<Livre> findByTitreContainingIgnoreCase(String titre);

    // Recherche par auteur
    List<Livre> findByAuteurContainingIgnoreCase(String auteur);

    // Recherche par disponibilité
    List<Livre> findByDisponible(boolean disponible);

    // Recherche par année de publication
    List<Livre> findByAnneePublication(Integer annee);

    // Recherche combinée titre et auteur
    @Query("SELECT l FROM Livre l WHERE LOWER(l.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(l.auteur) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Livre> searchByTitreOrAuteur(@Param("keyword") String keyword);

    // Vérifier si un ISBN existe déjà
    Optional<Livre> findByIsbn(String isbn);
}
