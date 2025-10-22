# BrainBrawl — Interactive Quiz Application

BrainBrawl is a JavaFX-based quiz application designed to test users’ knowledge across multiple categories such as General Knowledge and Geography.  
It provides a clean, responsive interface for users to sign up, log in securely, and attempt quizzes. User progress and scores are stored in a local SQLite database.

---

## Features

- **User Authentication**
  - Secure sign-up and login
  - Passwords hashed using **SHA-256 + salt**
  - Data stored safely in a local SQLite database
- **Quiz System**
  - Multiple categories (General, Geography, etc.)
  - Multiple-choice questions (MCQs)
  - Dynamic quiz window that tracks progress and score
- **Admin / Question Management**
  - Add, edit, or delete questions in the “Manage Questions” panel
- **Persistent Data Storage**
  - SQLite used for all data persistence
  - Tables for `users` and `questions`
- **JavaFX GUI**
  - Designed using FXML layout files and controllers
  - Modern, intuitive user interface
- **Testing**
  - JUnit 5 tests for logic and data layers
  - Mockito used to mock database access (isolating tests from DB)
- **Automation**
  - GitHub Actions CI pipeline runs build + tests automatically
  - Maven build system for dependency management and automation

---

## Architecture Overview
src/
├─ brainbrawl/
│ ├─ db/ # Database connection setup (Db.java)
│ ├─ model/ # Core data models (User, Question)
│ ├─ dao/ # Database access objects (UserDaoJdbc, QuestionDaoJdbc)
│ ├─ service/ # Business logic and validation
│ ├─ ui/ # JavaFX controllers + event handling
│ └─ Main.java # Application entry point
├─ resources/
│ ├─ fxml/ # FXML layout files for UI
│ └─ styles.css # Application styling
└─ test/
└─ brainbrawl/ # Unit tests and Mockito test cases

## Database

- Database file: `brainbrawl.db` (auto-created on first run)
- Technology: **SQLite**
- Managed through `Db.java`
- Two main tables:
  - `users`: stores username, hashed password, and scores
  - `questions`: stores question text, category, type, options, and correct answer
- To view or edit the database:
  - Open `brainbrawl.db` in **DB Browser for SQLite**

---

## Key Technical Details

| Component | Description |
|------------|--------------|
| **Database Setup** | SQLite database initialized via `Db.java` |
| **Password Hashing** | SHA-256 with unique salt applied during registration |
| **FXML Files** | Define UI layouts for login, quiz, results, etc. |
| **Controllers** | Handle user input and link GUI → logic (e.g., `LoginController.java`) |
| **Mockito Testing** | Simulates DB responses to test logic independently |
| **CI/CD** | GitHub Actions automates build & test for every push |
| **Build System** | Maven (`pom.xml`) manages dependencies and runs JUnit tests |

---

## How to Run the Application

### Requirements
- Java 21+ (Amazon Corretto or OpenJDK)
- JavaFX SDK (included in project setup)
- Maven 3.9+ (for build and run commands)

### Run from IntelliJ IDEA
1. Open the project in IntelliJ.
2. Load Maven dependencies (`pom.xml` → right-click → “Reload Project”).
3. Locate `Main.java`.
4. Right-click → **Run 'Main'**.

### Run from Command Line
```bash
mvn clean install
mvn javafx:run

### Run Tests
mvn test

## Build & Deployment
Maven compiles and packages the app into a .jar file:

mvn clean package

Output JAR file is located in /target.

GitHub Actions automatically:
- Builds the project
- Runs unit tests
- Reports build status in pull requests

Testing Overview

Testing framework: JUnit 5
Mocking library: Mockito

Test Type	Purpose
Unit Tests	Validate logic of DAOs, Services, and Controllers
Mock Tests	Replace actual DB connections with simulated responses

Common Mockito methods:
- mock() — creates a fake object for testing
- when(...).thenReturn(...) — defines behavior of mock objects
- verify() — confirms interactions occurred as expected

User Interface Overview:
- FXML Files: define the structure of each scene (e.g., Login, Quiz, Manage Questions)
- Controllers: connect logic to FXML
- Styling: defined in home.css & login.css

Example FXML screens:
- HomePage.fxml
- GeoQuiz.fxml
- GKQuiz.fxml
- Quiz.fxml

Example User Flow

Sign Up → enter username & password → password hashed and stored in DB.

Log In → credentials validated → user directed to Home screen.

Select Category → loads relevant quiz questions from DB.

Play Quiz → answers recorded → score calculated.

View Results → shows total score → “Back” button returns to main menu.

Admin Access → open Manage Questions → add/edit/delete questions.

Team Members & Roles:
Anish Madhusudhan, Developer: Set up SQLite DB, created tables for users & questions, built connection logic, ensured data persistence.
Kai Langley,	UI Developer: Designed and handled button clicks, timer animations, implemented FXML screens and and layout styling in JavaFX.
Aaqil Mohammed, Tester,	Wrote unit and integration tests, set up Mockito for DAO tests, managed Trello workflow.
Prabhanjan Muthukannan, Login Developer: Built login and sign-up system using secure password hashing, linked the FXML login and register screens with the database.
Shehroz Khan, Project Lead & Lobby developer: Coordinated sprints, oversaw documentation and CI/CD setup, made the lobby system where a player can start a quiz.

Documentation
- Javadoc: generated in /docs/javadoc/
    -Run with:
      -mvn javadoc:javadoc

- Agile Artefacts:
  -User stories, sprint plans,
  -Screenshots of Trello board and Git commits
  -Meeting minutes & stand-up summaries

- Design Evidence:
  -UI mockups
  -Test case logs and results

Walkthrough Video (10 Minutes)
- Introduction – project overview & purpose
- Features – key functionalities and must-haves
- UI Overview – walk through major screens
- Teamwork – how Agile & task management worked
- Code Overview – show DB connection, controllers, testing
- Demo – live run of app from login → quiz → result screen


