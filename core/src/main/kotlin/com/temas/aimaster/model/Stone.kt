package com.temas.aimaster.model

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 07.10.2016
 */
class Stone(startPoint: Vector2, val rad: Float = 20f, val dir: Vector2, var velocity: Float = 2f, world: World) {

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

    val body = createPhysicsBody(world, startPoint)

    fun circle() = Circle(pos, rad)

    fun update(delta: Float) {
        /*if (state == STATE.MOVE) {
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
        } */

        pos.set(PhysicsWorld.toPixels(body.position))
    }

    private fun createPhysicsBody(world: World, startPoint: Vector2): Body {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(PhysicsWorld.toMeters(startPoint))
        bodyDef.linearDamping = 0.35f
        bodyDef.angularDamping = 0.35f

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
        body.linearVelocity = PhysicsWorld.toMeters(dir).scl(2.0f)

        shape.dispose()

        return body
    }
}