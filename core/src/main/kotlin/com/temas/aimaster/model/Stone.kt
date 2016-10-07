package com.temas.aimaster.model

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 07.10.2016
 */
class Stone(startPoint: Vector2, val rad: Float = 30f, val dir: Vector2, var velocity: Float = 2f) {

    companion object {
        val acceleration: Float = -0.0005f
        val collisionVelocityLoss: Float = 0.001f
    }

    enum class STATE {
        MOVE,
        STAY,
        STICKED
    }

    val pos = Vector2(startPoint.x, startPoint.y)

    var state: STATE = STATE.MOVE

    fun circle() = Circle(pos, rad)

    fun update(delta: Float) {
        if (state == STATE.MOVE) {
            val newX = pos.x + dir.x * velocity * delta
            val newY = pos.y + dir.y * velocity * delta
            pos.set(newX, newY)
            velocity = -acceleration * delta

            if (Intersections.isBoardHorisontal(circle())) {
                dir.y = -dir.y
                velocity = -collisionVelocityLoss
            } else if (Intersections.isBoardVertical(circle())) {
                dir.x = -dir.x
                velocity = -collisionVelocityLoss
            }
        }
    }
}