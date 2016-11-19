package com.temas.aimaster.server

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.temas.aimaster.dao.UserRepository
import io.nadron.app.Player
import io.nadron.app.impl.DefaultPlayer

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 03.11.2016
 */
object Users {

    fun generateUser(): Player {
        val lastUserId = UserRepository.lastUserId.incrementAndGet()
        val nextUserName = "player" + lastUserId
        val nextUserEmail = generateEmail(nextUserName)
        UserRepository.storeUser(lastUserId, nextUserName, nextUserEmail)

        return DefaultPlayer(lastUserId, nextUserName, nextUserEmail)
    }

    private fun generateEmail(nextUserName: String) = nextUserName + "@email.com"

    fun createUser(id: Int, user: String): Player {
        return DefaultPlayer(id, user, generateEmail(user))
    }

    fun findUser(userId: Int): Player? {
        val userInfo = UserRepository.findRecordById(userId)
        if (userInfo != null) {
            return DefaultPlayer(userId, userInfo.first, userInfo.second)
        }
        return null
    }


}