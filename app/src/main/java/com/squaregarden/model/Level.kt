package com.squaregarden.model

data class Level(
    val id: Int,
    val world: Int,
    val name: String,
    val boardWidth: Int,
    val boardHeight: Int,
    val maxMoves: Int,
    val initialTiles: List<List<TileColor>>,
    val goals: List<Goal>,
    val starThresholds: StarThresholds,
    val tutorialSteps: List<TutorialStep>? = null,
    val frozenCells: Set<CellPos> = emptySet(),
    val voidCells: Set<CellPos> = emptySet()
)

data class StarThresholds(
    val twoStar: Int,
    val threeStar: Int
)

data class TutorialStep(
    val message: String,
    val highlightCells: List<CellPos>? = null
)
