package com.temas.aimaster.core

import com.badlogic.gdx.physics.box2d.Body
import com.temas.aimaster.model.AbstractModel
import com.temas.aimaster.model.Target
import com.temas.aimaster.model.PhysicsWorld

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 13.10.2016
 */
class ServerModel(physics: PhysicsWorld): AbstractModel<ServerStone>(physics) {

    fun update(delta: Float) {
        target.update(delta)
        stones.forEach {
            it.update(delta)
        }
        physics.update(delta)
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