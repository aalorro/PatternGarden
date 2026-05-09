package com.squaregarden.logic

import com.squaregarden.model.*

object ChallengeGenerator {

    fun generateLevel(type: ChallengeType): Level {
        return when (type) {
            ChallengeType.BLITZ -> generateBlitz()
            ChallengeType.OVERGROWN -> generateOvergrown()
            ChallengeType.SHIFTING -> generateShifting()
            ChallengeType.MEMORY -> generateMemory()
        }
    }

    /** Generate a fresh set of goals achievable on the current board (for Blitz replenish). */
    fun generateBlitzGoalSet(board: Board): List<Goal> {
        val colorCounts = mutableMapOf<TileColor, Int>()
        for (r in 0 until board.height) {
            for (c in 0 until board.width) {
                if (!board.isVoid(r, c)) {
                    val color = board.tileAt(r, c).color
                    colorCounts[color] = (colorCounts[color] ?: 0) + 1
                }
            }
        }
        val abundant = colorCounts.entries
            .filter { it.value >= 3 }
            .sortedByDescending { it.value }
            .map { it.key }

        if (abundant.isEmpty()) {
            val fallback = colorCounts.keys.toList().shuffled().first()
            return listOf(Goal.Line(fallback, 3))
        }

        val goals = mutableListOf<Goal>()
        val usedColors = mutableSetOf<TileColor>()
        val goalCount = 3.coerceAtMost(abundant.size)

        for (i in 0 until goalCount) {
            val color = abundant[i]
            usedColors.add(color)
            val count = colorCounts[color] ?: 0
            goals.add(
                if (count >= 4 && Math.random() < 0.3) Goal.Square(color)
                else Goal.Line(color, 3)
            )
        }
        return goals
    }

    // ── Blitz: 6×6, 3 simple goals, 999 moves (timer-based) ──

    private fun generateBlitz(): Level {
        val w = 6; val h = 6
        val colors = TileColor.entries.toList().shuffled().take(4)
        val goals = pickSimpleGoals(colors, 3)
        val tiles = generateRandomTiles(w, h, colors)
        return Level(
            id = ChallengeType.BLITZ.id,
            world = 0,
            name = ChallengeType.BLITZ.title,
            boardWidth = w,
            boardHeight = h,
            maxMoves = 999,
            initialTiles = tiles,
            goals = goals,
            starThresholds = StarThresholds(twoStar = 0, threeStar = 0)
        )
    }

    // ── Overgrown: 9×9, 8 mixed goals, 12 moves, ~15 frozen ──

    private fun generateOvergrown(): Level {
        val w = 9; val h = 9
        val colors = TileColor.entries.toList()
        val goals = pickMixedGoals(colors, 8)
        val tiles = generateRandomTiles(w, h, colors)
        val frozen = pickFrozenCells(w, h, 15)
        return Level(
            id = ChallengeType.OVERGROWN.id,
            world = 0,
            name = ChallengeType.OVERGROWN.title,
            boardWidth = w,
            boardHeight = h,
            maxMoves = 12,
            initialTiles = tiles,
            goals = goals,
            starThresholds = StarThresholds(twoStar = 3, threeStar = 6),
            frozenCells = frozen
        )
    }

    // ── Shifting Sands: 7×7, 4 mixed goals, 20 moves ──

    private fun generateShifting(): Level {
        val w = 7; val h = 7
        val colors = TileColor.entries.toList().shuffled().take(4)
        val goals = pickMixedGoals(colors, 4)
        val tiles = generateRandomTiles(w, h, colors)
        return Level(
            id = ChallengeType.SHIFTING.id,
            world = 0,
            name = ChallengeType.SHIFTING.title,
            boardWidth = w,
            boardHeight = h,
            maxMoves = 20,
            initialTiles = tiles,
            goals = goals,
            starThresholds = StarThresholds(twoStar = 6, threeStar = 12)
        )
    }

    // ── Memory Garden: 5×5, 2 simple goals, 8 moves ──

    private fun generateMemory(): Level {
        val w = 5; val h = 5
        val colors = TileColor.entries.toList().shuffled().take(3)
        val goals = pickSimpleGoals(colors, 2)
        val tiles = generateRandomTiles(w, h, colors)
        return Level(
            id = ChallengeType.MEMORY.id,
            world = 0,
            name = ChallengeType.MEMORY.title,
            boardWidth = w,
            boardHeight = h,
            maxMoves = 8,
            initialTiles = tiles,
            goals = goals,
            starThresholds = StarThresholds(twoStar = 2, threeStar = 4)
        )
    }

    // ── Helpers ──

    private fun pickSimpleGoals(colors: List<TileColor>, count: Int): List<Goal> {
        val shuffled = colors.shuffled()
        return (0 until count).map { i ->
            val color = shuffled[i % shuffled.size]
            if (Math.random() < 0.25) Goal.Square(color) else Goal.Line(color, 3)
        }
    }

    private fun pickMixedGoals(colors: List<TileColor>, count: Int): List<Goal> {
        val shuffled = colors.shuffled()
        val simpleShapes = listOf(ShapeType.L_SHAPE, ShapeType.T_SHAPE)
        val hardShapes = listOf(ShapeType.CROSS, ShapeType.Z_SHAPE, ShapeType.U_SHAPE)
        return (0 until count).map { i ->
            val color = shuffled[i % shuffled.size]
            val roll = Math.random()
            when {
                roll < 0.35 -> Goal.Line(color, if (Math.random() < 0.3) 4 else 3)
                roll < 0.55 -> Goal.Square(color)
                roll < 0.80 -> Goal.Shape(color, simpleShapes.random())
                else -> Goal.Shape(color, hardShapes.random())
            }
        }
    }

    private fun generateRandomTiles(w: Int, h: Int, colors: List<TileColor>): List<List<TileColor>> {
        return List(h) { List(w) { colors.random() } }
    }

    private fun pickFrozenCells(w: Int, h: Int, count: Int): Set<CellPos> {
        val all = mutableListOf<CellPos>()
        // Avoid edges for frozen cells so swaps are still possible
        for (r in 1 until h - 1) {
            for (c in 1 until w - 1) {
                all.add(CellPos(r, c))
            }
        }
        return all.shuffled().take(count.coerceAtMost(all.size)).toSet()
    }
}
