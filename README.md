USER STORIES

Section 1 - Player Management & Authentication

Story 1

- Description: Players can select which subject category they want (Maths, Geography, General Knowledge).
- User Need: “I want to choose the subject area so I can play in topics I prefer.”
- Criteria for Success: The system loads questions only from the chosen category.

Story 2

- Description: Questions are shuffled to avoid predictability.
- User Need: “I want questions to be random so each match feels unique.”
- Criteria for Success: The order of questions differs across matches, and no duplicates appear.

Story 3

- Description: No repeated questions within the same match.
- User Need: “I don’t want to see the same question twice during one game.”
- Criteria for Success: The pool checks for repeats before serving new questions.

Story 4

- Description: Answers are checked against a correct stored response.
- User Need: “I want the system to fairly mark my answer right or wrong.”
- Criteria for Success: Correct answers are validated consistently, with no errors.

Story 5

- Description: Support for multiple question types (MCQ, True/False).
- User Need: “I want variety in how questions are asked to keep the game interesting.”
- Criteria for Success: Each question is shown in the intended format, with options visible.

Story 6

- Description: Questions are displayed clearly without ambiguity.
- User Need: “I want to quickly understand what’s being asked.”
- Criteria for Success: Font, layout, and wording are unambiguous.

Story 7

- Description: Questions increase in challenge as the match continues.
- User Need: “I want the game to get harder over time to stay engaging.”
- Criteria for Success: Early questions are easier, later ones are more difficult.

Story 8

- Description: All questions are verified for correctness.
- User Need: “I want to trust that the right answers are accurate.”
- Criteria for Success: Every question-and-answer pair has been reviewed and approved.

Story 9 – Smooth Loading
- Description: Questions should appear quickly when requested.
- User Need: “I want gameplay to flow without waiting for questions to load.”
- Criteria for Success: Question loads in under two seconds.

Story 10

- Description: Both players receive the same set of questions.
- User Need: “I want the match to be fair and balanced between me and my opponent.”
- Criteria for Success: Identical questions are sent to all players in the same order.

Section 2 – Lobby and Matchmaking

Story 1

As a player, I want to create a game lobby so that I can invite my friends to join.

Acceptance Criteria:
- Given I am logged into the game
- When I select “Create Lobby”
- Then a new lobby should be created with a unique lobby ID.

Story 2

As a player, I want to join an existing lobby using a code or invite link so that I can play with friends.

Acceptance Criteria:
- Given a lobby already exists
- When I enter a valid lobby code or click an invite link
- Then I should be added to that lobby.

Story 3

As a player, I want to see a list of all players in the lobby so that I know who I am playing with.

Acceptance Criteria:
- Given I am inside a lobby
- When other players join or leave
- Then the player list should update in real time.

Story 4

As a player, I want the lobby host to be able to start the match so that gameplay begins when everyone is ready.

Acceptance Criteria:
- Given I am the host of a lobby
- When I press “Start Game”
- Then the system should transition all players from the lobby into the game.

Story 5

As a player, I want to use matchmaking to automatically find a game with players of similar skill so that matches feel fair.

Acceptance Criteria:
- Given I select “Quick Match”
- When the system searches for players
- Then I should be placed into a game lobby with players of similar skill or ranking.

User Story 6

As a player, I want to see the lobby chat so that I can communicate with others before the game starts.

Acceptance Criteria:
- Given I am in a lobby
- When I or another player sends a message
- Then the message should appear instantly for all lobby members.

User Story 7

As a player, I want to see lobby settings (e.g., game mode, difficulty, max players) so that I understand the rules before the match starts.

Acceptance Criteria:
- Given I am inside a lobby
- When the host configures lobby settings
- Then those settings should be visible to all players in the lobby.

Section 3 - Question Management

Story 1

As a player, I want my match results to be saved automatically so I don’t lose progress.

Acceptance Criteria:
- Given I have completed a game
- When the game ends
- Then my match results should automatically be saved to the database without needing manual input.

Story 2

As a player, I want to view all my past matches so I can reflect on my performance.

Acceptance Criteria:
- Given I have played previous matches
- When I open the match history screen
- Then I should see a list of all past matches in chronological order.

Story 3

As a player, I want to see detailed stats from each past game.

Acceptance Criteria:
- Given I have selected a past match from my history
- When the match details are displayed
- Then I should see my score, accuracy, and opponent’s details for that game.

Story 4

As a player, I want my wins and losses to be recorded so I can track my progress.

