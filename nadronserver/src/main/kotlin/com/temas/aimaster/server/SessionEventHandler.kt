package com.temas.aimaster.server

import com.badlogic.gdx.math.Vector2
import com.google.protobuf.MessageLite
import com.temas.aimaster.ClientProto
import com.temas.aimaster.core.PhysicalStone
import com.temas.aimaster.core.ServerModel
import io.nadron.app.PlayerSession
import io.nadron.communication.NettyMessageBuffer
import io.nadron.event.Event
import io.nadron.event.impl.DefaultSessionEventHandler
import io.netty.buffer.ByteBuf
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import kotlin.collections.HashMap
import kotlin.concurrent.write

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 21.09.2016
 */



class SessionEventHandler(val model: ServerModel, session : PlayerSession) : DefaultSessionEventHandler(session) {

    companion object {
        private val LOG = LoggerFactory.getLogger(SessionEventHandler::class.java)
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss:SSS")
        var inPacketCount: Long = 0
    }

    private val lastStoneIds: MutableMap<Int, Int> = HashMap()
    private val prototype: MessageLite = ClientProto.ClientData.getDefaultInstance()
    var lastUpdateTime: Long = 0

    var lastPackIds: MutableMap<Int, Long> = HashMap()
    val simulationRoom = SimulationRoom()

    fun getPlayer() = (session as PlayerSession).player

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
//        LOG.debug("Received client state server timestamp = ${simpleDateFormat.format(Date(event.timeStamp))} " +
//                "stones count= ${clientData.throwActionsCount}, Packet number = $inPacketCount; ")
        updateModel(clientData)
    }

    private fun updateModel(clientData: ClientProto.ClientData) {
        val playerId = getPlayer().id as Int
        val lastPacketId = lastPackIds.getOrPut(playerId, { clientData.packId })
        if (lastPacketId < clientData.packId) {
            model.lock.write {
                clientData.throwActionsList.forEach {
                    val stoneId = it.id
                    val lastStoneId = lastStoneIds.getOrPut(playerId, { stoneId })
                    if (lastStoneId < stoneId) {
                        val stone = PhysicalStone(playerId = playerId,
                                id = stoneId,
                                startPoint = Vector2(it.startPoint.x, it.startPoint.y),
                                velocity = Vector2(it.velocity.x, it.velocity.y),
                                world = model.physics.world)
                        model.stones.add(stone)
                        lastStoneIds[playerId] = stoneId
                    }
                }
            }
            lastPackIds[playerId] = clientData.packId
        }
    }

    private fun convertToProto(msg: ByteBuf): MessageLite {
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