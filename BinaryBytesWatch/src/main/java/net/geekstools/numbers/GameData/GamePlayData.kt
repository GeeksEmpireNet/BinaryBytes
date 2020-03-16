package net.geekstools.numbers.GameData

import android.content.Context
import androidx.preference.PreferenceManager
import net.geekstools.numbers.GameView.GamePlayView
import net.geekstools.numbers.GameView.Tile

class GamePlayData (private var context: Context, private var gamePlayView: GamePlayView) {

    fun save() {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = settings.edit()
        val field = gamePlayView.game.grid.field
        val undoField = gamePlayView.game.grid.undoField
        editor.putInt(WIDTH, field.size)
        editor.putInt(HEIGHT, field.size)
        for (xx in field.indices) {
            for (yy in 0 until field[0].size) {
                if (field[xx][yy] != null) {
                    editor.putInt(xx.toString() + " " + yy, field[xx][yy]?.value!!)
                } else {
                    editor.putInt(xx.toString() + " " + yy, 0)
                }

                if (undoField[xx][yy] != null) {
                    editor.putInt("$UNDO_GRID$xx $yy", undoField[xx][yy]?.value!!)
                } else {
                    editor.putInt("$UNDO_GRID$xx $yy", 0)
                }
            }
        }
        editor.putLong(SCORE, gamePlayView.game.score)
        editor.putLong(HIGH_SCORE, gamePlayView.game.highScore)
        editor.putLong(UNDO_SCORE, gamePlayView.game.lastScore)
        editor.putBoolean(CAN_UNDO, gamePlayView.game.canUndo)
        editor.putInt(GAME_STATE, gamePlayView.game.gameState)
        editor.putInt(UNDO_GAME_STATE, gamePlayView.game.lastGameState)
        editor.apply()
    }

    fun load() {
        //Stopping all animations
        gamePlayView.game.aGrid.cancelAnimations()

        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        for (xx in gamePlayView.game.grid.field.indices) {
            for (yy in gamePlayView.game.grid.field[0].size until gamePlayView.game.grid.field[0].size) {
                val value = settings.getInt(xx.toString() + " " + yy, -1)
                if (value > 0) {
                    gamePlayView.game.grid.field[xx][yy] = Tile(xx, yy, value)
                } else if (value == 0) {
                    gamePlayView.game.grid.field[xx][yy] = null
                }

                val undoValue = settings.getInt("$UNDO_GRID$xx $yy", -1)
                if (undoValue > 0) {
                    gamePlayView.game.grid.undoField[xx][yy] = Tile(xx, yy, undoValue)
                } else if (value == 0) {
                    gamePlayView.game.grid.undoField[xx][yy] = null
                }
            }
        }

        gamePlayView.game.score = settings.getLong(SCORE, gamePlayView.game.score)
        gamePlayView.game.highScore = settings.getLong(HIGH_SCORE, gamePlayView.game.highScore)
        gamePlayView.game.lastScore = settings.getLong(UNDO_SCORE, gamePlayView.game.lastScore)
        gamePlayView.game.canUndo = settings.getBoolean(CAN_UNDO, gamePlayView.game.canUndo)
        gamePlayView.game.gameState = settings.getInt(GAME_STATE, gamePlayView.game.gameState)
        gamePlayView.game.lastGameState = settings.getInt(UNDO_GAME_STATE, gamePlayView.game.lastGameState)
    }

    companion object {
        private val WIDTH = "width"
        private val HEIGHT = "height"
        private val SCORE = "score"
        private val HIGH_SCORE = "high score temp"
        private val UNDO_SCORE = "undo score"
        private val CAN_UNDO = "can undo"
        private val UNDO_GRID = "undo"
        private val GAME_STATE = "game state"
        private val UNDO_GAME_STATE = "undo game state"
    }
}