Acceptance Criteria:
- Given I have completed matches with different results
- When I view my overall statistics
- Then my total number of wins and losses should be displayed.

User Story 5

As a player, I want my average score across matches to be displayed.

Acceptance Criteria:
- Given I have completed one or more matches
- When I check my statistics
- Then my average score should be displayed.

Story 6

As a player, I want my personal best scores highlighted so I can celebrate milestones.

Acceptance Criteria:
- Given I have achieved a new personal best in a category
- When I view my match history
- Then the personal best score should be highlighted for that category.

Story 7

As a player, I want to filter stats by category so I can focus on specific subjects.

Acceptance Criteria:
- Given I have matches across multiple categories
- When I select a category filter (e.g., Maths, Geography, General Knowledge)
- Then only matches from that category should be displayed.

Story 8

As a player, I want to see stats by opponent so I can compare performance.

Acceptance Criteria:
- Given I have played against different opponents
- When I view opponent-specific stats
- Then I should see my results and performance against each opponent.

Story 9

As a player, I want to see my winning streaks, so I stay motivated.

Acceptance Criteria:
- Given I have won consecutive matches
- When I check my profile or stats
- Then my current winning streak should be displayed.

Story 10

As a player, I want a summary of my weekly or monthly performance.

Acceptance Criteria:
- Given I have completed matches over time
- When I select a weekly or monthly view
- Then a summary of matches played, wins, losses, and scores for that period should be displayed.

Section 4 - Gameplay and Timing User Stories

Story 1

As a player, I want an uncluttered interface during a game so I can focus on each question.

Acceptance criteria:
-	Given the player is in a quiz round
-	When the system displays a question
-	Then the question is the focal point and only other gameplay/essential elements are visible on the UI

Story 2

As a player, I want a question countdown timer or obvious visual indicators so that I am always aware of how much time I have.

Acceptance criteria:
-	Given a question is active
-	When the system starts the round countdown
-	Then a visible timer or indicator is displayed to both players with effect changes when low

Story 3

As a player, I want clarity on the answer inputs and submission controls so I can quickly and easily answer questions.

Acceptance criteria:
-	Given a round is active
-	When the system provides the answer interface elements
-	Then the options/input are clearly visible and easily understood

Story 4

As a player, I want instant feedback if an answer is successfully submitted and whether it is correct or incorrect so that I always know how I am performing.

Acceptance criteria:
-	Given a round is active
-	When the player submits a selected/input answer
-	Then the system immediately provides visual indication whether the answer is correct/incorrect

Story 5

As a player, I want clear indication of mine and the opponent's answering progress so that I am aware of how each round is going.

Acceptance criteria:
-	Given the two players are in a round
-	When either player submits an answer
-	Then the system updates a visual indicator to show players answered/pending status for the round

Story 6

As a player, I want an automatic game flow that moves on from each round when both players are done so that time is not wasted and there is a smooth gameplay experience.

Acceptance criteria:
-	Given a round is active
-	When both players have answered or the round timer expires
-	Then automatically complete the round and transition both players towards the next round

Story 7

As a player, I want to know the correct answer after each round so that I understand my mistakes.

Acceptance criteria:
-	Given a round ended
-	When the game transitions
-	Then briefly show the correct answer

Story 8

As a player, I want clear transition visuals between stages so that I won’t misunderstand at any point or input accidental answers.

Acceptance criteria:
-	Given a round ended
-	When the system transitions between stages 
-	Then display visuals that clarify the stage change

Story 9

As a player, I want a brief countdown before new questions so that if I answered last, I could have a mental break to keep up with the games flow for fairness.

Acceptance criteria:
-	Given a game is active
-	When the system starts a new round
-	Then display a brief synchronised countdown to both players before displaying the new question

Story 10

As a player, I want minimal delay or desynchronisation between players and the system so I can be confident in the game’s fairness and consistency.

Acceptance criteria:
-	Given two players are connected to an active game
-	When the system delivers game elements/functions
Then the system should detect disconnections and enforce consistency between interfaces despite latency differences

Section 5 – User stories for Scoring and Results

Story 1
As a player, I want to earn points for correct answers so that my performance is rewarded fairly.

Acceptance Criteria:
-	Given I answer a question correctly
-	When the system checks my response
-	Then points should be added to my score based on accuracy and speed.

Story 2

As a player, I want to lose points for incorrect answers so that accuracy is prioritized over guessing.

