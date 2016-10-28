package com.temas.aimaster.core

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.temas.aimaster.model.PhysicsWorld
import com.temas.aimaster.model.Stone

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 12.10.2016
 */
open class PhysicalStone(id: Int = ++Stone.ID_COUNTER,
                         playerId: Int,
                        startPoint: Vector2,
                        velocity: Vector2,
                        val world: World):
        Stone(id, playerId = playerId, startPoint = startPoint, velocity = velocity) {

    companion object {
        private val STOP_SPEED_SQUARE = 0.04f
    }

    val body = createStoneBody()

    override fun update(delta: Float) {
        if (state == STATE.MOVE) {
            pos.set(PhysicsWorld.toPixels(body.position))
            velocity.set(PhysicsWorld.toPixels(body.linearVelocity))
            if (body.linearVelocity.len2() < STOP_SPEED_SQUARE) {
                body.linearVelocity = Vector2(0f,0f)
                body.angularVelocity = 0f
            }
        }
    }

    private fun createStoneBody(): Body {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(PhysicsWorld.toMeters(startPoint))
        bodyDef.linearDamping = Stone.VELOCITY_DAMPING
        bodyDef.angularDamping = Stone.VELOCITY_DAMPING

        val shape = CircleShape()
        shape.position.set(PhysicsWorld.toMeters(startPoint))
        shape.radius = PhysicsWorld.toMeters(rad)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.density = 0.2f
        fixtureDef.friction = 0.8f
        fixtureDef.restitution = 0.75f
        val body = world.createBody(bodyDef)
        body.createFixture(fixtureDef)
        body.userData = this
        body.linearVelocity = PhysicsWorld.toMeters(velocity).scl(2.0f)

        shape.dispose()

        return body
    }

    override fun updateFromServer(position: Vector2, velocity: Vector2) {
        body.linearVelocity = velocity
        body.position.set(position)
    }
}