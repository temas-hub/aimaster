package com.temas.aimaster.server

import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.core.PhysicalStone
import com.temas.aimaster.model.PhysicsWorld


/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 27.10.2016
 */
class SimulationRoom {
    val physics = PhysicsWorld()


    fun <T: PhysicalStone> simulate(pos: Vector2, vel: Vector2, delta: Float, stoneFactory: (Vector2,Vector2)-> T): T {
        val stone = PhysicalStone(playerId = -1, startPoint = pos, velocity = vel, world = physics.world)

        physics.doPhysicsStep(delta)

        val simulatedStone = stoneFactory(PhysicsWorld.toPixels(stone.body.position), PhysicsWorld.toPixels(stone.body.linearVelocity))
        dispose(stone)
        return simulatedStone
    }

    fun dispose(simulatedStone: PhysicalStone) {
        physics.world.destroyBody(simulatedStone.body)
    }
}