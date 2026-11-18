package com.bibliotheque.service;

import com.bibliotheque.exception.LivreIntrouvableException;
import com.bibliotheque.exception.RegleMetierException;
import com.bibliotheque.model.Livre;
import com.bibliotheque.repository.LivreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LivreService {

    private final LivreRepository livreRepository;

    public LivreService(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    // Récupérer tous les livres
    @Transactional(readOnly = true)
    public List<Livre> findAll() {
        return livreRepository.findAll();
    }

    // Récupérer un livre par ID
    @Transactional(readOnly = true)
    public Optional<Livre> findById(Long id) {
        return livreRepository.findById(id);
    }

    // Créer un nouveau livre
    public Livre save(Livre livre) {
        if (livreRepository.findByIsbn(livre.getIsbn()).isPresent()) {
            throw new RegleMetierException("Un livre avec cet ISBN existe déjà");
        }
        livre.setId(null);
        return livreRepository.save(livre);
    }

    // Mettre à jour un livre
    public Livre update(Long id, Livre livreDetails) {
        Livre livre = trouver(id);
        if (!livre.getIsbn().equals(livreDetails.getIsbn())
                && livreRepository.findByIsbn(livreDetails.getIsbn()).isPresent()) {
            throw new RegleMetierException("Un livre avec cet ISBN existe déjà");
        }
        livre.setTitre(livreDetails.getTitre());
        livre.setAuteur(livreDetails.getAuteur());
        livre.setAnneePublication(livreDetails.getAnneePublication());
        livre.setIsbn(livreDetails.getIsbn());
        livre.setDisponible(livreDetails.isDisponible());
        return livreRepository.save(livre);
    }

    // Supprimer un livre
    public void deleteById(Long id) {
        livreRepository.delete(trouver(id));
    }

    // Recherche par mot-clé (titre ou auteur)
    @Transactional(readOnly = true)
    public List<Livre> search(String keyword) {
        return livreRepository.searchByTitreOrAuteur(keyword);
    }

    // Récupérer les livres disponibles
    @Transactional(readOnly = true)
    public List<Livre> findDisponibles() {
        return livreRepository.findByDisponible(true);
    }

    // Emprunter un livre
    public Livre emprunterLivre(Long id) {
        Livre livre = trouver(id);
        if (!livre.isDisponible()) {
            throw new RegleMetierException("Le livre n'est pas disponible");
        }
        livre.setDisponible(false);
        return livreRepository.save(livre);
    }

    // Retourner un livre
    public Livre retournerLivre(Long id) {
        Livre livre = trouver(id);
        if (livre.isDisponible()) {
            throw new RegleMetierException("Le livre n'est pas emprunté");
        }
        livre.setDisponible(true);
        return livreRepository.save(livre);
    }

    private Livre trouver(Long id) {
        return livreRepository.findById(id).orElseThrow(() -> new LivreIntrouvableException(id));
    }
}
