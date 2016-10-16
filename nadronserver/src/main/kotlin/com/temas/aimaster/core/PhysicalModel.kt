package com.temas.aimaster.core

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.temas.aimaster.model.Target
import com.temas.aimaster.model.Model
import com.temas.aimaster.model.Stone

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 13.10.2016
 */
class PhysicalModel(val physics: PhysicsWorld): Model() {

    override fun update(delta: Float) {
        target.update(delta)
        stones.forEach {
            it.update(delta)
        }
        physics.update(delta)
    }

    override open fun createStone(): Stone {
        throw UnsupportedOperationException("Refactor this") //TODO refactor
    }

    override fun createTarget(): Target {
        return PhysicalTarget(TARGET_SPEED, TARGET_RADIUS, physics.world)
    }

    fun getPhysicalTarget(): Body {
        return (target as PhysicalTarget).body
    }

    fun startGameSession() {
        (target as PhysicalTarget).start()
    }

    fun addStone(id: Int, startX: Float, startY: Float, velocityX: Float, velocityY: Float): Stone {
        val physicalStone = PhysicalStone(id, startPoint = Vector2(startX, startY),
                velocity = Vector2(velocityX, velocityY),
                world = physics.world)
        stones.add(physicalStone)

        return physicalStone
    }
}