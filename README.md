# Inscriptions universitaires

API REST qui gère les étudiants, les enseignants, les modules, les créneaux et les inscriptions. Une inscription n'est acceptée que si l'étudiant n'est pas déjà inscrit, s'il reste des places et si tous les prérequis du module sont déjà validés.

## Stack

| Élément | Version |
|---|---|
| Java | 17 (bytecode). L'exécution locale peut se faire avec un JDK plus récent |
| Spring Boot | 3.2.5 |
| Spring Web | API REST |
| Spring Data JPA / Hibernate | persistance |
| Validation Jakarta | `@NotBlank`, `@Email`, `@NotNull`, `@Min` |
| MySQL | base `db_inscriptions` |
| Lombok | 1.18.38 |
| JUnit 5 + Mockito | tests des services |
| Maven | build et `mvn test` |

Les identifiants MySQL sont dans `src/main/resources/application.properties`. Hibernate met le schéma à jour au démarrage (`spring.jpa.hibernate.ddl-auto=update`). L'API écoute sur `http://localhost:8080`.

Si les tests Mockito échouent sur un JDK récent avec le message `Java xx is not supported by the current version of Byte Buddy`, Surefire lance déjà la JVM de test avec `-Dnet.bytebuddy.experimental=true`. Relancer les tests depuis le cycle de vie Maven (`test`), pas seulement avec le bouton vert d'IntelliJ.

## Fonctionnement

Chaque ressource suit la même chaîne :

`Controller` → `Service` → `Repository` → entité JPA

Le controller ne voit que des DTO. Le mapper fait la conversion. Le service porte les règles métier et lève :

- `ResourceNotFoundException` → HTTP **404**
- `BusinessException` → HTTP **400**
- une contrainte `@Valid` non respectée → HTTP **400**, avec le détail des champs

Une création renvoie **201**. Une lecture renvoie **200**. Une suppression réussie renvoie **204** (corps vide).

### Règles métier

**Étudiant.** L'email est obligatoire, unique et doit avoir un format valide. La suppression est refusée tant qu'il reste des inscriptions.

**Enseignant.** Même règle d'email. La suppression est refusée s'il est encore responsable d'un module.

**Module.** Le code est unique. `nombrePlaces` doit être au moins 1. `enseignantId` est facultatif. `prerequisIds` contient les identifiants de modules qui doivent déjà exister. La réponse expose ces prérequis sous forme de codes (`codesPrerequis`) et le nombre de places encore libres. Les places libres valent `nombrePlaces` moins le nombre d'inscriptions au statut `VALIDE`. La suppression est refusée s'il reste des inscrits, ou si le module sert de prérequis à un autre module. Ses créneaux partent avec lui.

**Créneau.** Lié à un module obligatoire. `heureFin` doit être strictement après `heureDebut`. Les heures sont des `LocalTime`, au format JSON `"HH:mm:ss"`.

**Inscription.** On envoie l'identifiant de l'étudiant et celui du module. Le service refuse la demande si :

- l'étudiant ou le module n'existe pas (**404**) ;
- cet étudiant est déjà inscrit à ce module (**400**) ;
- le nombre d'inscriptions `VALIDE` atteint `nombrePlaces` (**400**) ;
- au moins un prérequis n'a pas encore d'inscription `VALIDE` pour cet étudiant (**400**).

Une inscription acceptée est enregistrée tout de suite avec le statut `VALIDE` et la date du jour. La supprimer libère une place et retire ce module des prérequis validés de l'étudiant.

Statuts possibles : `EN_COURS`, `VALIDE`, `REFUSE`, `ANNULE`. Seul `VALIDE` compte pour les places et les prérequis. L'API de création ne pose pas les autres statuts.

## Structure

