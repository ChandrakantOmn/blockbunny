package io.wasin.blockbunny.states

import io.wasin.blockbunny.handlers.B2DVars
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import io.wasin.blockbunny.Game
import io.wasin.blockbunny.handlers.GameStateManager
import io.wasin.blockbunny.handlers.MyContactListener
import io.wasin.blockbunny.handlers.MyInput

/**
 * Created by haxpor on 5/16/17.
 */
class Play(gsm: GameStateManager) : GameState(gsm) {

    private var world: World
    private var b2dr: Box2DDebugRenderer
    private var playerBody: Body
    private var b2dCam: OrthographicCamera
    private var cl: MyContactListener

    private var tileMap: TiledMap
    private var tileSize: Float
    private var tmr: OrthogonalTiledMapRenderer

    init {
        world = World(Vector2(0f, -9.81f), true)

        cl = MyContactListener()
        world.setContactListener(cl)
        b2dr = Box2DDebugRenderer()

        // create platform
        val bdef = BodyDef()
        val shape = PolygonShape()
        val fdef = FixtureDef()

        // create player
        bdef.position.set(160f / B2DVars.PPM, 200f / B2DVars.PPM)
        bdef.type = BodyDef.BodyType.DynamicBody
        playerBody = world.createBody(bdef)
        shape.setAsBox(5f / B2DVars.PPM,5f / B2DVars.PPM)
        fdef.shape = shape
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER
        fdef.filter.maskBits = B2DVars.BIT_RED
        playerBody.createFixture(fdef).userData = "player"

        // create foot sensor
        shape.setAsBox(2 / B2DVars.PPM, 2 / B2DVars.PPM, Vector2(0f, -5 / B2DVars.PPM), 0f)
        fdef.shape = shape
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER
        fdef.filter.maskBits = B2DVars.BIT_RED
        fdef.isSensor = true
        playerBody.createFixture(fdef).userData = "foot"

        // set box2d cam
        b2dCam = OrthographicCamera()
        b2dCam.setToOrtho(false, Game.V_WIDTH / B2DVars.PPM, Game.V_HEIGHT / B2DVars.PPM)

        ////

        // load tile map
        tileMap = TmxMapLoader().load("maps/test.tmx")
        tmr = OrthogonalTiledMapRenderer(tileMap)

        val layer = tileMap.layers.get("red") as TiledMapTileLayer
        tileSize = layer.tileWidth

        // go through all the cells in the layer
        for (row in 0..layer.height-1) {
            for (col in 0..layer.width-1) {
                // get cell
                val cell = layer.getCell(col, row)

                // check if cell exist
                if (cell == null) continue
                if (cell.tile == null) continue

                // creae a body fixture from cell
                val bdef = BodyDef()
                bdef.type = BodyDef.BodyType.StaticBody
                bdef.position.set(
                        (col + 0.5f) * tileSize / B2DVars.PPM,
                        (row + 0.5f) * tileSize / B2DVars.PPM
                )

                val cs = ChainShape()
                val v = Array<Vector2>(3, { i -> Vector2.Zero })
                v[0] = Vector2(-tileSize / 2 / B2DVars.PPM, -tileSize / 2 / B2DVars.PPM)
                v[1] = Vector2(-tileSize / 2 / B2DVars.PPM, tileSize / 2 / B2DVars.PPM)
                v[2] = Vector2(tileSize / 2 / B2DVars.PPM, tileSize / 2 / B2DVars.PPM)
                cs.createChain(v)

                val fdef = FixtureDef()
                fdef.shape = cs
                fdef.filter.categoryBits = B2DVars.BIT_RED
                fdef.filter.maskBits = B2DVars.BIT_PLAYER

                world.createBody(bdef).createFixture(fdef)
            }
        }
    }

    override fun handleInput() {
        // player jump
        if (MyInput.isPressed(MyInput.BUTTON1)) {
            if (cl.playerOnGround) {
                playerBody.applyForceToCenter(0f, 200f, true)
            }
        }
    }

    override fun update(dt: Float) {
        handleInput()
        world.step(dt, 6, 2)
    }

    override fun render() {
        // clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // draw tile map
        tmr.setView(cam)
        tmr.render()

        // draw box2d world
        b2dr.render(world, b2dCam.combined)
    }

    override fun dispose() {
    }
}