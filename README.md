# JavaFX-COINCHE 🕹️

> Play Coinche with your friends online!
> 
> Jump to the [Setup](#setup) section to to download the game, enjoy!
> 
> Discover [How to play](#how-to-play) and give me your [Feedback](#feedback) on this project.
> 
> You can find 5 more board games in the [Collection](#collection) section, give them a try!

## Table of contents
* [The second challenge](#the-second-challenge)
* [Game Charachteristics](game-charachteristics)
* [How to play](#how-to-play)
* [Setup](#setup)
* [Collection](#collection)
* [Project dependencies](#project-dependencies)
* [Additional info](#additional-info)
* [Feedback](#feedback)

# The second challenge
This project is the **fifth** learning project of my collection.

Card games such as Coinche, UNO, [Salad](https://www.pagat.com/compendium/canadian_salad.html)... 
have complex rules which translates to a difficult logic to implement, 
check out [this](https://www.belote.com/regles-et-variantes/regle-belote-coinche/) 
french web page to see coinche rules; however, points calculation has minor differences because of personal preferences (how I play it with friends).

# Game Charachteristics
- Turn based
- Multiplayer (no AI)
- Tabletop view (2D)
- Distributed (client/server)
- Socket programming (TCP)
- Message oriented communication (except game app)
- Supports multiple resolutions thanks to JavaFX scaling
- Supports fullscreen mode
- Supports All trumps/No trumps bids
- coinche/surcoinche anytime the last bid

# How to play
![Main app gui](./screenshots/mainApp.png)

After connecting to the server, the main app gui is presented to the player, 
allowing him to enter any username, then either Host, Join a specific room through its ID, 
or Join public rooms.

![Join app gui](./screenshots/joinApp.png)

If the player chooses to play with random people, he will be presented with a list of public rooms

![Room app gui](./screenshots/roomApp.png)

After joining a **room** the player is presented with this gui, where he can:
- Start a **chat** with people already in the room
- Change his **name**
- Take an empty **place**
- Set his **ready** status
- **Team up** with another player (with his approval)

The host has in addition the ability to:
- Change the room's **privacy** to either public/private
- **Kick** someone out of the room
- **Start** the game

Host privileges are **passed** automatically to the next player if the host leaves the room

![Buy phase](./screenshots/buy_phase.png)

The game works as a regular coinche game but in a distributed environment, 
you can end the game without leaving the room using the **Return to..** menu at the top.

![Play phase](./screenshots/play_phase.png)

After a successfull buying phase, 
players will announce their declarations and reveal them in the third round
Hold Tab to see previous round's cards.

# Setup
Describe how to install / setup your local environement / add link to demo version.
Portable exe file in setup folder

# Collection
- [XO](https://github.com/BHA-Bilel/JavaFX-XO)
- [Checkers](https://github.com/BHA-Bilel/JavaFX-CHECKERS)
- [Chess](https://github.com/BHA-Bilel/JavaFX-CHESS)
- [Connect4](https://github.com/BHA-Bilel/JavaFX-CONNECT4)
- [Dominoes](https://github.com/BHA-Bilel/JavaFX-DOMINOS)
- Coinche (current)

# Project dependencies
- **Java**-15.0.1
- **JavaFX**-11.0.2
- **controlsfx**-11.0.0
- **jfoenix**-9.0.10

# Additional info
- This project was developed intermittently due to other preoccupations, 
that's why I can't tell how much time it took me to complete it.
All the collection was initially developed in **Eclipse IDE** in late 2019, 
before I migrated to **Intellij IDEA** in 2021 to code the remaining parts
while redesigning some parts to fit the new workflow model.

- This project wasn't my first nor my last experience coding in JavaFX, 
I'll do my best to publish other projects on my GitHub.

- **All** of the projects/repositories (so far) in my profile are produced by an **individual** effort, 
that I coded from **scratch**.

# Feedback
What do you think of this project? leave your thoughts/recommendations !

<p align="center">
  <a href="https://gist.github.com/BHA-Bilel/1323a0c645a5d7df5db2ac8f2d79fef9">
    <img src="https://gist.githubusercontent.com/BHA-Bilel/6eb01c298f0ccceff7511427afb52534/raw/bfaf1c70946579fe9f4991d923215d7f402de250/bguestbook.gif" 
    alt="Click here to sign my guestbook!">
  </a>
</p>
