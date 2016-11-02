package com.temas.aimaster.model

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.core.PhysicalStone

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 07.10.2016
 */
open class Stone(val id: Int,
                 val playerId: Int,
                 val startPoint: Vector2,
                 val rad: Float = DEFAULT_RADIUS,
                 var velocity: Vector2,
                 val creationTime:Long = System.currentTimeMillis()) {

    companion object {
        var ID_COUNTER = 0
        val DEFAULT_RADIUS = 20f
    }

    enum class STATE {
        MOVE,
        STAY,
        STICKED
    }

    constructor(playerId: Int, startPoint: Vector2, velocity: Vector2):
        this(id = ++ID_COUNTER, playerId = playerId, startPoint = startPoint, velocity = velocity)

    val pos = Vector2(startPoint.x, startPoint.y)
    var state: STATE = STATE.MOVE

    fun circle() = Circle(pos, rad)

    open fun update(delta: Float) {
        if (state == STATE.MOVE) {
            val newX = pos.x + velocity.x * delta
            val newY = pos.y + velocity.y * delta
            pos.set(newX, newY)
                //velocity.sub(velocity.cpy().scl(PhysicalStone.VELOCITY_DAMPING * delta))
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

    open fun updateFromServer(position: Vector2, velocity: Vector2) {
        pos.set(position.x, position.y)
        velocity.set(velocity.x, velocity.y)
    }
}