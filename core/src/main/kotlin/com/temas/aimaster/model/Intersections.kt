package com.temas.aimaster.model

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.Renderer

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 07.10.2016
 */
object Intersections {

    val screenBounds = Rectangle(0f,0f, Renderer.GAME_WIDTH, Renderer.GAME_HEIGHT)
    private val p1 = Vector2(screenBounds.x, screenBounds.y)
    private val p2 = Vector2(screenBounds.x + screenBounds.width, screenBounds.y)
    private val p3 = Vector2(screenBounds.x + screenBounds.width, screenBounds.y + screenBounds.height)
    private val p4 = Vector2(screenBounds.x, screenBounds.y + screenBounds.height)

    fun intersects(cir: Circle): Boolean {

        val center = Vector2(cir.x, cir.y)

        return Intersector.intersectSegmentCircle(p1, p2, center, cir.radius) ||
                Intersector.intersectSegmentCircle(p2, p3, center, cir.radius) ||
                Intersector.intersectSegmentCircle(p3, p4, center, cir.radius) ||
                Intersector.intersectSegmentCircle(p4, p1, center, cir.radius)
    }

    private val Circle.center: Vector2
            get() = Vector2(this.x, this.y)


    fun isBoardHorisontal(cir: Circle): Boolean {

        return Intersector.intersectSegmentCircle(p1, p2, cir.center, cir.radius) ||
                Intersector.intersectSegmentCircle(p3, p4, cir.center, cir.radius)
    }

    fun isBoardVertical(cir: Circle): Boolean {

        return Intersector.intersectSegmentCircle(p2, p3, cir.center, cir.radius) ||
                Intersector.intersectSegmentCircle(p4, p1, cir.center, cir.radius)
    }


    fun Stone.reflectedStone(otherStone: Stone){

    }
}