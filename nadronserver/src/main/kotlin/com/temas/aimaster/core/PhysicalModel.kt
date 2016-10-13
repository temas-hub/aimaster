package com.temas.aimaster.core

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
        return PhysicalStone(startPoint = arrow.firstPoint, dir = arrow.dir, world = physics.world)
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
}