# CommandBack

A small Fabric mod for **Minecraft Java 1.21.11** that restores numeric command arguments and adds legacy shortcuts.

## Commands

### Game modes
- `/gamemode 0|1|2|3 [targets]`
- `/gamemode s|c|a|sp [targets]`
- `/gm 0|1|2|3 [targets]`
- `/gm s|c|a|sp [targets]`
- `/gms [targets]`, `/gmc [targets]`, `/gma [targets]`, `/gmsp [targets]`

### Default game mode
- `/defaultgamemode 0|1|2|3`
- `/dgm 0|1|2|3`

### Difficulty
- `/difficulty 0|1|2|3`

### Weather
- `/toggledownfall` toggles between clear and rain.

Numeric mappings:
- Game mode: 0 Survival, 1 Creative, 2 Adventure, 3 Spectator.
- Difficulty: 0 Peaceful, 1 Easy, 2 Normal, 3 Hard.

All commands use vanilla command execution and therefore retain Minecraft's normal permission checks. Commands that accept targets support vanilla selectors such as `@a`, `@p`, and player names.

## Build

The GitHub Actions workflow builds with Java 21 and Gradle 9.2.1. Open the **Actions** tab and download the `CommandBack-Minecraft-1.21.11-Fabric` artifact from a successful run.

The distributable mod is the remapped JAR in `build/libs`. Install it with Fabric Loader 0.18.4 or newer and Fabric API for Minecraft 1.21.11.

## License

MIT
