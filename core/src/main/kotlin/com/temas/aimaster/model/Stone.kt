package com.temas.aimaster.model

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 07.10.2016
 */
open class Stone(val id: Int = ++ID_COUNTER,
                 val startPoint: Vector2,
                 val rad: Float = DEFAULT_RADIUS,
                 var velocity: Vector2,
                 val creationTime:Long = System.currentTimeMillis()) {

    companion object {
        var ID_COUNTER = 0;
        val DEFAULT_RADIUS = 20f
        val START_VELOCITY = 2f

//        val acceleration: Float = -0.0005f
//        val collisionVelocityLoss: Float = 0.001f
    }

    enum class STATE {
        MOVE,
        STAY,
        STICKED
    }

    val pos = Vector2(startPoint.x, startPoint.y)
    var state: STATE = STATE.MOVE

    fun circle() = Circle(pos, rad)

    open fun update(delta: Float) {
        if (state == STATE.MOVE) {
            val newX = pos.x + velocity.x * delta
            val newY = pos.y + velocity.y * delta
            pos.set(newX, newY)
//                velocity = -acceleration * delta
//
//                if (Intersections.isBoardHorisontal(circle())) {
//                    dir.y = -dir.y
//                    velocity = -collisionVelocityLoss
//                } else if (Intersections.isBoardVertical(circle())) {
//                    dir.x = -dir.x
//                    velocity = -collisionVelocityLoss
//                }
        }
    }
}