package com.temas.aimaster

import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import kotlin.properties.Delegates

public class MainGame(val playerId: Int? = null) : Game() {

    override fun create() {
        setScreen(MainScreen(ScreenAdapter(), playerId))
    }
}