```
src/main/java/com/universite/inscriptions
├── InscriptionsApplication.java
├── controller          Etudiant, Enseignant, Module, Creneau, Inscription
├── service             règles métier, @Transactional sur les écritures
├── repository          Spring Data JPA
├── mapper              entité <-> DTO
├── dto/request         corps des POST, avec @Valid
├── dto/response        corps des réponses
├── entity              Etudiant, Enseignant, Module, Creneau, Inscription
├── enums               StatutInscription
└── exception           BusinessException, ResourceNotFoundException, GlobalExceptionHandler

src/test/java/com/universite/inscriptions/service
├── InscriptionServiceTest.java    prérequis, capacité, doublon, ressources absentes
├── EtudiantServiceTest.java
├── EnseignantServiceTest.java
├── ModuleServiceTest.java
└── CreneauServiceTest.java
```

Tables MySQL : `etudiants`, `enseignants`, `modules`, `module_prerequis`, `inscriptions`, `creneaux`. `module_prerequis` relie un module (`module_id`) au module qu'il exige (`prerequis_id`).

## Démarrage

MySQL doit tourner et la base `db_inscriptions` doit exister. Puis :

```bash
mvn spring-boot:run
```

Tests unitaires (repositories simulés, sans MySQL) :

```bash
mvn test
```

## Tests Postman

Header de chaque POST : `Content-Type: application/json`.

Les identifiants ci-dessous sont ceux renvoyés si la base est vide et que les requêtes sont envoyées dans cet ordre. Si des lignes existent déjà, remplace-les par les `id` réellement renvoyés.

### 1. Enseignant

`POST http://localhost:8080/api/enseignants`

```json
{
  "nom": "Martin",
  "prenom": "Alice",
  "email": "alice.martin@univ.fr"
}
```

Attendu : **201**, par exemple `"id": 1`.

`GET http://localhost:8080/api/enseignants`  
`GET http://localhost:8080/api/enseignants/1`

Email déjà utilisé : renvoyer le même POST. Attendu : **400**, « Un enseignant avec cet email existe déjà ».

Email invalide :

```json
{
  "nom": "Martin",
  "prenom": "Alice",
  "email": "pas-un-email"
}
```

Attendu : **400**, « Format d'email invalide ».

### 2. Étudiant

`POST http://localhost:8080/api/etudiants`

```json
{
  "nom": "Bernard",
  "prenom": "Lucie",
  "email": "lucie.bernard@univ.fr",
  "niveau": "L2",
  "filiere": "Informatique"
}
```

Attendu : **201**, par exemple `"id": 1`.

`GET http://localhost:8080/api/etudiants`  
`GET http://localhost:8080/api/etudiants/1`  
`GET http://localhost:8080/api/etudiants/999` → **404**.

### 3. Modules et prérequis

Module sans prérequis :

`POST http://localhost:8080/api/modules`

```json
{
  "code": "ALGO100",
  "nom": "Algorithmique",
  "nombrePlaces": 30,
  "semestre": "S1",
  "enseignantId": 1
}
```

Attendu : **201**, `"id": 1`, `"codesPrerequis": []`, `"placesRestantes": 30`, `"nomEnseignant": "Alice Martin"`.

Deuxième module, qui exige le premier :

```json
{
  "code": "JAVA101",
  "nom": "Programmation Java",
  "nombrePlaces": 2,
  "semestre": "S2",
  "enseignantId": 1,
  "prerequisIds": [1]
}
```

`nombrePlaces` est volontairement à 2 pour le test de capacité plus bas. Attendu : **201**, `"id": 2`, `"codesPrerequis": ["ALGO100"]`.

Vérifier :

`GET http://localhost:8080/api/modules`  
`GET http://localhost:8080/api/modules/2`

Code déjà pris : renvoyer `ALGO100`. Attendu : **400**.  
Prérequis inconnu : `"prerequisIds": [999]`. Attendu : **404**, « Module prérequis introuvable ».

### 4. Créneau

`POST http://localhost:8080/api/creneaux`

