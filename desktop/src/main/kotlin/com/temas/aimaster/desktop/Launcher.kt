package com.temas.aimaster.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.temas.aimaster.MainGame

fun main(args: Array<String>) {
    val cfg = LwjglApplicationConfiguration()
    cfg.title = "Aimaster"
    //cfg.useGL30 = true;
    if (args.size > 1) { // use -PappArgs=[697,1080] as gradle script parameter
        cfg.width = Integer.valueOf(args[0])
        cfg.height = Integer.valueOf(args[1])
    } else {        // 0.6453
        cfg.width = 452
        cfg.height = 700
    }
    cfg.x = -1 // center
    cfg.y = -1

    val playerId = if (args.isNotEmpty()) Integer.valueOf(args[0]) else null

    LwjglApplication(MainGame(playerId), cfg)
}
