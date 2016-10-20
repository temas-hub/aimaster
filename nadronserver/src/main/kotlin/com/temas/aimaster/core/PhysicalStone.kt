package com.temas.aimaster.core

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.temas.aimaster.model.Stone
import io.nadron.app.Player

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 12.10.2016
 */
class PhysicalStone(val serverId: Int = ++ID_CONTER,
                    val player: Player,
                    id: Int,
                    startPoint: Vector2,
                    rad: Float = Stone.DEFAULT_RADIUS,
                    velocity: Vector2,
                    val world: World):
        Stone(id, startPoint, rad, velocity) {

    companion object {
        var ID_CONTER = 0
    }

    val body = createStoneBody()

    override fun update(delta: Float) {
        pos.set(PhysicsWorld.toPixels(body.position))
        velocity.set(PhysicsWorld.toPixels(body.linearVelocity))
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
        body.linearVelocity = PhysicsWorld.toMeters(velocity).scl(2.0f)

        shape.dispose()

        return body
    }




}