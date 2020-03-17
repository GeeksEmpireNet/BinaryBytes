package net.geekstools.numbers.GameView

import android.app.Activity
import android.app.AlertDialog
import android.view.MotionEvent
import android.view.View
import net.geekstools.numbers.R

internal class InputListener(private val mView: GamePlayView, var activity: Activity) : View.OnTouchListener {
    private var x: Float = 0.toFloat()
    private var y: Float = 0.toFloat()
    private var lastDx: Float = 0.toFloat()
    private var lastDy: Float = 0.toFloat()
    private var previousX: Float = 0.toFloat()
    private var previousY: Float = 0.toFloat()
    private var startingX: Float = 0.toFloat()
    private var startingY: Float = 0.toFloat()
    private var previousDirection = 1
    private var veryLastDirection = 1
    private var hasMoved = false

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                x = event.x
                y = event.y
                startingX = x
                startingY = y
                previousX = x
                previousY = y
                lastDx = 0f
                lastDy = 0f
                hasMoved = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                x = event.x
                y = event.y
                if (mView.gameLogic.isActive) {
                    val dx = x - previousX
                    if (Math.abs(lastDx + dx) < Math.abs(lastDx) + Math.abs(dx) && Math.abs(dx) > RESET_STARTING
                            && Math.abs(x - startingX) > SWIPE_MIN_DISTANCE) {
                        startingX = x
                        startingY = y
                        lastDx = dx
                        previousDirection = veryLastDirection
                    }
                    if (lastDx == 0f) {
                        lastDx = dx
                    }
                    val dy = y - previousY
                    if (Math.abs(lastDy + dy) < Math.abs(lastDy) + Math.abs(dy) && Math.abs(dy) > RESET_STARTING
                            && Math.abs(y - startingY) > SWIPE_MIN_DISTANCE) {
                        startingX = x
                        startingY = y
                        lastDy = dy
                        previousDirection = veryLastDirection
                    }
                    if (lastDy == 0f) {
                        lastDy = dy
                    }
                    if (pathMoved() > SWIPE_MIN_DISTANCE * SWIPE_MIN_DISTANCE && !hasMoved) {
                        var moved = false
                        //Vertical
                        if ((dy >= SWIPE_THRESHOLD_VELOCITY && Math.abs(dy) >= Math.abs(dx) || y - startingY >= MOVE_THRESHOLD) && previousDirection % 2 != 0) {
                            moved = true
                            previousDirection = previousDirection * 2
                            veryLastDirection = 2
                            mView.gameLogic.move(2)
                        } else if ((dy <= -SWIPE_THRESHOLD_VELOCITY && Math.abs(dy) >= Math.abs(dx) || y - startingY <= -MOVE_THRESHOLD) && previousDirection % 3 != 0) {
                            moved = true
                            previousDirection = previousDirection * 3
                            veryLastDirection = 3
                            mView.gameLogic.move(0)
                        }
                        //Horizontal
                        if ((dx >= SWIPE_THRESHOLD_VELOCITY && Math.abs(dx) >= Math.abs(dy) || x - startingX >= MOVE_THRESHOLD) && previousDirection % 5 != 0) {
                            moved = true
                            previousDirection = previousDirection * 5
                            veryLastDirection = 5
                            mView.gameLogic.move(1)
                        } else if ((dx <= -SWIPE_THRESHOLD_VELOCITY && Math.abs(dx) >= Math.abs(dy) || x - startingX <= -MOVE_THRESHOLD) && previousDirection % 7 != 0) {
                            moved = true
                            previousDirection = previousDirection * 7
                            veryLastDirection = 7
                            mView.gameLogic.move(3)
                        }
                        if (moved) {
                            hasMoved = true
                            startingX = x
                            startingY = y
                        }
                    }
                }
                previousX = x
                previousY = y
                return true
            }
            MotionEvent.ACTION_UP -> {
                x = event.x
                y = event.y
                previousDirection = 1
                veryLastDirection = 1
                //"Menu" inputs
                if (!hasMoved) {
                    if (iconPressedNew(mView.sXNewGame - mView.iconSize * 3, mView.sYIcons)) {
                        if (!mView.gameLogic.gameLost()) {
                            AlertDialog.Builder(activity)
                                    .setPositiveButton(R.string.reset) { dialog, which -> mView.gameLogic.newGame() }
                                    .setNegativeButton(R.string.continue_game, null)
                                    .setMessage(R.string.reset_dialog_message)
                                    .show()
                        } else {
                            mView.gameLogic.newGame()
                        }
                    } else if (iconPressedUndo(mView.sXUndo, mView.sYIcons)) {
                        mView.gameLogic.revertUndoState()
                        /*try {
                            throw new RuntimeException("Original Exception");
                        } catch (RuntimeException e) {
                            throw new RuntimeException("Check out buddybuild Crash Reporting!");
                        }*/
                    } else if (isTap(2) && inRange(mView.startingX.toFloat(), x, mView.endingX.toFloat())
                            && inRange(mView.startingY.toFloat(), x, mView.endingY.toFloat()) && mView.continueButtonEnabled) {
                        mView.gameLogic.setEndlessMode()
                    }
                }
            }
        }
        return true
    }

    private fun pathMoved(): Float {
        return (x - startingX) * (x - startingX) + (y - startingY) * (y - startingY)
    }

    private fun iconPressedNew(sx: Int, sy: Int): Boolean {
        return (isTap(1)
                && inRange(sx.toFloat(), x, mView.sXNewGame.toFloat())
                && inRange(sy.toFloat(), y, (sy + mView.iconSize).toFloat()))
    }

    private fun iconPressedUndo(sx: Int, sy: Int): Boolean {
        return (isTap(1)
                && inRange(sx.toFloat(), x, (mView.sXUndo + mView.iconSize * 3).toFloat())
                && inRange(sy.toFloat(), y, (sy + mView.iconSize).toFloat()))
    }

    private fun inRange(starting: Float, check: Float, ending: Float): Boolean {
        return starting <= check && check <= ending
    }

    private fun isTap(factor: Int): Boolean {
        return pathMoved() <= mView.iconSize * factor
    }

    companion object {

        private val SWIPE_MIN_DISTANCE = 0
        private val SWIPE_THRESHOLD_VELOCITY = 25
        private val MOVE_THRESHOLD = 250
        private val RESET_STARTING = 10
    }
}
