package com.bibliotheque.controller;

import com.bibliotheque.model.Livre;
import com.bibliotheque.service.LivreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/livres")
@CrossOrigin(origins = "http://localhost:3000") // Pour une application front-end
public class LivreController {

    private final LivreService livreService;

    public LivreController(LivreService livreService) {
        this.livreService = livreService;
    }

    // GET /api/livres - Récupérer tous les livres
    @GetMapping
    public ResponseEntity<List<Livre>> getAllLivres() {
        return ResponseEntity.ok(livreService.findAll());
    }

    // GET /api/livres/{id} - Récupérer un livre par ID
    @GetMapping("/{id}")
    public ResponseEntity<Livre> getLivreById(@PathVariable Long id) {
        return livreService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/livres - Créer un nouveau livre
    @PostMapping
    public ResponseEntity<Livre> createLivre(@Valid @RequestBody Livre livre) {
        Livre nouveauLivre = livreService.save(livre);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(nouveauLivre.getId()).toUri();
        return ResponseEntity.created(location).body(nouveauLivre);
    }

    // PUT /api/livres/{id} - Mettre à jour un livre
    @PutMapping("/{id}")
    public ResponseEntity<Livre> updateLivre(@PathVariable Long id, @Valid @RequestBody Livre livreDetails) {
        return ResponseEntity.ok(livreService.update(id, livreDetails));
    }

    // DELETE /api/livres/{id} - Supprimer un livre
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLivre(@PathVariable Long id) {
        livreService.deleteById(id);
    }

    // GET /api/livres/search?keyword=... - Recherche par titre ou auteur
    @GetMapping("/search")
    public ResponseEntity<List<Livre>> searchLivres(@RequestParam String keyword) {
        return ResponseEntity.ok(livreService.search(keyword));
    }

    // GET /api/livres/disponibles - Livres disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<Livre>> getLivresDisponibles() {
        return ResponseEntity.ok(livreService.findDisponibles());
    }

    // PUT /api/livres/{id}/emprunter - Emprunter un livre
    @PutMapping("/{id}/emprunter")
    public ResponseEntity<Livre> emprunterLivre(@PathVariable Long id) {
        return ResponseEntity.ok(livreService.emprunterLivre(id));
    }

    // PUT /api/livres/{id}/retourner - Retourner un livre
    @PutMapping("/{id}/retourner")
    public ResponseEntity<Livre> retournerLivre(@PathVariable Long id) {
        return ResponseEntity.ok(livreService.retournerLivre(id));
    }
}
