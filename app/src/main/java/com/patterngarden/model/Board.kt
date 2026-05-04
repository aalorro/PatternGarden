package com.patterngarden.model

data class Board(
    val width: Int,
    val height: Int,
    val tiles: List<List<Tile>>,
    val voids: Set<CellPos> = emptySet()
) {
    fun tileAt(row: Int, col: Int): Tile = tiles[row][col]

    fun isVoid(row: Int, col: Int): Boolean = CellPos(row, col) in voids

    fun isValidCell(row: Int, col: Int): Boolean =
        row in 0 until height && col in 0 until width && !isVoid(row, col)

    fun withSwap(r1: Int, c1: Int, r2: Int, c2: Int): Board {
        val mutable = tiles.map { it.toMutableList() }
        val temp = mutable[r1][c1]
        mutable[r1][c1] = mutable[r2][c2]
        mutable[r2][c2] = temp
        return copy(tiles = mutable.map { it.toList() })
    }
}
