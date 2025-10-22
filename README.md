BrainBrawl — Self-Revision Quiz Application
Overview

BrainBrawl is a JavaFX-based self-revision quiz application developed as part of a university software engineering project.
The goal of the project was to create an educational app that allows users to test their knowledge across multiple categories, track scores, and improve through repeated quizzes.

The project demonstrates:

Application of object-oriented design and software architecture principles

Use of JavaFX for GUI design

Integration of a SQLite database for persistent user and quiz data

Implementation of test-driven development (TDD)

Collaboration using Agile methodology, GitHub, and Trello

Tech Stack
Layer	Technology
Language	Java (Amazon Corretto 21)
Framework	JavaFX
Database	SQLite (JDBC)
Build Tool	Maven
Version Control	Git + GitHub
IDE	IntelliJ IDEA
Project Management	Trello
CI/CD	GitHub Actions

Architecture and Project Structure

BrainBrawl follows a modular MVC-inspired architecture, which separates data, logic, and presentation layers.
This makes the system easier to test, extend, and maintain.

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

Key Features

User Authentication: Secure login and registration using SHA-256 password hashing with salt.

Quiz Management: Multiple categories, question types, and difficulty levels.

Data Persistence: All data (users, questions, scores) stored in SQLite.

Modular Architecture: DAO, service, and UI layers separated for clarity and scalability.

Unit Testing: Implemented with JUnit 5 and Mockito for isolated testing.

Continuous Integration: Automated builds and tests using GitHub Actions.

Agile Workflow: Managed tasks and sprints via Trello and GitHub Projects.

Testing

Testing was implemented using JUnit 5 and Mockito to validate the core business logic and database interactions.

Unit tests verify key components such as question addition, authentication, and DAO behavior.

Mockito was used to mock the database, ensuring tests run independently of the actual SQLite file.

Example: QuestionServiceTest.java validates input handling and DAO integration using mock(), verify(), and when().

Build and Run Instructions
Prerequisites

Java 21 (Amazon Corretto or OpenJDK)

Maven 3.9+

IntelliJ IDEA (recommended)

Build and Run
# Clone repository
git clone https://github.com/YourTeam/BrainBrawl.git

# Navigate into project directory
cd BrainBrawl

# Build project
mvn clean install

# Run JavaFX app
mvn javafx:run

Javadoc

All major classes and methods include Javadoc comments for clarity and maintainability.
You can generate documentation using:

mvn javadoc:javadoc


This will generate HTML files under target/site/apidocs/.

Team Members and Contributions
Name	Contribution
Aaqil Mohammed	Created test cases for login and quiz functions. Helped debug errors and confirm feature functionality. Managed Trello tasks and contributed to final slides and report.
Anish Madhusudhan	Set up the SQLite database and created tables for users and questions. Implemented database connectivity and user data handling. Ensured data persistence and integrity.
Kai Langley	Designed main quiz and results screens in JavaFX. Handled button clicks, timer animations, and UI styling. Fixed layout bugs and tested transitions between screens.
Prabhanjan Muthukannan	Built login and registration system using secure password hashing. Linked FXML screens with the database and tested user authentication flow.
Shehroz Khan	Developed the quiz lobby system. Implemented question timers, score updates, and round logic to manage quiz progression.

Walkthrough Video (to include)

Project overview and goals

User story prioritization and key features

Demonstration of the JavaFX interface

Explanation of database persistence

Code walkthrough (DAO + Service + Controller)

Evidence of testing and CI

Application demo (login, quiz, results)
