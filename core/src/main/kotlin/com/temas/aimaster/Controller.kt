package com.temas.aimaster

import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.model.Model
import com.temas.aimaster.Renderer

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 09/09/2015
 */

class Controller(val model: Model, val renderer: Renderer) {
    fun init() {
        renderer.load()
        Multiplayer.init()
    }

    fun update(delta: Float) {
        model.update(delta)
        renderer.render(delta)
    }

    fun launch(point: Vector2) {
        model.createBall(point)
        model.launch(point)
    }

}