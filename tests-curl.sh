#!/usr/bin/env bash
# Scénario de test de l'API (le service doit être lancé : mvn spring-boot:run)
URL=http://localhost:8080/api/livres
J="Content-Type: application/json"
t() { echo; echo "===== $1"; }

t "1. Créer un livre (201)"
curl -s -i -X POST $URL -H "$J" -d '{"titre":"Le Petit Prince","auteur":"Antoine de Saint-Exupéry","anneePublication":1943,"isbn":"978-2-07-040525-0"}'
t "2. Créer un 2e livre (201)"
curl -s -X POST $URL -H "$J" -d '{"titre":"L'"'"'Étranger","auteur":"Albert Camus","anneePublication":1942,"isbn":"978-2-07-036002-4"}'
t "3. Tous les livres (200)"
curl -s $URL
t "4. Livre id=1 (200)"
curl -s $URL/1
t "5. Recherche keyword=prince (200)"
curl -s "$URL/search?keyword=prince"
t "6. Emprunter le livre 1 (200)"
curl -s -X PUT $URL/1/emprunter
t "7. Livres disponibles (200)"
curl -s $URL/disponibles
t "8. Retourner le livre 1 (200)"
curl -s -X PUT $URL/1/retourner
t "9. Mettre à jour le livre 2 (200)"
curl -s -X PUT $URL/2 -H "$J" -d '{"titre":"L'"'"'Étranger (édition Folio)","auteur":"Albert Camus","anneePublication":1942,"isbn":"978-2-07-036002-4","disponible":true}'
t "10. ISBN en double (409)"
curl -s -i -X POST $URL -H "$J" -d '{"titre":"Copie","auteur":"X","anneePublication":2000,"isbn":"978-2-07-040525-0"}'
t "11. Données invalides (400)"
curl -s -i -X POST $URL -H "$J" -d '{"titre":"","auteur":"","anneePublication":500,"isbn":""}'
t "12. Livre inexistant (404)"
curl -s -i $URL/99
t "13. Supprimer le livre 2 (204)"
curl -s -i -X DELETE $URL/2
echo
