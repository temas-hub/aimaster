package com.temas.aimaster

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.renderer.Renderer
import java.util.*

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 14/09/2015
 */

public class Target(var speed: Float, var radius: Float) {
    private val rnd = Random()
    private val screenBounds = Rectangle(0f,0f,Renderer.GAME_WIDTH, Renderer.GAME_HEIGHT)

    public var center: Vector2 = Vector2(Renderer.GAME_WIDTH / 2, Renderer.GAME_HEIGHT / 2)
    public var moveDir: Vector2 = Vector2.Zero

    fun update(delta: Float) {
        if (moveDir.isZero()) {
            moveDir = getRandomVector()
        }
        var targetCircle = getNewPoistion(delta)
        while (interects(targetCircle)) {
            targetCircle = getNewPoistion(delta)
        }
        center.set(targetCircle.x, targetCircle.y)
    }

    private fun interects(cir: Circle): Boolean {
        val p1 = Vector2(screenBounds.x, screenBounds.y)
        val p2 = Vector2(screenBounds.x + screenBounds.width, screenBounds.y)
        val p3 = Vector2(screenBounds.x + screenBounds.width, screenBounds.y + screenBounds.height)
        val p4 = Vector2(screenBounds.x, screenBounds.y + screenBounds.height)

        val center = Vector2(cir.x, cir.y)

        return Intersector.intersectSegmentCircle(p1, p2, center, cir.radius) ||
                Intersector.intersectSegmentCircle(p2, p3, center, cir.radius) ||
                Intersector.intersectSegmentCircle(p3, p4, center, cir.radius) ||
                Intersector.intersectSegmentCircle(p4, p1, center, cir.radius)
    }

    private fun getNewPoistion(delta: Float): Circle {
        var newCenter = center.cpy()
        newCenter = newCenter.add(moveDir.cpy().scl(speed * delta))
        val targetCircle = Circle(newCenter, radius)
        return targetCircle
    }

    private fun getRandomVector(): Vector2 {
        val x: Float = (rnd.nextInt(21) - 10) / 10f
        val y: Float = (rnd.nextInt(21) - 10) / 10f
        return Vector2(x,y)
    }

}
