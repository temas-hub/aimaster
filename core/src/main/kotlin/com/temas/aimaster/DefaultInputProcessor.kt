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
        controller.model.lastPoints.insert(Vector2(startX, startY))
        return false
    }


    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

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
        val tmpVec = Vector2(startX, startY)
        val lenSq = tmpVec.dst2(screenX.toFloat(), screenY.toFloat())
        if (lenSq >= minDistance) {
            startX = screenX.toFloat()
            startY = screenY.toFloat()
            controller.model.lastPoints.insert(tmpVec)
        }
        return false
    }

    public fun simplify(points: FixedList<Vector2>, sqTolerance: Float, out: FixedList<Vector2>) {
        val len = points.size;

        var point = Vector2();
        var prevPoint = points.get(0);

        out.clear();
        out.add(prevPoint);

        for (i in 1..len - 1) {
            point = points.get(i);
            if (distSq(point, prevPoint) > sqTolerance) {
                out.add(point);
                prevPoint = point;
            }
        }
        if (!prevPoint.equals(point)) {
            out.add(point);
        }

    }

    public fun distSq(p1: Vector2, p2: Vector2): Float {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return dx * dx + dy * dy
    }

}