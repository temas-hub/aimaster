package com.temas.aimaster

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.temas.aimaster.model.Model
import com.temas.aimaster.renderer.Renderer

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 08/09/2015
 */

class MainScreen(s: Screen) : Screen by s {

    val model = Model()
    val renderer = Renderer(model)
    val controller = Controller(model, renderer)

    init {
        controller.init()
        Gdx.input.setInputProcessor(DefaultInputProcessor(InputAdapter(), controller))
    }


    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.45f, 0.45f, 0.45f, 1f)  //#727272
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        controller.update(delta)
    }

    override fun resize(width: Int, height: Int) {
        renderer.setSize(width, height)
    }
}