```json
{
  "jour": "LUNDI",
  "heureDebut": "08:30:00",
  "heureFin": "10:00:00",
  "salle": "Amphi A",
  "moduleId": 2
}
```

Attendu : **201**.

Heure de fin avant l'heure de début (`"heureFin": "08:00:00"`) : **400**.  
Module absent (`"moduleId": 999`) : **404**.

`GET http://localhost:8080/api/creneaux`  
`GET http://localhost:8080/api/creneaux/1`

### 5. Inscription : prérequis, doublon, capacité

Lucie n'a pas encore validé `ALGO100`. Inscription directe à `JAVA101` :

`POST http://localhost:8080/api/inscriptions`

```json
{
  "etudiantId": 1,
  "moduleId": 2
}
```

Attendu : **400**, « Prérequis non validé » et le code `ALGO100`.

Valider d'abord le prérequis :

```json
{
  "etudiantId": 1,
  "moduleId": 1
}
```

Attendu : **201**, `"statut": "VALIDE"`.

Renvoyer le body vers `JAVA101` (`moduleId` 2). Attendu : **201**.  
`GET http://localhost:8080/api/modules/2` doit maintenant indiquer `"placesRestantes": 1`.

Renvoyer encore une fois le même body. Attendu : **400**, « déjà inscrit ».

`GET http://localhost:8080/api/inscriptions/etudiant/1` liste les deux inscriptions.

Créer un deuxième étudiant, lui faire valider `ALGO100`, puis l'inscrire à `JAVA101` : **201**, et `placesRestantes` passe à 0. Un troisième étudiant, après avoir validé `ALGO100`, reçoit **400**, « Capacité maximale atteinte ».

### 6. Suppressions

L'ordre compte, à cause des liens.

`DELETE http://localhost:8080/api/etudiants/1` → **400** tant qu'il a des inscriptions.  
`DELETE http://localhost:8080/api/modules/1` → **400** tant qu'il a des inscrits ou qu'il est prérequis de `JAVA101`.  
`DELETE http://localhost:8080/api/enseignants/1` → **400** tant qu'un module le référence.  
`DELETE http://localhost:8080/api/inscriptions/{id}` → **204**. Après coup, une nouvelle inscription au même module redevient possible, et une place est libérée.  
`DELETE http://localhost:8080/api/creneaux/1` → **204**.  
`DELETE` sur un id absent → **404**.

Pour vider un module ensuite : supprimer ses inscriptions, supprimer les modules qui le citent dans `prerequisIds`, puis `DELETE /api/modules/{id}`. Les créneaux restants sont supprimés avec le module.

## Vérification MySQL

```sql
USE db_inscriptions;

SHOW TABLES;

SELECT * FROM etudiants;
SELECT * FROM enseignants;
SELECT * FROM modules;
SELECT * FROM inscriptions;
SELECT * FROM creneaux;
```

Prérequis : un module et les codes qu'il exige.

```sql
SELECT m.id, m.code, m.nom, p.code AS prerequis
FROM modules m
LEFT JOIN module_prerequis mp ON mp.module_id = m.id
LEFT JOIN modules p ON p.id = mp.prerequis_id;
```

Inscriptions d'un étudiant, avec le module et le statut.

```sql
SELECT i.id, e.prenom, e.nom, m.code, m.nom, i.statut, i.date_inscription
FROM inscriptions i
JOIN etudiants e ON e.id = i.etudiant_id
JOIN modules m ON m.id = i.module_id
WHERE e.id = 1;
```

Places prises par les inscriptions `VALIDE`.

```sql
SELECT m.code, m.nombre_places,
       COUNT(i.id) AS inscrits_valides,
       m.nombre_places - COUNT(i.id) AS places_restantes
FROM modules m
LEFT JOIN inscriptions i
       ON i.module_id = m.id AND i.statut = 'VALIDE'
GROUP BY m.id, m.code, m.nombre_places;
```
