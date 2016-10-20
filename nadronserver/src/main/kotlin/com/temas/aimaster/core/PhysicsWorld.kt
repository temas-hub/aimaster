package com.temas.aimaster.core

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.temas.aimaster.model.Intersections

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 10.10.2016
 */
class PhysicsWorld {
    companion object {
        val PIXELS_TO_METERS = 100f
        val STONE_WEIGHT = 17f
        val STONE_RADIUS = 7.5f
        val TIME_STEP = 1f/60f
        val VELOCITY_ITERATIONS = 6
        val POSITION_ITERATIONS = 2

        fun toMeters(v: Vector2) = v.cpy().scl(1/ PIXELS_TO_METERS)
        fun toPixels(v: Vector2) = v.cpy().scl(PIXELS_TO_METERS)
        fun toMeters(v: Float) = v / PIXELS_TO_METERS

        fun toPixels(v: Float)= v * PIXELS_TO_METERS
    }

    val world = World(Vector2(0f,0f), true)
    val screenBody = createBoardBody(world)


    fun createBoardBody(world: World): Body {

        val w = toMeters(Intersections.screenBounds.width - 10)
        val h = toMeters(Intersections.screenBounds.height - 10)

        val bottomDef = BodyDef()
        bottomDef.type = BodyDef.BodyType.StaticBody
        bottomDef.position.set(0f, 0f)

        val shape = ChainShape()
        shape.createLoop(arrayOf(Vector2(0f, 0f), Vector2(0f, h), Vector2(w, h), Vector2(w, 0f)))

        val fixture = FixtureDef()
        fixture.shape = shape
        fixture.friction = 0.7f

        val body = world.createBody(bottomDef)
        body.createFixture(fixture)
        shape.dispose()

        return body
    }


    fun update(delta: Float) {
        world.step(delta, VELOCITY_ITERATIONS, POSITION_ITERATIONS)
    }


    private fun doPhysicsStep(deltaTime: Float) {
        var accumulator = 0f
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        val frameTime = Math.min(deltaTime, 0.25f)
        accumulator += frameTime
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS)
            accumulator -= TIME_STEP
        }
    }

}