Acceptance Criteria:
-	Given I select a wrong answer
-	When the system calculates scores
-	Then my total points should decrease accordingly.

Story 3

As a player, I want to earn extra points for answering faster so that quick thinking is rewarded in addition to accuracy.

Acceptance Criteria:
-	Given two players answer correctly
-	When one player answers faster
-	Then that player should receive a speed bonus.

Story 4

As a player, I want to see a live scoreboard during the match so that I know my progress compared to my opponent.

Acceptance Criteria:
-	Given both players are answering questions
-	When scores are updated
-	Then the live scoreboard should reflect the current points instantly.

Story 5

As a player, I want to play a tiebreaker question if scores are equal so that a clear winner is determined fairly.

Acceptance Criteria:
-	Given both players finish with the same score
-	When the match ends
-	Then a sudden-death question should appear until one player wins.

Story 6

As a player, I want the system to declare the winner automatically so that I know the outcome without disputes.

Acceptance Criteria:
-	Given the game ends
-	When the scores are finalized
-	Then the system should display the winner’s name and score.

Story 7

As a player, I want to see the results summary after the match so that I can review my performance.

Acceptance Criteria:
-	Given the game ends
-	When results are displayed
-	Then I should see my total score, accuracy %, and win/loss status.

Story 8

As a player, I want to save my past match results so that I can track progress over time.

Acceptance Criteria:
-	Given a match is completed
-	When results are finalized
-	Then they should be stored in my match history with opponent name and date.

Story 9

As a player, I want to unlock achievement badges for reaching milestones so that I feel motivated to improve and keep playing.

Acceptance Criteria:
-	Given I achieve a milestone (e.g., 5 consecutive correct answers, highest score in a week)
-	When the system checks my progress
-	Then I should be awarded a visible badge that is stored in my profile.

Story 10

As a player, I want to view detailed analytics of my performance (strengths and weaknesses by category) so that I can identify areas to improve.

Acceptance Criteria:
-	Given the match has ended
-	When results are displayed
- Then I should see a breakdown of my correct/incorrect answers by topic, average response time, and improvement suggestions.


Section 6 

Story 1

As a player, I want my match results to be saved automatically to the database so that I don’t lose my progress after finishing a game.

Acceptance Criteria:
- Given a match has concluded,
- When the end-of-game event fires, 
- Then a single match record (player IDs, opponent, category, score, accuracy, timestamp, outcome) is stored.

story 2

As a returning player, I want to view all my past matches so that I can reflect on performance over time.

Acceptance Criteria:
- Given I’m authenticated
- When I open Match History 
- Then I can see all my matches that I won, loss and the result.

Story 3

As a  player, I want detailed stats for each past game (score, accuracy, opponent) so that I can analyse individual performance.

Acceptance Criteria:
- Given a history list
- When I select a match,
- Then a details view shows score, accuracy, opponent, questions and correct/wrong answers 

story 4

As a player, I want my total wins and losses recorded so that I can track progress.

Acceptance Criteria:
- Given a match finishes,
- When the outcome is determined, 
- Then my wins or losses counter updates atomically.

 Story 5 
 
 As a player, I want my average score displayed so that I can measure consistency.

Acceptance Criteria:
- Given more than 1 match has been completed
- When I view stats, 
- Then average score = total score ÷ number of matches 


story 6

As a  player, I want personal bests highlighted per category so that I can celebrate milestones and aim to beat them.

Acceptance Criteria:
- Given history data,
- When viewing category stats, 
- Then the highest score in each category is labeled “PB”.

story 7 
As a player, I want to filter my stats by category (Maths, Geography, General Knowledge) so that I can target improvements.

Acceptance Criteria:
- Given categories exist, 
- When I apply one or more category filters,
- Then the history list and aggregates update to only those categories.

story 8 
As a player, I want opponent comparison stats so that I can see performance against different players

Acceptance Criteria:
- Given im versing an Opponent 
- When I select an opponent, 
- Then I see how many wins and losses they have and there average score 

story 9
As a player, I want to see my current and best winning streaks so that I stay engaged.

Acceptance Criteria:
 - Given my match history
 - When I open Streaks
 - Then the current win streak and best win streak are displayed.

story 10 

As a long-term tracker, I want weekly or monthly summaries so that I can monitor improvement trends.

Acceptance Criteria:
- Given a selected period (Weekly/Monthly) 
- When I open Summary
- Then it shows matches played, Win-Losses average score, and average accuracy for that period.



