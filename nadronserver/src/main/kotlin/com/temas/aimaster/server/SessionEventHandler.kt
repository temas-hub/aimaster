package com.temas.gameserver.aimmaster

import io.nadron.app.Session
import io.nadron.event.Event
import io.nadron.event.impl.DefaultSessionEventHandler

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 21.09.2016
 */
class SessionEventHandler(session : Session) : DefaultSessionEventHandler(session) {


    override fun onDataIn(event: Event) {
        handleClientData(event.source)
    }

    private fun  handleClientData(source: Any) {
        println(source)
    }

}