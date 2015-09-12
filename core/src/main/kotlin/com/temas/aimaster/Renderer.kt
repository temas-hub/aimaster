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
        try {
            shaper.setProjectionMatrix(cam.combined)
            val dir = model.throwDirection
            if (dir.firstPoint.x != -1f) {
                shaper.begin(ShapeRenderer.ShapeType.Line)
                val dirVect = dir.lastPoint.cpy().sub(dir.firstPoint)
                val endPoint = dir.lastPoint.cpy().sub(dirVect.scl(2f))
                val normDir = dirVect.nor()
                val perp = Vector2(-normDir.y, normDir.x)
                val thickness = 20f
                val perpDiv = perp.cpy().scl(thickness / 2)
                val perpPoint1 = dir.firstPoint.cpy().sub(perpDiv)
                val perpPoint2 = dir.firstPoint.cpy().add(perpDiv)
                shaper.line(perpPoint1, perpPoint2)
                shaper.line(perpPoint1, endPoint)
                shaper.line(perpPoint2, endPoint)
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
