package com.temas.aimaster

import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.core.PhysicalStone
import com.temas.aimaster.model.Model
import com.temas.aimaster.model.ThrownStone
import com.temas.aimaster.multiplayer.NadronClient

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 09/09/2015
 */

class Controller(val model: Model, val renderer: Renderer) {

    val multiPlayer = NadronClient(model)

    fun init() {
        multiPlayer.init()
        renderer.load()
    }

    fun update(delta: Float) {
        model.update(delta)
        multiPlayer.update(delta)
        renderer.render(delta)
    }

    fun launch(point: Vector2) {
//        model.stones.add(PhysicalStone(playerId = multiPlayer.playerId,
//                            startPoint = model.arrow.firstPoint.cpy(),
//                            velocity = model.arrow.dir.cpy().scl(2.0f),
//                            world = model.physics.world))

        model.thrown.add(ThrownStone(model.arrow.firstPoint.cpy(), model.arrow.dir.cpy().scl(2.0f)))
        model.launch(point)
    }

}