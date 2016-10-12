package com.temas.aimaster.model

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 12.10.2016
 */
class PhysicalStone(startPoint: Vector2,
                    rad: Float = Stone.DEFAULT_RADIUS,
                    dir: Vector2, velocity:
                    Float = Stone.START_VELOCITY,
                    val world: World):
        Stone(startPoint, rad, dir, velocity) {

    val body = createStoneBody()

    override fun update(delta: Float) {
        pos.set(body.position)
    }

    private fun createStoneBody(): Body {
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