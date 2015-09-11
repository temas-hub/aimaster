package com.temas.aimaster

import com.temas.aimaster.model.Model
import com.temas.aimaster.renderer.Renderer

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 09/09/2015
 */

class Controller(val model: Model, val renderer: Renderer) {
    fun init() {
        renderer.load()
    }

    fun update(delta: Float) {
        model.update(delta)
        renderer.render(delta)
    }

}