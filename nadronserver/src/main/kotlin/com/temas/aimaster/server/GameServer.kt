package com.temas.aimaster.server

import io.nadron.server.ServerManager
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 21.09.2016
 */

fun main() {
    GameServer().start()
}

class GameServer {
    companion object {
        private val LOG = LoggerFactory.getLogger(GameServer::class.java)
        val ctx = AnnotationConfigApplicationContext(SpringConfig::class.java)
    }

    fun start() {
        // For the destroy method to work.
        ctx.registerShutdownHook()

        // Start the main game server
        val serverManager = ctx.getBean(ServerManager::class.java)
        try {
            //serverManager.startServers();
            serverManager.startServers(18090, 0, 50122)
        } catch (e: Exception) {
            LOG.error("Unable to start servers cleanly: {}", e)
        }

        println("Started servers")
    }

}