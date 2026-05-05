package com.squaregarden.logic

import android.content.Context
import com.squaregarden.R
import com.squaregarden.model.*
import org.json.JSONArray
import org.json.JSONObject

object LevelLoader {

    fun loadAllLevels(context: Context): List<Level> {
        val json = context.resources.openRawResource(R.raw.levels)
            .bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        return (0 until array.length()).map { parseLevel(array.getJSONObject(it)) }
    }

    private fun parseLevel(obj: JSONObject): Level {
        val goalsArray = obj.getJSONArray("goals")
        val goals = (0 until goalsArray.length()).map { parseGoal(goalsArray.getJSONObject(it)) }

        val tilesArray = obj.getJSONArray("tiles")
        val tiles = (0 until tilesArray.length()).map { r ->
            val row = tilesArray.getJSONArray(r)
            (0 until row.length()).map { c ->
                TileColor.valueOf(row.getString(c))
            }
        }

        val stars = obj.getJSONObject("stars")

        val tutorialSteps = if (obj.has("tutorial")) {
            val tutArray = obj.getJSONArray("tutorial")
            (0 until tutArray.length()).map { parseTutorialStep(tutArray.getJSONObject(it)) }
        } else null

        val frozenCells = if (obj.has("frozen")) {
            val arr = obj.getJSONArray("frozen")
            (0 until arr.length()).map {
                val cell = arr.getJSONObject(it)
                CellPos(cell.getInt("row"), cell.getInt("col"))
            }.toSet()
        } else emptySet()

        val voidCells = if (obj.has("voids")) {
            val arr = obj.getJSONArray("voids")
            (0 until arr.length()).map {
                val cell = arr.getJSONObject(it)
                CellPos(cell.getInt("row"), cell.getInt("col"))
            }.toSet()
        } else emptySet()

        return Level(
            id = obj.getInt("id"),
            world = obj.getInt("world"),
            name = obj.getString("name"),
            boardWidth = obj.getInt("width"),
            boardHeight = obj.getInt("height"),
            maxMoves = obj.getInt("maxMoves"),
            initialTiles = tiles,
            goals = goals,
            starThresholds = StarThresholds(
                twoStar = stars.getInt("twoStar"),
                threeStar = stars.getInt("threeStar")
            ),
            tutorialSteps = tutorialSteps,
            frozenCells = frozenCells,
            voidCells = voidCells
        )
    }

    private fun parseGoal(obj: JSONObject): Goal {
        val color = TileColor.valueOf(obj.getString("color"))
        return when (obj.getString("type")) {
            "line" -> Goal.Line(color, obj.getInt("length"))
            "square" -> Goal.Square(color)
            "shape" -> Goal.Shape(color, ShapeType.valueOf(obj.getString("shape")))
            else -> throw IllegalArgumentException("Unknown goal type: ${obj.getString("type")}")
        }
    }

    private fun parseTutorialStep(obj: JSONObject): TutorialStep {
        return TutorialStep(
            message = obj.getString("message"),
            highlightCells = if (obj.has("highlightCells")) {
                val arr = obj.getJSONArray("highlightCells")
                (0 until arr.length()).map {
                    val cell = arr.getJSONObject(it)
                    CellPos(cell.getInt("row"), cell.getInt("col"))
                }
            } else null
        )
    }
}
