package com.patterngarden.logic

import com.patterngarden.model.*

object HintSolver {

    fun findBestSwap(
        board: Board,
        goals: List<Goal>,
        completedGoalIds: Set<String>
    ): Pair<CellPos, CellPos>? {
        val remainingGoals = goals.filter { it.id !in completedGoalIds }
        if (remainingGoals.isEmpty()) return null

        var bestSwap: Pair<CellPos, CellPos>? = null
        var bestScore = -1

        for (r in 0 until board.height) {
            for (c in 0 until board.width) {
                if (board.isVoid(r, c) || board.tileAt(r, c).frozen) continue
                val from = CellPos(r, c)
                val neighbors = listOf(CellPos(r, c + 1), CellPos(r + 1, c))
                for (neighbor in neighbors) {
                    if (!BoardEngine.canSwap(board, from, neighbor)) continue

                    val swapped = BoardEngine.executeSwap(board, from, neighbor)
                    val newlyCompleted = BoardEngine.evaluateGoals(swapped, remainingGoals)
                    val score = newlyCompleted.size

                    if (score > bestScore) {
                        bestScore = score
                        bestSwap = Pair(from, neighbor)
                    }
                }
            }
        }

        return bestSwap
    }
}
