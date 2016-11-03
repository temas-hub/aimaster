package com.temas.gameserver.aimmaster

import com.badlogic.gdx.math.Vector2
import com.google.protobuf.MessageLite
import com.temas.aimaster.ClientProto
import com.temas.aimaster.core.PhysicalStone
import com.temas.aimaster.core.ServerModel
import com.temas.aimaster.server.SimulationRoom
import io.nadron.app.PlayerSession
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



class SessionEventHandler(val model: ServerModel, val session : PlayerSession) : DefaultSessionEventHandler(session) {

    companion object {
        private val LOG = LoggerFactory.getLogger(SessionEventHandler::class.java)
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss:SSS")
        var inPacketCount: Long = 0
    }

    private val prototype: MessageLite = ClientProto.ClientData.getDefaultInstance()
    var lastUpdateTime: Long = 0
    var lastStoneId: Int = -1

    var lastPackId: Long = -1
    val simulationRoom = SimulationRoom()


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
                "stones count= ${clientData.throwActionsCount}, Packet number = $inPacketCount; ")
        updateModel(clientData)
    }

    private fun updateModel(clientData: ClientProto.ClientData) {
        if (lastPackId < clientData.packId) {
            lastPackId = clientData.packId
            val currentTime = System.currentTimeMillis()
            clientData.throwActionsList.forEach {
                if (it.id > lastStoneId) {
                    val stone = PhysicalStone(playerId = session.player.id as Int,
                            id = it.id,
                            startPoint = Vector2(it.startPoint.x, it.startPoint.y),
                            velocity = Vector2(it.velocity.x, it.velocity.y),
                            world = model.physics.world)
                    lastStoneId = it.id
                    model.stones.add(stone)
                }
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