package com.temas.aimaster.server

import com.temas.aimaster.multiplayer.NadronClient
import io.nadron.app.Game
import io.nadron.app.GameRoom
import io.nadron.app.impl.GameRoomSession
import io.nadron.app.impl.SimpleGame
import io.nadron.protocols.Protocol
import io.nadron.service.LookupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 30.08.2016
 */

@Configuration
@ImportResource("classpath:beans.xml")
open class SpringConfig {

    //TODO implement it own implementation of the protocol with Protobuf
    @Autowired
    @Qualifier("messageBufferProtocol")
    private lateinit var messageBufferProtocol: Protocol

    private val game : Game = SimpleGame(1, "aimmaster")

    @Bean(name = arrayOf("gameRoom"))
    open fun gameRoom() : GameRoom {
        val sessionBuilder = GameRoomSession.GameRoomSessionBuilder()
        sessionBuilder.
                parentGame(game).
                gameRoomName(NadronClient.DEFAULT_ROOM_NAME).
                protocol(messageBufferProtocol)
        return ServerGameRoom(sessionBuilder)
    }


    @Bean(name = arrayOf("lookupService"))
    open fun lookupService(): LookupService {
//        val refKeyGameRoomMap = HashMap<String, GameRoom>()
//        val gameRoom = gameRoom()
//        refKeyGameRoomMap.put(gameRoom.gameRoomName, gameRoom)
        val sessionBuilder = GameRoomSession.GameRoomSessionBuilder()
        sessionBuilder.
                parentGame(game).
                protocol(messageBufferProtocol)
        val service = DefaultLookupService(game, sessionBuilder)
        return service
    }

}