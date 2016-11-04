package com.temas.aimaster.server

import com.temas.aimaster.multiplayer.NadronClient
import com.temas.gameserver.aimmaster.ServerGameRoom
import io.nadron.app.Game
import io.nadron.app.GameRoom
import io.nadron.app.Player
import io.nadron.app.impl.DefaultPlayer
import io.nadron.app.impl.GameRoomSession
import io.nadron.service.LookupService
import io.nadron.util.Credentials

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 28.09.2016
 */
class DefaultLookupService(val game: Game,
                           val roomSessionBuilder: GameRoomSession.GameRoomSessionBuilder) : LookupService {

    var currentRoom : ServerGameRoom? = null

    override fun gameRoomLookup(gameContextKey: Any): GameRoom? {
        //return refKeyGameRoomMap[gameContextKey as String]

        synchronized(this, {
            if (currentRoom == null || currentRoom!!.isFull()) {
                val newRoom = ServerGameRoom(roomSessionBuilder)
                currentRoom = newRoom
            }
        })

        return
    }

    override fun gameLookup(gameContextKey: Any): Game {
        return game
    }

    override fun playerLookup(loginDetail: Credentials): Player? {
        if (loginDetail.username.equals(NadronClient.DEFAULT_LOGIN)) {
            return Users.generateUser()
        }
        return null // all slots are busy
    }
}