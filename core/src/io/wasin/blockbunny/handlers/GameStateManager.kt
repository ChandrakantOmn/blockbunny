package io.wasin.blockbunny.handlers

import io.wasin.blockbunny.Game
import io.wasin.blockbunny.states.GameState
import io.wasin.blockbunny.states.LevelSelection
import io.wasin.blockbunny.states.Play
import io.wasin.blockbunny.states.Score

import java.util.Stack

/**
 * Created by haxpor on 5/14/17.
 */

class GameStateManager(game: Game){
    var game: Game
        private set
    private var gameStates: Stack<GameState>

    private var isCurrentStateClear: Boolean = false
    private var currentStateCystalsAmount: Int = 0
    private var currentMaxCrystalAmount: Int = 0

    init {
        this.game = game
        this.gameStates = Stack<GameState>()
    }

    companion object {
        const val PLAY = 5000
        const val LEVEL_SELECTION = 5001
        const val SCORE = 5002
    }

    fun update(dt: Float) {
        this.gameStates.peek().update(dt)
    }

    fun render() {
        for (state in this.gameStates) {
            state.render()
        }
    }

    private fun getState(state: Int): GameState? {
        if (state == PLAY)  return Play(this)
        else if (state == LEVEL_SELECTION)  return LevelSelection(this)
        else if (state == SCORE) {
            if (isCurrentStateClear) {
                return Score(false, currentStateCystalsAmount, currentMaxCrystalAmount, this)
            }
            else {
                return Score(this)
            }
        }
        return null
    }

    fun setState(state: Int) {
        this.gameStates.clear()
        this.pushState(state)
    }

    fun pushState(state: Int) {
        this.gameStates.push(this.getState(state))
    }

    fun popState() {
        val g = this.gameStates.pop()
        g.dispose()
    }

    fun setCurrentActiveLevelAsGameOver() {
        isCurrentStateClear = false
        currentStateCystalsAmount = 0
        currentMaxCrystalAmount = 0
    }

    fun setCurrentActiveLevelAsClear(crystals: Int, maxCrystals: Int) {
        isCurrentStateClear = true
        currentStateCystalsAmount = crystals
        currentMaxCrystalAmount = maxCrystals
    }

    fun resetPreviousActiveLevelState() {
        setCurrentActiveLevelAsGameOver()
    }
}