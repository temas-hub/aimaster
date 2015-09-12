package com.temas.aimaster

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 09/09/2015
 */


class DefaultInputProcessor(i: InputProcessor, val controller: Controller) : InputProcessor by i{

    private val minDistance = 10;

    private var startX : Float = -1f
    private var startY : Float = -1f



    override fun touchDown (screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        startX = screenX.toFloat()
        startY = screenY.toFloat()
        val point = controller.renderer.toGameCoords(startX, startY)
        controller.model.lastPoints.insert(point)
        controller.model.throwDirection.firstPoint.set(point)
        return false
    }


    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val point = controller.renderer.toGameCoords(Vector2(screenX.toFloat(), screenY.toFloat()))
        controller.launch(point)
//        try {
//            if (startX != -1f) {
//
//            }
//            return false
//        } finally {
//            startX = -1f
//            startY = -1f
//        }

        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val point = controller.renderer.toGameCoords(Vector2(startX, startY))
        controller.model.throwDirection.lastPoint.set(point)
        val lenSq = point.dst2(screenX.toFloat(), screenY.toFloat())
        if (lenSq >= minDistance) {
            startX = screenX.toFloat()
            startY = screenY.toFloat()
            controller.model.lastPoints.insert(point)
        }
        return false
    }
}