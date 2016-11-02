package com.temas.aimaster.core

import com.badlogic.gdx.physics.box2d.*
import com.temas.aimaster.model.Intersections
import com.temas.aimaster.model.PhysicsWorld
import com.temas.aimaster.model.Target

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
        //body.linearVelocity = PhysicsWorld.toMeters(getRandomVector()).scl(2.0f)

        shape.dispose()

        return body
    }

    override fun update(delta: Float) {
        //center.set(body.position)
        if (moveDir.isZero) {
            moveDir = getRandomVector()
        }
        center.add(moveDir.cpy().scl(speed * delta))
        if (center.x - radius < Intersections.screenBounds.x || center.x + radius> Intersections.screenBounds.width) {
            moveDir.x = -moveDir.x
        }
        if (center.y - radius < Intersections.screenBounds.y || center.y + radius> Intersections.screenBounds.height) {
            moveDir.y = -moveDir.y
        }
        body.position.set(PhysicsWorld.toMeters(center))
    }

    fun start() {
        //body.applyForceToCenter(PhysicsWorld.toMeters(getRandomVector().scl(Model.TARGET_SPEED)).scl(body.mass), true)
        //body.linearVelocity = PhysicsWorld.toMeters(getRandomVector().scl(Model.TARGET_SPEED))
    }

}