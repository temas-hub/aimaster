package com.temas.aimaster.server

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import io.nadron.app.Player
import io.nadron.app.impl.DefaultPlayer

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 03.11.2016
 */
object Users {

    val LAST_USER_ID = "lastUserId"

    val preferences: Preferences = Gdx.app.getPreferences("users")
    var lastUserId = preferences.getInteger(LAST_USER_ID, 0)

    fun generateUser(): Player {
        val nextUserName = "player" + (++lastUserId)
        val nextUserEmail = nextUserName + "@email.com"

        preferences.putInteger(LAST_USER_ID, lastUserId)
        return DefaultPlayer(lastUserId, nextUserName, nextUserEmail)
    }
}