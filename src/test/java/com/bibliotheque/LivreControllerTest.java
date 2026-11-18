package com.bibliotheque;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LivreControllerTest {

    private static final String PETIT_PRINCE = """
            {"titre":"Le Petit Prince","auteur":"Antoine de Saint-Exupéry",
             "anneePublication":1943,"isbn":"978-2-07-040525-0"}""";

    @Autowired
    private MockMvc mvc;

    @Test
    @Order(1)
    void creerUnLivre() throws Exception {
        mvc.perform(post("/api/livres").contentType(MediaType.APPLICATION_JSON).content(PETIT_PRINCE))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.disponible").value(true));
    }

    @Test
    @Order(2)
    void refuserUnIsbnEnDouble() throws Exception {
        mvc.perform(post("/api/livres").contentType(MediaType.APPLICATION_JSON).content(PETIT_PRINCE))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    void refuserUnLivreInvalide() throws Exception {
        mvc.perform(post("/api/livres").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titre\":\"\",\"auteur\":\"X\",\"anneePublication\":500,\"isbn\":\"1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreurs.titre").exists())
                .andExpect(jsonPath("$.erreurs.anneePublication").exists());
    }

    @Test
    @Order(4)
    void listerEtRechercher() throws Exception {
        mvc.perform(get("/api/livres")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/livres/search").param("keyword", "prince"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/livres/search").param("keyword", "zola"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(5)
    void emprunterPuisRetourner() throws Exception {
        mvc.perform(put("/api/livres/1/emprunter"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.disponible").value(false));
        mvc.perform(put("/api/livres/1/emprunter")).andExpect(status().isConflict());
        mvc.perform(get("/api/livres/disponibles")).andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(put("/api/livres/1/retourner"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.disponible").value(true));
    }

    @Test
    @Order(6)
    void livreInexistant() throws Exception {
        mvc.perform(get("/api/livres/99")).andExpect(status().isNotFound());
        mvc.perform(put("/api/livres/99/emprunter")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/livres/99")).andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    void supprimerUnLivre() throws Exception {
        mvc.perform(delete("/api/livres/1")).andExpect(status().isNoContent());
        mvc.perform(get("/api/livres/1")).andExpect(status().isNotFound());
    }
}
