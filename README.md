# 🏓 Pong - Tennis Player Registration API

A RESTful API built with **Spring Boot 4** and **H2 in-memory database** for managing tennis player registrations.

## 📋 Features

- **Register** a new tennis player
- **Get** a player by ID
- **List** all registered players
- **Delete** a player by ID
- Input **validation** (name, email, ranking)
- Duplicate email detection
- **Swagger UI** for interactive API testing
- **H2 Console** for database inspection

## 🛠️ Tech Stack

| Technology       | Version |
|-----------------|---------|
| Java            | 25      |
| Spring Boot     | 4.0.3   |
| Spring Data JPA | —       |
| H2 Database     | —       |
| SpringDoc OpenAPI| 2.8.6  |
| JUnit 5         | —       |
| Mockito         | —       |

## Architecture
```mermaid
graph TD

%% Layers
subgraph Controllers
    ControllerA[REST Controller A]
    ControllerB[REST Controller B]
end

subgraph Services
    ServiceA[Service A]
    ServiceB[Service B]
end

subgraph Repositories
    RepoA[JPA Repository A]
    RepoB[JPA Repository B]
end

subgraph Database
    H2["(H2 In-Memory DB)"]
end

%% Flows between layers
ControllerA --> ServiceA
ControllerB --> ServiceB
ControllerB -.-> ServiceA

ServiceA --> RepoA
ServiceA -.-> RepoB
ServiceB --> RepoB

RepoA --> H2
RepoB --> H2
```

## 🚀 Getting Started

### Prerequisites

- **Java 25** (or compatible JDK)

### Run the Application

```bash
./gradlew bootRun
```

The application starts on **http://localhost:8080**.

### Run Tests

```bash
./gradlew test
```

## 📖 API Endpoints

| Method   | Endpoint             | Description              |
|----------|---------------------|--------------------------|
| `POST`   | `/api/players`      | Register a new player    |
| `GET`    | `/api/players/{id}` | Get a player by ID       |
| `GET`    | `/api/players`      | Get all players          |
| `DELETE` | `/api/players/{id}` | Delete a player by ID    |

### Request Body (POST /api/players)

```json
{
  "firstName": "Roger",
  "lastName": "Federer",
  "email": "roger.federer@tennis.com",
  "country": "Switzerland",
  "ranking": 3
}
```

### Validation Rules

| Field       | Rules                                          |
|------------|------------------------------------------------|
| `firstName`| Required, 2–50 characters                      |
| `lastName` | Required, 2–50 characters                      |
| `email`    | Required, must be a valid email, must be unique |
| `country`  | Optional, max 100 characters                   |
| `ranking`  | Optional, must be between 1 and 10 000          |

### Response Example

```json
{
  "id": 1,
  "firstName": "Roger",
  "lastName": "Federer",
  "email": "roger.federer@tennis.com",
  "country": "Switzerland",
  "ranking": 3
}
```

### Error Response Example

```json
{
  "timestamp": "2026-03-18T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "firstName: First name is required, email: Email must be valid"
}
```

## 🧪 Testing with Swagger UI

1. Start the application:
   ```bash
   ./gradlew bootRun
   ```

2. Open **Swagger UI** in your browser:
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. You will see all available endpoints grouped under **Tennis Players**.

4. **To register a player:**
   - Expand `POST /api/players`
   - Click **"Try it out"**
   - Edit the JSON body with player details
   - Click **"Execute"**
   - See the `201 Created` response with the player data

5. **To get all players:**
   - Expand `GET /api/players`
   - Click **"Try it out"** → **"Execute"**

6. **To get a player by ID:**
   - Expand `GET /api/players/{id}`
   - Click **"Try it out"**
   - Enter the player ID
   - Click **"Execute"**

7. **To delete a player:**
   - Expand `DELETE /api/players/{id}`
   - Click **"Try it out"**
   - Enter the player ID
   - Click **"Execute"**
   - See the `204 No Content` response

## 🗄️ H2 Database Console

You can inspect the database directly:

