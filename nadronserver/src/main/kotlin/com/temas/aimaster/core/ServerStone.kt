package com.temas.aimaster.core

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import io.nadron.app.Player

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 12.10.2016
 */
class ServerStone(val serverId: Int = ++ID_CONTER,
                  val player: Player,
                  id: Int,
                  startPoint: Vector2,
                  velocity: Vector2,
                  world: World):
        PhysicalStone(id, player.id as Int, startPoint, velocity, world) {

    companion object {
        private var ID_CONTER = 0
    }
}