package com.temas.aimaster.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Scaling
import com.temas.aimaster.model.Model
import java.util.*

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 08/09/2015
 */



public class Renderer(val model: Model) {
    companion object {
        public val GAME_WIDTH : Float = 960f
        public val GAME_HEIGHT : Float = 640f
        public val ARROW_THICKNESS : Float = 20f
    }
    private var viewportX: Int = 0
    private var viewportY: Int = 0
    private var viewportWidth: Int = 0
    private var viewportHeight: Int = 0
    private val shaper = ShapeRenderer()
    private val cam = OrthographicCamera()

    fun load() {
        //TODO textures
    }

    fun render(delta: Float) {
        cam.update();
        drawTarget()
        drawBall()
        drawArrow()
    }

    private fun drawTarget() {
        val oldColor = shaper.getColor()
        try {
            shaper.setProjectionMatrix(cam.combined)
            shaper.begin(ShapeRenderer.ShapeType.Filled)
            shaper.setColor(Color.OLIVE)
            shaper.circle(model.target.center.x, model.target.center.y, model.target.radius)
        } finally {
            shaper.setColor(oldColor)
            shaper.end()
        }
    }

    private fun drawBall() {
        try {
            shaper.setProjectionMatrix(cam.combined)
            if (model.ball != null) {
                shaper.begin(ShapeRenderer.ShapeType.Line)
                val ball = model.ball!!
                val defBallRadius = 10f
                shaper.circle(ball.pos3.x, ball.pos3.y, ball.pos3.z + defBallRadius)
            }
        } finally {
            shaper.end()
        }
    }

    private fun drawArrow() {
        try {
            shaper.setProjectionMatrix(cam.combined)
            val a = model.arrow
            if (a.len != -1f) {
                shaper.begin(ShapeRenderer.ShapeType.Line)
                val normDir = a.dir.cpy().nor()
                val perp = Vector2(-normDir.y, normDir.x)
                val perpDiv = perp.cpy().scl(ARROW_THICKNESS / 2)
                val perpPoint1 = a.firstPoint.cpy().sub(perpDiv)
                val perpPoint2 = a.firstPoint.cpy().add(perpDiv)
                shaper.line(perpPoint1, perpPoint2)
                shaper.line(perpPoint1, a.endPoint)
                shaper.line(perpPoint2, a.endPoint)
            }
        } finally {
            shaper.end()
        }
    }

    private fun drawTraceLine() {
        shaper.begin(ShapeRenderer.ShapeType.Line)
        if (model.lastPoints.size > 1) {
            val lastPoints = ArrayList<Vector2>(model.lastPoints.size * 2)
            model.smooth(model.lastPoints, lastPoints)
            var first = lastPoints.get(lastPoints.size() - 1)
            for (i in lastPoints.size() - 2 downTo  0) {
                shaper.line(first, lastPoints.get(i))
                first = lastPoints.get(i)
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
