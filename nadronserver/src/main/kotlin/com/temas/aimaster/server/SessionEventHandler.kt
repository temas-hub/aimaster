package com.temas.gameserver.aimmaster

import com.badlogic.gdx.math.Vector2
import com.google.protobuf.MessageLite
import com.temas.aimaster.ClientProto
import com.temas.aimaster.core.PhysicalModel
import com.temas.aimaster.core.PhysicalStone
import com.temas.aimaster.model.Stone
import io.nadron.app.PlayerSession
import io.nadron.app.Session
import io.nadron.communication.NettyMessageBuffer
import io.nadron.event.Event
import io.nadron.event.impl.DefaultSessionEventHandler
import io.netty.buffer.ByteBuf
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 21.09.2016
 */

var inPacketCount: Long = 0

class SessionEventHandler(val model: PhysicalModel, val session : PlayerSession) : DefaultSessionEventHandler(session) {

    companion object {
        private val LOG = LoggerFactory.getLogger(SessionEventHandler::class.java)
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss:SSS")
    }

    private val prototype: MessageLite = ClientProto.ClientData.getDefaultInstance()
    var lastUpdateTime: Long = 0
    var lastStoneId: Int = -1


    override fun onDataIn(event: Event) {
        if (event.timeStamp > lastUpdateTime) {
            lastUpdateTime = event.timeStamp
            handleClientData(event)
        }

    }

    private fun  handleClientData(event: Event) {
        val buffer = event.source as NettyMessageBuffer
        val clientData = buffer.readObject { convertToProto(it) } as ClientProto.ClientData
        ++inPacketCount
        LOG.debug("Received client state server timestamp = ${simpleDateFormat.format(Date(event.timeStamp))} " +
                "stones count= ${clientData.stonesCount}, Packet number = $inPacketCount; ")
        updateModel(clientData)
    }

    private fun updateModel(clientData: ClientProto.ClientData) {

        clientData.stonesList.forEach {
            if (it.id > lastStoneId) {
                val clientStone = Stone(startPoint = Vector2(it.startPoint.x, it.startPoint.y),
                                        velocity = Vector2(it.velocity.x, it.velocity.y))
                val timeToReplay = System.currentTimeMillis() - it.timeDelta.toFloat()
                if (timeToReplay < 0) {
                    throw IllegalStateException("Client time time cannot be later than server time")
                }
                clientStone.update(timeToReplay)
                val serverStone =
                        PhysicalStone(player = session.player,
                                        id = it.id,
                                        startPoint = clientStone.pos,
                                        velocity = clientStone.velocity,
                                        world = model.physics.world)
                lastStoneId = it.id
                model.stones.add(serverStone)
            }
        }
    }

    fun convertToProto(msg: ByteBuf): MessageLite {
        val array: ByteArray
        val offset: Int
        val length = msg.readableBytes()
        if (msg.hasArray()) {
            array = msg.array()
            offset = msg.arrayOffset() + msg.readerIndex()
        } else {
            array = ByteArray(length)
            msg.getBytes(msg.readerIndex(), array, 0, length)
            offset = 0
        }

        return prototype.parserForType.parseFrom(array, offset, length)
    }

}