1. Open **http://localhost:8080/h2-console**
2. Use the following settings:
   - **JDBC URL:** `jdbc:h2:mem:pongdb`
   - **User Name:** `sa`
   - **Password:** *(leave empty)*
3. Click **Connect**
4. Run SQL queries, e.g.: `SELECT * FROM PLAYERS;`

## 📁 Project Structure

```
src/main/java/com/dynatrace/pong/
├── PongApplication.java              # Application entry point
├── controller/
│   └── PlayerController.java         # REST controller with validation
├── dto/
│   ├── PlayerRequest.java            # Input DTO with validation annotations
│   └── PlayerResponse.java           # Output DTO
├── exception/
│   ├── DuplicateEmailException.java  # Thrown when email already exists
│   ├── GlobalExceptionHandler.java   # Centralized error handling
│   └── PlayerNotFoundException.java  # Thrown when player not found
├── model/
│   └── Player.java                   # JPA entity
├── repository/
│   └── PlayerRepository.java         # Spring Data JPA repository
└── service/
    └── PlayerService.java            # Business logic layer

src/test/java/com/dynatrace/pong/
├── PongApplicationTests.java         # Context load test
├── controller/
│   └── PlayerControllerTest.java     # Controller unit tests (MockMvc)
├── repository/
│   └── PlayerRepositoryTest.java     # Repository integration tests
└── service/
    └── PlayerServiceTest.java        # Service unit tests (Mockito)
```

## 🆕 Tournament Scoring Addendum

The tournament now also supports recording ping pong match results and generating standings from those recorded matches.

### Additional Features

- Record a completed ping pong match between two registered players
- Calculate standings from the full match history
- Prevent deleting a player if that player already has recorded matches
- Validate ping pong game scores before saving a result

### Additional API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/matches` | Record a ping pong match result |
| `GET`  | `/api/standings` | Get tournament standings |

### Match Request Example

```json
{
  "playerOneId": 1,
  "playerTwoId": 2,
  "playerOneScore": 11,
  "playerTwoScore": 8
}
```

### How Score Is Counted

Each saved match represents one ping pong game.

- A match must contain two different players
- A match cannot end in a tie
- The winner must score at least 11 points
- The winner must lead by at least 2 points
- `11-7` is valid
- `12-10` is valid
- `11-10` is invalid because the lead is only 1 point
- `10-8` is invalid because the winner did not reach 11

### How Standings Are Calculated

Standings are computed dynamically from all recorded matches.

- Win = 3 tournament points
- Loss = 0 tournament points
- `pointsFor` = total points scored by the player
- `pointsAgainst` = total points conceded by the player
- `scoreDifference` = `pointsFor - pointsAgainst`

Standings are ordered by:

1. Tournament points descending
2. Score difference descending
3. Points scored descending
4. Wins descending
5. Last name, first name, then player id for stable ordering

### Additional Testing

The new scoring system is covered with tests for:

- Match result validation rules
- Standings aggregation and ordering
- Match and standings controller endpoints
- Match repository behavior
- Player deletion guard when match history exists

### Additional Project Structure

```
src/main/java/com/dynatrace/pong/
├── controller/
│   └── TournamentController.java         # Match and standings endpoints
├── dto/
│   ├── MatchRequest.java                 # Input DTO for match results
│   ├── MatchResponse.java                # Output DTO for saved matches
│   └── StandingResponse.java             # Output DTO for standings rows
├── exception/
│   ├── InvalidMatchResultException.java  # Thrown for invalid ping pong scores
│   └── PlayerHasMatchesException.java    # Thrown when deleting a player with match history
├── model/
│   └── TournamentMatch.java              # Stored tournament match entity
├── repository/
│   └── TournamentMatchRepository.java    # Match persistence
└── service/
    └── TournamentService.java            # Match validation and standings logic

src/test/java/com/dynatrace/pong/
├── controller/
│   └── TournamentControllerTest.java         # Controller tests for new endpoints
├── repository/
│   └── TournamentMatchRepositoryTest.java    # Repository test for match lookup rules
└── service/
    └── TournamentServiceTest.java            # Service tests for scoring and standings
```
