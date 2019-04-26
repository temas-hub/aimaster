package com.temas.aimaster.server

import com.temas.aimaster.multiplayer.NadronClient
import io.nadron.app.Game
import io.nadron.app.GameRoom
import io.nadron.app.Player
import io.nadron.app.impl.GameRoomSession
import io.nadron.service.LookupService
import io.nadron.util.Credentials
import org.slf4j.LoggerFactory

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 28.09.2016
 */
class DefaultLookupService(val game: Game,
                           val roomSessionBuilder: GameRoomSession.GameRoomSessionBuilder) : LookupService {

    companion object {
        private var ROOM_ID = 0
        private val LOG = LoggerFactory.getLogger(DefaultLookupService::class.java)
    }

    var currentRoom : ServerGameRoom? = null
    val rooms = mutableListOf<ServerGameRoom>()


    override fun gameRoomLookup(gameContextKey: Any): GameRoom? {
        //return refKeyGameRoomMap[gameContextKey as String]
        synchronized(this) {
            if (currentRoom == null || currentRoom!!.isFull()) {
                roomSessionBuilder.gameRoomName(game.gameName + (ROOM_ID++))
                roomSessionBuilder.sessions(HashSet())
                val newRoom = ServerGameRoom(roomSessionBuilder)
                rooms.add(newRoom)
                currentRoom = newRoom
            }
            return currentRoom
        }

    }

    override fun gameLookup(gameContextKey: Any): Game {
        return game
    }

    override fun playerLookup(loginDetail: Credentials): Player? {
        if (loginDetail.username.equals(NadronClient.DEFAULT_LOGIN)) {
            return Users.generateUser()
        }
        val userId = loginDetail.username.toInt()

        val user = Users.findUser(userId)
        if (user == null) {
            LOG.error("User with login ${loginDetail.username} is not found")
            return Users.generateUser(userId)
        }
        return user
    }
}