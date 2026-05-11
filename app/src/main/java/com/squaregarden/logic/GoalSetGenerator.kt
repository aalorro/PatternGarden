package com.squaregarden.logic

import com.squaregarden.model.*
import kotlin.random.Random

object GoalSetGenerator {

    private enum class GoalType { LINE, SQUARE, SHAPE }

    private data class WorldConstraints(
        val colors: List<TileColor>,
        val allowedTypes: Set<GoalType>,
        val lineLengths: IntRange,
        val allowedShapes: List<ShapeType>
    )

    private val FOUR_COLORS = listOf(TileColor.RED, TileColor.BLUE, TileColor.GREEN, TileColor.YELLOW)
    private val FIVE_COLORS = TileColor.entries.toList()

    private val worldConstraints = mapOf(
        1 to WorldConstraints(FOUR_COLORS, setOf(GoalType.LINE), 3..5, emptyList()),
        2 to WorldConstraints(FOUR_COLORS, setOf(GoalType.LINE, GoalType.SQUARE), 4..5, emptyList()),
        3 to WorldConstraints(
            FOUR_COLORS, setOf(GoalType.LINE, GoalType.SQUARE, GoalType.SHAPE), 3..5,
            listOf(ShapeType.L_SHAPE, ShapeType.T_SHAPE)
        ),
        4 to WorldConstraints(
            FOUR_COLORS, setOf(GoalType.LINE, GoalType.SQUARE, GoalType.SHAPE), 3..5,
            listOf(ShapeType.L_SHAPE, ShapeType.T_SHAPE, ShapeType.CROSS, ShapeType.Z_SHAPE)
        ),
        5 to WorldConstraints(
            FIVE_COLORS, setOf(GoalType.LINE, GoalType.SQUARE, GoalType.SHAPE), 3..5,
            ShapeType.entries.toList()
        ),
        6 to WorldConstraints(
            FIVE_COLORS, setOf(GoalType.LINE, GoalType.SQUARE, GoalType.SHAPE), 3..5,
            ShapeType.entries.toList()
        ),
        7 to WorldConstraints(
            FIVE_COLORS, setOf(GoalType.LINE, GoalType.SQUARE, GoalType.SHAPE), 5..6,
            ShapeType.entries.toList()
        ),
        8 to WorldConstraints(
            FIVE_COLORS, setOf(GoalType.LINE, GoalType.SQUARE, GoalType.SHAPE), 5..6,
            ShapeType.entries.toList()
        ),
        9 to WorldConstraints(
            FIVE_COLORS, setOf(GoalType.LINE, GoalType.SQUARE, GoalType.SHAPE), 5..7,
            ShapeType.entries.toList()
        ),
        10 to WorldConstraints(
            FIVE_COLORS, setOf(GoalType.LINE, GoalType.SQUARE, GoalType.SHAPE), 6..7,
            ShapeType.entries.toList()
        )
    )

    /**
     * Generate 4 goal sets for a level: the original + 3 alternatives.
     * Tutorial levels return only the original set.
     */
    fun generateGoalSets(level: Level): List<List<Goal>> {
        if (level.tutorialSteps != null) return listOf(level.goals)
        return listOf(
            level.goals,
            generateAlternateSet(level, 1),
            generateAlternateSet(level, 2),
            generateAlternateSet(level, 3)
        )
    }

    private fun generateAlternateSet(level: Level, setIndex: Int): List<Goal> {
        val rng = Random(level.id.toLong() * 31 + setIndex.toLong())
        val constraints = worldConstraints[level.world] ?: worldConstraints[10]!!
        val goalCount = level.goals.size

        // Analyze original type distribution for weighted selection
        val lineCount = level.goals.count { it is Goal.Line }
        val squareCount = level.goals.count { it is Goal.Square }
        val shapeCount = level.goals.count { it is Goal.Shape }
        val total = (lineCount + squareCount + shapeCount).toFloat()

        val linePct = if (total > 0) lineCount / total else 0.5f
        val squarePct = if (total > 0) squareCount / total else 0.2f

        val goals = mutableListOf<Goal>()
        val usedIds = mutableSetOf<String>()
        var retries = 0

        while (goals.size < goalCount && retries < goalCount * 30) {
            val goal = generateSingleGoal(rng, constraints, linePct, squarePct)
            if (goal.id !in usedIds) {
                goals.add(goal)
                usedIds.add(goal.id)
            } else {
                retries++
            }
        }

        // Fallback: mutate original goals' colors to fill remaining slots
        var fallbackIdx = 0
        while (goals.size < goalCount && fallbackIdx < level.goals.size) {
            val mutated = mutateGoalColor(level.goals[fallbackIdx], rng, constraints.colors, usedIds)
            if (mutated.id !in usedIds) {
                goals.add(mutated)
                usedIds.add(mutated.id)
            }
            fallbackIdx++
        }

        return goals
    }

    private fun generateSingleGoal(
        rng: Random,
        constraints: WorldConstraints,
        linePct: Float,
        squarePct: Float
    ): Goal {
        val color = constraints.colors[rng.nextInt(constraints.colors.size)]

        // Weighted type selection with noise for variety
        val roll = rng.nextFloat()
        val adjLinePct = (linePct + rng.nextFloat() * 0.3f - 0.15f).coerceIn(0f, 1f)
        val adjSquarePct = (squarePct + rng.nextFloat() * 0.3f - 0.15f).coerceIn(0f, 1f)

        return when {
            roll < adjLinePct && GoalType.LINE in constraints.allowedTypes -> {
                val length = rng.nextInt(constraints.lineLengths.first, constraints.lineLengths.last + 1)
                Goal.Line(color, length)
            }
            roll < adjLinePct + adjSquarePct && GoalType.SQUARE in constraints.allowedTypes -> {
                Goal.Square(color)
            }
            GoalType.SHAPE in constraints.allowedTypes && constraints.allowedShapes.isNotEmpty() -> {
                val shape = constraints.allowedShapes[rng.nextInt(constraints.allowedShapes.size)]
                Goal.Shape(color, shape)
            }
            GoalType.LINE in constraints.allowedTypes -> {
                val length = rng.nextInt(constraints.lineLengths.first, constraints.lineLengths.last + 1)
                Goal.Line(color, length)
            }
            else -> Goal.Line(color, 3)
        }
    }

    private fun mutateGoalColor(
        original: Goal,
        rng: Random,
        colors: List<TileColor>,
        usedIds: Set<String>
    ): Goal {
        for (color in colors.shuffled(rng)) {
            val mutated = when (original) {
                is Goal.Line -> Goal.Line(color, original.length)
                is Goal.Square -> Goal.Square(color)
                is Goal.Shape -> Goal.Shape(color, original.shapeType)
            }
            if (mutated.id !in usedIds) return mutated
        }
        return original
    }
}
