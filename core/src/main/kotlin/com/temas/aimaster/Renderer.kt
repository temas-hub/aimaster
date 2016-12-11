package com.temas.aimaster

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
//import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.Scaling
import com.temas.aimaster.model.Model
import com.temas.aimaster.model.PhysicsWorld
import java.util.*
import kotlin.concurrent.read

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 08/09/2015
 */



public class Renderer(val model: Model) {
    companion object {
        public val GAME_WIDTH : Float = 640f
        public val GAME_HEIGHT : Float = 960f
        public val ARROW_THICKNESS : Float = 20f
    }
    private var viewportX: Int = 0
    private var viewportY: Int = 0
    private var viewportWidth: Int = 0
    private var viewportHeight: Int = 0
    private val shaper = ShapeRenderer()
    private val cam = OrthographicCamera()
    private val debugRenderer = Box2DDebugRenderer(true, true,true,true,true,true)
    private var debugMatrix: Matrix4? = null

    fun load() {
        //TODO textures
    }

    fun render(delta: Float) {
        model.lock.read {
            cam.update();
            shaper.projectionMatrix = cam.combined
            shaper.begin(ShapeRenderer.ShapeType.Filled)
            drawTarget()
            drawStones()
            shaper.end()
            shaper.begin(ShapeRenderer.ShapeType.Line)
            shaper.color = Color.WHITE
            drawArrow()
            debugMatrix = shaper.projectionMatrix.cpy().scale(PhysicsWorld.PIXELS_TO_METERS,
                    PhysicsWorld.PIXELS_TO_METERS, 0f)
            shaper.end()
            drawPhysicsDebug()
        }
    }

    private fun drawPhysicsDebug() {
        debugRenderer.render(model.physics.world, debugMatrix)
    }

    private fun drawTarget() {
        shaper.color = Color.OLIVE
        shaper.circle(model.target.center.x, model.target.center.y, model.target.radius)
    }

    private fun drawStones() {
        shaper.color = Color.BLACK
        model.stones.forEach {
            shaper.circle(it.pos.x, it.pos.y, it.rad)
            // DEBUG vector
            //shaper.rectLine(it.pos, it.pos.cpy().add(it.velocity), 3f)
        }
    }

    private fun drawArrow() {
        val a = model.arrow
        if (a.len != -1f) {
            val normDir = a.dir.cpy().nor()
            val perp = Vector2(-normDir.y, normDir.x)
            val perpDiv = perp.cpy().scl(ARROW_THICKNESS / 2)
            val perpPoint1 = a.firstPoint.cpy().sub(perpDiv)
            val perpPoint2 = a.firstPoint.cpy().add(perpDiv)
            shaper.line(perpPoint1, perpPoint2)
            shaper.line(perpPoint1, a.endPoint)
            shaper.line(perpPoint2, a.endPoint)
        }
    }

    private fun drawTraceLine() {
        if (model.lastPoints.size > 1) {
            val lastPoints = ArrayList<Vector2>(model.lastPoints.size * 2)
            model.smooth(model.lastPoints, lastPoints)
            var first = lastPoints.get(lastPoints.size - 1)
            for (i in lastPoints.size - 2 downTo  0) {
                shaper.line(first, lastPoints[i])
                first = lastPoints[i]
            }
        }
    }


    fun setSize(width: Int, height: Int) {
        val size = Scaling.fit.apply(GAME_WIDTH, GAME_HEIGHT, width.toFloat(), height.toFloat())
        viewportX = (width - size.x).toInt() / 2
        viewportY = (height - size.y).toInt() / 2
        viewportWidth = size.x.toInt()
        viewportHeight = size.y.toInt()
        Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight)
        val centerX = GAME_WIDTH / 2
        val centerY = GAME_HEIGHT / 2
        cam.position.set(centerX, centerY, 0f)
        cam.viewportWidth = GAME_WIDTH
        cam.viewportHeight = GAME_HEIGHT
    }

    fun toGameCoords(v: Vector2): Vector2 {
        return toGameCoords(v.x, v.y)
    }

    fun toGameCoords(x: Float, y: Float): Vector2 {
        val gameCoords = cam.unproject(Vector3(x, y, 0f), viewportX.toFloat(), viewportY.toFloat(), viewportWidth.toFloat(), viewportHeight.toFloat())
        return Vector2(gameCoords.x, gameCoords.y)
    }

}
