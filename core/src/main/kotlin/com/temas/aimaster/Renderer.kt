package com.temas.aimaster.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Scaling
import com.temas.aimaster.model.Model

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
        try {
            cam.update();
            shaper.setProjectionMatrix(cam.combined)
            /*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.setColor(Color.FOREST)
            shapeRenderer.circle(GAME_WIDTH / 2, GAME_HEIGHT / 2, 50f)*/
            shaper.begin(ShapeRenderer.ShapeType.Line)
            val lastPoints = model.lastPoints
            if (lastPoints.size > 0) {
                var first = lastPoints.get(lastPoints.size - 1)
                for (i in lastPoints.size - 2 downTo  0) {
                    shaper.line(toGameCoords(first), toGameCoords(lastPoints.get(i)))
                    first = lastPoints.get(i)
                }
            }
        } finally {
            shaper.end()
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
