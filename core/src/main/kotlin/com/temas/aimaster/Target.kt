package com.temas.aimaster

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import java.util.*

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 14/09/2015
 */

public class Target(var speed: Float, var radius: Float) {
    private val NUM_OF_CHANGE_DIR_TRIES: Int = 10;
    private val CENTRAL_POINT = Vector2(Renderer.GAME_WIDTH / 2, Renderer.GAME_HEIGHT / 2);

    private val rnd = Random()
    private val screenBounds = Rectangle(0f,0f,Renderer.GAME_WIDTH, Renderer.GAME_HEIGHT)
    private val p1 = Vector2(screenBounds.x, screenBounds.y)
    private val p2 = Vector2(screenBounds.x + screenBounds.width, screenBounds.y)
    private val p3 = Vector2(screenBounds.x + screenBounds.width, screenBounds.y + screenBounds.height)
    private val p4 = Vector2(screenBounds.x, screenBounds.y + screenBounds.height)



    var center: Vector2 = CENTRAL_POINT;
    var moveDir: Vector2 = Vector2.Zero

    fun update(delta: Float) {
        if (moveDir.isZero) {
            moveDir = getRandomVector()
        }
        var targetCircle = getNewPoistion(delta)
        targetCircle = makeSureItISWithInTheScreen(delta, targetCircle)
        center.set(targetCircle.x, targetCircle.y)
    }



    private fun makeSureItISWithInTheScreen(delta: Float, targetCircle: Circle): Circle {
        var res = targetCircle
        if (interects(res)) {
            for (i in 1..NUM_OF_CHANGE_DIR_TRIES) {
                if (interects(res)) {
                    moveDir = getRandomVector()
                    res = getNewPoistion(delta)
                }
            }
            if (interects(res)) {
                res = Circle(CENTRAL_POINT, radius)
            }
        }
        return res
    }

    private fun interects(cir: Circle): Boolean {

        val center = Vector2(cir.x, cir.y)

        return Intersector.intersectSegmentCircle(p1, p2, center, cir.radius) ||
                Intersector.intersectSegmentCircle(p2, p3, center, cir.radius) ||
                Intersector.intersectSegmentCircle(p3, p4, center, cir.radius) ||
                Intersector.intersectSegmentCircle(p4, p1, center, cir.radius)
    }

    private fun getNewPoistion(delta: Float): Circle {
        var newCenter = center.cpy().add(moveDir.cpy().scl(speed * delta))
        val targetCircle = Circle(newCenter, radius)
        return targetCircle
    }

    private fun getRandomVector(): Vector2 {
        val x: Float = (rnd.nextInt(21) - 10) / 10f
        val y: Float = (rnd.nextInt(21) - 10) / 10f
        return Vector2(x,y).nor()
    }

}
