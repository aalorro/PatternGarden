# Square Garden

A tile-swap puzzle game for Android built with Kotlin and Jetpack Compose.

## Gameplay

Slide tiles to swap them with adjacent neighbors and form color patterns to complete level goals. Use fewer moves to earn more stars.

### Goal Types
- **Line** - Form a row/column of N same-colored tiles
- **Square** - Form a 2x2 block of same-colored tiles
- **Shape** - Form an L, T, or Cross shape of same-colored tiles

### Worlds

| World | Name | Levels | Board | Features |
|-------|------|--------|-------|----------|
| 1 | Seedling Garden | 1-8 | 5x5 | Tutorial, basic goals |
| 2 | Blooming Meadow | 9-17 | 6x6 | Multi-goal levels, shapes |
| 3 | Ancient Grove | 18-25 | 7x7 | Complex shapes, 3-4 goals |
| 4 | Crystal Cavern | 26-33 | 7x7 | Frozen tiles (immovable) |
| 5 | Shattered Isles | 34-41 | 7x7 | Void cells (irregular boards) |
| 6 | Void Fortress | 42-49 | 8x8 | Frozen tiles + void cells |

### Obstacles
- **Frozen Tiles** - Cannot be swapped but their color counts toward patterns. Shown with an ice overlay.
- **Void Cells** - Empty spaces on the board. Creates irregular board shapes. Lines cannot cross voids.

### Difficulty Modes
- **Easy** - More moves, 1x star multiplier
- **Medium** - Standard moves, 2x star multiplier
- **Hard** - Fewer moves, 3x star multiplier, completed goals lock tiles

### Features
- Drag-to-swap with animated sliding
- Embossed tile rendering with unique motifs per color
- Hint system (highlights quadrant containing best move)
- 6 color themes (Light, Dark, Summer, Winter, Fall, Spring)
- User profiles with emoji avatars
- Star trail animations on win
- Life system with difficulty-gated recovery
- Win streak tracking

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Compose Canvas
- **Architecture**: MVVM (ViewModel + StateFlow)
- **Storage**: DataStore Preferences
- **Navigation**: Navigation Compose
- **Audio**: SoundPool (stub)
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35

## Building

```bash
./gradlew assembleDebug
```

Requires Android Studio with JBR (JetBrains Runtime) and Android SDK installed.

## Project Structure

```
com.patterngarden/
  model/       - Tile, Board, Goal, Level, GameState, PlayerProgress
  logic/       - BoardEngine, PatternMatcher, HintSolver, LevelLoader
  viewmodel/   - GameViewModel
  ui/
    theme/     - 6 themes with Material3 ColorScheme
    navigation/- Screen routes
    screens/   - Splash, Home, WorldSelect, LevelSelect, Game, Settings, Profile
    components/- GameBoardCanvas, GoalPanel, MoveCounter, StarDisplay, etc.
  data/        - ProgressRepository, SettingsRepository, ProfileRepository
  audio/       - AudioManager
```
