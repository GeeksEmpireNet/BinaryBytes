package net.geekstools.numbers.GameData

import android.content.Context
import androidx.preference.PreferenceManager
import net.geekstools.numbers.GameView.GamePlayView
import net.geekstools.numbers.GameView.Tile

class GamePlayData (private var context: Context, private var gamePlayView: GamePlayView) {

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
    
    fun save() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()

        val field = gamePlayView.gameLogic.grid.field
        val undoField = gamePlayView.gameLogic.grid.undoField
        editor.putInt(WIDTH, field.size)
        editor.putInt(HEIGHT, field.size)
        for (xx in field.indices) {
            for (yy in field[0].indices) {
                if (field[xx][yy] != null) {
                    editor.putInt("$xx-$yy", field[xx][yy]?.value!!)
                } else {
                    editor.putInt("$xx-$yy", 0)
                }

                if (undoField[xx][yy] != null) {
                    editor.putInt("$UNDO_GRID$xx $yy", undoField[xx][yy]?.value!!)
                } else {
                    editor.putInt("$UNDO_GRID$xx $yy", 0)
                }
            }
        }
        editor.putLong(SCORE, gamePlayView.gameLogic.score)
        editor.putLong(HIGH_SCORE, gamePlayView.gameLogic.highScore)
        editor.putLong(UNDO_SCORE, gamePlayView.gameLogic.lastScore)
        editor.putBoolean(CAN_UNDO, gamePlayView.gameLogic.canUndo)
        editor.putInt(GAME_STATE, gamePlayView.gameLogic.gameState)
        editor.putInt(UNDO_GAME_STATE, gamePlayView.gameLogic.lastGameState)
        editor.apply()
    }

    fun load() {
        //Stopping all animations
        gamePlayView.gameLogic.aGrid.cancelAnimations()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        for (xx in gamePlayView.gameLogic.grid.field.indices) {

            for (yy in gamePlayView.gameLogic.grid.field[0].indices) {

                val value = sharedPreferences.getInt("$xx-$yy", -1)
                if (value > 0) {
                    gamePlayView.gameLogic.grid.field[xx][yy] = Tile(xx, yy, value)
                } else if (value == 0) {
                    gamePlayView.gameLogic.grid.field[xx][yy] = null
                }

                val undoValue = sharedPreferences.getInt("$UNDO_GRID$xx $yy", -1)
                if (undoValue > 0) {
                    gamePlayView.gameLogic.grid.undoField[xx][yy] = Tile(xx, yy, undoValue)
                } else if (value == 0) {
                    gamePlayView.gameLogic.grid.undoField[xx][yy] = null
                }
            }
        }

        gamePlayView.gameLogic.score = sharedPreferences.getLong(SCORE, gamePlayView.gameLogic.score)
        gamePlayView.gameLogic.highScore = sharedPreferences.getLong(HIGH_SCORE, gamePlayView.gameLogic.highScore)
        gamePlayView.gameLogic.lastScore = sharedPreferences.getLong(UNDO_SCORE, gamePlayView.gameLogic.lastScore)
        gamePlayView.gameLogic.canUndo = sharedPreferences.getBoolean(CAN_UNDO, gamePlayView.gameLogic.canUndo)
        gamePlayView.gameLogic.gameState = sharedPreferences.getInt(GAME_STATE, gamePlayView.gameLogic.gameState)
        gamePlayView.gameLogic.lastGameState = sharedPreferences.getInt(UNDO_GAME_STATE, gamePlayView.gameLogic.lastGameState)
    }
}