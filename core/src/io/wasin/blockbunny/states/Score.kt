package io.wasin.blockbunny.states

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.utils.viewport.ExtendViewport
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.handlers.BBInput
import io.wasin.blockbunny.handlers.GameStateManager

/**
 * Created by haxpor on 5/30/17.
 */
class Score(failed: Boolean, crystalsAmount: Int, maxCrystalAmount: Int, gsm: GameStateManager): GameState(gsm) {

    private var font: BitmapFont

    private val failedLine1: String = "Game Over"
    private val failedLine2: String = "Press any key to continue"

    private val successLine1: String = "Level Clear!"
    private val successLine2: String = "Press any key to continue"

    private val glyph1: GlyphLayout
    private val glyph2: GlyphLayout

    private val textMargin: Float = 5f

    constructor(gsm: GameStateManager): this(true, 0, 0, gsm) {}

    init {
        font = BitmapFont()

        glyph1 = GlyphLayout()
        glyph2 = GlyphLayout()

        if (failed) {
            glyph1.setText(font, failedLine1)
            glyph2.setText(font, failedLine2)
        }
        else {
            glyph1.setText(font, successLine1)
            glyph2.setText(font, successLine2)
        }
    }

    override fun handleInput() {
        if (BBInput.isPressed(BBInput.BUTTON1) || BBInput.isPressed(BBInput.BUTTON2) ||
                BBInput.isMouseDown(BBInput.MOUSE_BUTTON_LEFT) ||
                BBInput.isControllerPressed(BBInput.CONTROLLER_BUTTON_2)) {
            // go back to level selection
            gsm.setState(GameStateManager.LEVEL_SELECTION)
        }
    }

    override fun update(dt: Float) {
        handleInput()
    }

    override fun render() {
        sb.begin()
        font.draw(sb, glyph1, hudCam.viewportWidth/2f - glyph1.width/2f, hudCam.viewportHeight/2f + glyph1.height/2f + textMargin/2f)
        font.draw(sb, glyph2, hudCam.viewportWidth/2f - glyph2.width/2f, hudCam.viewportHeight/2f - glyph2.height/2f - textMargin/2f)
        sb.end()
    }

    override fun dispose() {
        font.dispose()
    }

    override fun resize_user(width: Int, height: Int) {

    }

    override fun setupViewport(cam: OrthographicCamera, hudCam: OrthographicCamera, viewportWidth: Float, viewportHeight: Float) {
        camViewport = ExtendViewport(viewportWidth, viewportHeight, cam)
        hudViewport = ExtendViewport(viewportWidth, viewportHeight, hudCam)
    }
}