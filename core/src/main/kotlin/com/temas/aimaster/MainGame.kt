package com.temas.aimaster

import com.badlogic.gdx.*

class MainGame(val playerId: Int? = null) : Game() {

    override fun create() {
        setScreen(MainScreen(ScreenAdapter(), playerId))
    }
}

