package com.temas.aimaster.server

import io.nadron.app.Game
import io.nadron.app.GameRoom
import io.nadron.app.Player
import io.nadron.app.impl.DefaultPlayer
import io.nadron.service.LookupService
import io.nadron.util.Credentials

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 28.09.2016
 */
class DefaultLookupService(val game: Game,
                           val refKeyGameRoomMap: Map<String, GameRoom>) : LookupService {

    private lateinit var redSlot: Player
    private lateinit var blueSlot: Player


    override fun gameRoomLookup(gameContextKey: Any): GameRoom? {
        return refKeyGameRoomMap[gameContextKey as String]
    }

    override fun gameLookup(gameContextKey: Any): Game {
        return game
    }

    override fun playerLookup(loginDetail: Credentials): Player? {
        if (redSlot == null) {
            redSlot = DefaultPlayer(1, "red", null)
            return redSlot
        } else if (blueSlot == null) {
            blueSlot = DefaultPlayer(2, "blue", null)
            return blueSlot
        }
        return null // all slots are busy
    }
}