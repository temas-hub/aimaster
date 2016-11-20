package com.temas.aimaster.server

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.temas.aimaster.dao.UserRepository
import com.temas.gameserver.aimmaster.ServerGameRoom
import io.nadron.app.Player
import io.nadron.app.impl.DefaultPlayer
import org.slf4j.LoggerFactory

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 03.11.2016
 */
object Users {

    private val LOG = LoggerFactory.getLogger(Users::class.java)

    init {
        try {
            LOG.debug(UserRepository.db.toString())
        } catch (e: Exception) {
            LOG.error("Error during user repository initialization", e)
        }

    }

    fun generateUser(userId: Int = UserRepository.lastUserId.incrementAndGet()): Player {
        val nextUserName = "player" + userId
        val nextUserEmail = generateEmail(nextUserName)
        UserRepository.storeUser(userId, nextUserName, nextUserEmail)

        return DefaultPlayer(userId, nextUserName, nextUserEmail)
    }

    private fun generateEmail(nextUserName: String) = nextUserName + "@email.com"

    fun findUser(userId: Int): Player? {
        val userInfo = UserRepository.findRecordById(userId)
        if (userInfo != null) {
            return DefaultPlayer(userId, userInfo.first, userInfo.second)
        }
        return null
    }


}