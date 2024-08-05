# PokeShowDown

## Overview
PokeShowDown is an Android application that allows two players to battle with randomly assigned Pokémon. The app fetches Pokémon data from an API and provides a dynamic battling experience with type advantages and disadvantages.

## Features
- **Random Pokémon Assignment:** Each player is assigned a random Pokémon from a fetched list.
- **Next and Back Buttons:** Players can cycle through Pokémon using "Next" and "Back" buttons.
- **Battle Readiness:** Players confirm their readiness to battle, disabling further changes to their Pokémon.
- **Type Advantage System:** The battle system incorporates type advantages and disadvantages, affecting the outcome of battles.
- **Dynamic Pokémon List:** When the list of available Pokémon is exhausted, a new list is fetched from the API.

## Technologies Used
- **Kotlin:** Primary programming language for the Android app.
- **Retrofit:** For making API calls to fetch Pokémon data.
- **Glide:** For loading Pokémon images.
- **Coroutines:** For handling asynchronous operations.

## How It Works
### Landing Screen
- Displays the app title letter by letter and fetches a list of random Pokémon.

### Main Activity
- Players are assigned random Pokémon.
- Players can use "Next" and "Back" buttons to change their Pokémon.
- Players confirm their readiness to battle.
- The battle system calculates the winner based on Pokémon stats and type advantages.
- Scores are updated, and new Pokémon are assigned for the next round.

## Installation
1. Clone the repository.
2. Open the project in Android Studio.
3. Build and run the app on an Android device or emulator.

## API
The app uses a Pokémon API to fetch data. Ensure you have network access to retrieve Pokémon information.

## Contributing
Contributions are welcome! Please fork the repository and submit pull requests.
