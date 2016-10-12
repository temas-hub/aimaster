package com.temas.aimaster.model

import com.badlogic.gdx.physics.box2d.*
import com.temas.aimaster.Target

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 12.10.2016
 */
class PhysicalTarget(speed: Float, radius: Float,val world: World) : Target(speed, radius) {

    val body = createPhysicalBody()

    private fun createPhysicalBody(): Body {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(PhysicsWorld.toMeters(CENTRAL_POINT))

        val shape = CircleShape()
        shape.position.set(PhysicsWorld.toMeters(CENTRAL_POINT))
        shape.radius = PhysicsWorld.toMeters(radius)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.density = 0.2f
        fixtureDef.restitution = 0.75f
        fixtureDef.isSensor = true

        val body = world.createBody(bodyDef)
        body.createFixture(fixtureDef)
        body.userData = this
        body.linearVelocity = PhysicsWorld.toMeters(getRandomVector()).scl(2.0f)

        shape.dispose()

        return body
    }

    override fun update(delta: Float) {
        center.set(body.position)
    }

}