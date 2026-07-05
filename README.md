# Net21 🃏

A networked, multiplayer **21 (Blackjack-style)** card game written in Java. Players
connect to a central server over TCP, join a waiting room, and race across five rounds
to build the highest hand without going over 21.

Built with Java sockets for the networking layer and a Java Swing GUI for the client.

## Gameplay

- Launch the client, pick a **username**, and connect to the server.
- Join the **waiting room**. A game needs **2–4 players**.
  - When **4 players** join, the game starts immediately.
  - With **2 or 3 players**, a **10-second countdown** starts, then the game begins.
- Each game runs **5 rounds**. In every round you take turns:
  - **Hit** — draw another card and add its value to your score.
  - **Pass** — keep your current score and stop drawing.
- A per-round countdown timer keeps the pace moving.
- After the final round, the **leaderboard** shows the ranked scores.

### Scoring

The goal is to get as close to **21** as possible without going over. Card values are:

| Card        | Value |
| ----------- | ----- |
| Ace         | 1     |
| 2 – 10      | face value |
| Jack        | 11    |
| Queen       | 12    |
| King        | 13    |

Go over 21 and you **bust** — busted players are ranked below everyone still at or under 21.

## Project structure

```
Network/
├── pom.xml
└── src/main/
    ├── java/Net21/network/
    │   ├── Server.java          # Accepts connections, runs the game loop, timers & rounds
    │   ├── ClientHandler.java   # Per-player server-side thread (hit/pass/join/leave)
    │   ├── Client.java          # Swing GUI client
    │   ├── Client.form          # NetBeans GUI form
    │   ├── Listener.java        # Client-side thread listening for server messages
    │   ├── Card.java            # A single card (suit, rank, value, image)
    │   └── Deck.java            # 52-card deck, shuffling and drawing
    └── resources/
        └── images/              # Card faces (Hearts/Diamonds/Clubs/Spades) & backgrounds
```

## Requirements

- **Java 17** or later
- **Maven** (for building)

## Build

From the `Network/` directory:

```bash
cd Network
mvn clean package
```

## Running

The game needs **one server** and **one or more clients**. The client connects to
`localhost:2121` by default (see [Client.java](Network/src/main/java/Net21/network/Client.java#L105)).

### 1. Start the server

```bash
cd Network
mvn exec:java -Dexec.mainClass=Net21.network.Server
```

You should see `Server started on port 2121...`.

### 2. Start a client (repeat for each player)

```bash
cd Network
mvn exec:java -Dexec.mainClass=Net21.network.Client
```

Run this in a separate terminal for each player (2–4 players per game). Enter a
username, connect, and join the waiting room.

> **Note:** The client connects to `localhost`. To play across different machines,
> update the host in `Client.connectToServer()` to the server's IP address.

## How it works

- The **server** listens on port **2121** and spawns a `ClientHandler` thread for each
  connected player.
- Players send simple text commands (`join`, `hit`, `pass`, `leave game`, `exit`) and
  the server broadcasts game state (player lists, timers, rounds, scores) back to all
  clients.
- The **client** runs a background `Listener` thread that reads server messages and
  updates the Swing UI (waiting room, round panel, leaderboard).

## Authors

Group 8 — Net21
</content>
</invoke>
