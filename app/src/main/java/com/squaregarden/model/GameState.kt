package com.squaregarden.model

data class GameState(
    val level: Level,
    val board: Board,
    val movesRemaining: Int,
    val completedGoalIds: Set<String> = emptySet(),
    val completedGoalCells: Map<String, Set<CellPos>> = emptyMap(),
    val selectedCell: CellPos? = null,
    val hintCells: Set<CellPos> = emptySet(),
    val phase: GamePhase = GamePhase.PLAYING,
    val tutorialStepIndex: Int = 0,
    val swapAnim: SwapAnimation? = null,
    val starsAwarded: Int = 0,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val gameDifficulty: GameDifficulty = GameDifficulty.MEDIUM,
    val winsToRestoreLife: Int = 0,
    val lifeRestored: Boolean = false,
    val initialBoard: Board? = null,
    val solutionSteps: List<Pair<CellPos, CellPos>>? = null,
    val hasSolution: Boolean = false,
    val unlockedWorldName: String? = null
)

enum class GameDifficulty(val label: String, val starMultiplier: Float) {
    EASY("Easy", 0.75f),
    MEDIUM("Medium", 1.0f),
    HARD("Hard", 1.25f),
    VERY_HARD("Very Hard", 1.5f),
    EXTREMELY_HARD("Extremely Hard", 2.0f);

    companion object {
        fun calculate(
            maxMoves: Int,
            goals: List<Goal>,
            frozenCount: Int,
            voidCount: Int
        ): GameDifficulty {
            var points = 0f

            // Move pressure: fewer moves per goal = harder
            val movesPerGoal = if (goals.isNotEmpty()) maxMoves.toFloat() / goals.size else maxMoves.toFloat()
            points += when {
                movesPerGoal >= 5f -> 0f
                movesPerGoal >= 4f -> 1f
                movesPerGoal >= 3f -> 2f
                movesPerGoal >= 2f -> 3f
                else -> 4f
            }

            // Goal type complexity
            for (goal in goals) {
                points += when (goal) {
                    is Goal.Line -> 0f
                    is Goal.Square -> 0.5f
                    is Goal.Shape -> when (goal.shapeType) {
                        ShapeType.L_SHAPE, ShapeType.T_SHAPE -> 1f
                        ShapeType.CROSS, ShapeType.Z_SHAPE, ShapeType.U_SHAPE -> 1.5f
                    }
                }
            }

            // Board constraints
            points += frozenCount * 0.3f + voidCount * 0.2f

            return when {
                points < 2f -> EASY
                points < 3.5f -> MEDIUM
                points < 5.5f -> HARD
                points < 10f -> VERY_HARD
                else -> EXTREMELY_HARD
            }
        }
    }
}

data class SwapAnimation(
    val from: CellPos,
    val to: CellPos,
    val progress: Float // 0f..1f
)

enum class GamePhase {
    PLAYING, ANIMATING, WON, LOST, TUTORIAL_PAUSE, SHOWING_SOLUTION
}
