# BrainBrawl — Interactive Quiz Application

BrainBrawl is a JavaFX-based quiz application designed to test users’ knowledge across multiple categories such as General Knowledge and Geography. It provides a clean, responsive interface for users to sign up, log in securely, and attempt quizzes. User progress and scores are stored in a local SQLite database.

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

BrainBrawl/
│
├── .github/                
│   └── workflows/
│       └── ci.yml          # Continuous Integration build script
│
├── .idea/                  
│   └── libraries/           # IntelliJ project config and dependencies
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── brainbrawl/
│   │   │       ├── dao/             # Data Access Layer (DB interaction)
│   │   │       │   ├── QuestionDao.java
│   │   │       │   ├── QuestionDaoJdbc.java
│   │   │       │   ├── ResultDao.java
│   │   │       │   ├── ResultDaoJdbc.java
│   │   │       │   ├── UserDao.java
│   │   │       │   └── UserDaoJdbc.java
│   │   │       │
│   │   │       ├── db/              # Database setup and connection
│   │   │       │   └── Db.java
│   │   │       │
│   │   │       ├── model/           # Application data models (POJOs)
│   │   │       │   ├── GameResult.java
│   │   │       │   ├── Question.java
│   │   │       │   └── User.java
│   │   │       │
│   │   │       ├── service/         # Core business logic and validation
│   │   │       │   ├── AppServices.java
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── QuestionService.java
│   │   │       │   └── ResultService.java
│   │   │       │
│   │   │       └── ui/              # JavaFX controllers and UI logic
│   │   │           ├── GeoMCQController.java
│   │   │           ├── HomeController.java
│   │   │           ├── LoginApp.java
│   │   │           ├── Main.java
│   │   │           ├── ManageQuestionsApp.java
│   │   │           ├── QuizController.java
│   │   │           ├── QuizMCQController.java
│   │   │           ├── RegisterController.java
│   │   │           └── RegisterDialog.java
│   │   │
│   │   └── resources/
│   │       └── BrainBrawl/
│   │           ├── styles/
│   │           │   └── home.css
│   │           ├── views/
│   │           │   ├── GeoQuiz.fxml
│   │           │   ├── GKQuiz.fxml
│   │           │   └── Quiz.fxml
│   │           ├── brainbrawl-logo.png
│   │           ├── HomePage.fxml
│   │           └── login.css
│   │
│   └── test/
│       └── brainbrawl/
│           ├── dao/
│           │   ├── DbTestUtil.java
│           │   └── QuestionDaoJDBCTest.java
│           ├── model/
│           │   └── QuestionTest.java
│           ├── service/
│           │   └── QuestionServiceTest.java
│           └── ui/
│               ├── ManageQuestionsAppTest.java
│               └── SmokeAuthTest.java
│
├── target/                 # Maven build output (compiled classes, JAR, test reports)
├── brainbrawl.db           # SQLite database file
├── pom.xml                 # Maven configuration and dependencies
└── .gitignore

markdown
Copy code

---

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
- ```bash
  mvn clean install
  mvn javafx:run

### Run Tests
- ```bash
  mvn test

---

## Build & Deployment

Maven compiles and packages the app into a .jar file:
- ```bash
  mvn clean package

Output JAR file is located in /target.

GitHub Actions automatically:
- Builds the project
- Runs unit tests

---

## Testing Overview

Testing framework: JUnit 5
Mocking library: Mockito

Test Type	Purpose
Unit Tests	Validate logic of DAOs, Services, and Controllers
Mock Tests	Replace actual DB connections with simulated responses

Common Mockito methods:

- mock() — creates a fake object for testing

- when(...).thenReturn(...) — defines behavior of mock objects

- verify() — confirms interactions occurred as expected

---

## User Interface Overview

FXML Files: 
- Defines the structure of each scene (e.g., Login & Quiz)

Controllers: 
- Connects logic to FXML

Styling: 
- Defined in login.css & home.css

Example FXML screens:

- GeoQuiz.fxml

- GKQuiz.fxml

- HomePage.fxml

---

## Example User Flow

1. Sign Up → enter username & password → password hashed and stored in DB.

2. Log In → credentials validated → user directed to Home screen.

3. Select Category → loads relevant quiz questions from DB.

4. Play Quiz → answers recorded → score calculated.

5. View Results → shows total score → “Back” button returns to main menu.

6. Admin Access → open Manage Questions → add/edit/delete questions.

---

## Team Members and Contributions
Name	Contribution
- Aaqil Mohammed:
  - Created test cases for login and quiz functions. Helped debug errors and confirm feature functionality. Managed Trello tasks and contributed to final slides and report.
- Anish Madhusudhan:
  - Set up the SQLite database and created tables for users and questions. Implemented database connectivity and user data handling. Ensured data persistence and integrity.
- Kai Langley:
  - Designed main quiz and results screens in JavaFX. Handled button clicks, timer animations, and UI styling. Fixed layout bugs and tested transitions between screens.
- Prabhanjan Muthukannan:
  - Built login and registration system using secure password hashing. Linked FXML screens with the database and tested user authentication flow.
- Shehroz Khan:
  - Developed the quiz lobby system. Implemented question timers, score updates, and round logic to manage quiz progression.
