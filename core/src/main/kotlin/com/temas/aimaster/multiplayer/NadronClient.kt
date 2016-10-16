package com.temas.aimaster.multiplayer

import com.google.protobuf.MessageLite
import com.google.protobuf.MessageLiteOrBuilder
import com.temas.aimaster.ClientInfoOuterClass
import com.temas.aimaster.ClientProto
import com.temas.aimaster.ClientProto.ClientData
import com.temas.aimaster.Common
import com.temas.aimaster.ServerInfo
import com.temas.aimaster.model.Model
import com.temas.aimaster.model.Stone
import io.nadron.client.app.Session
import io.nadron.client.app.impl.SessionFactory
import io.nadron.client.communication.DeliveryGuaranty.*
import io.nadron.client.communication.NettyMessageBuffer
import io.nadron.client.event.Event
import io.nadron.client.event.Events
import io.nadron.client.event.impl.AbstractSessionEventHandler
import io.nadron.client.util.LoginHelper
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 22.09.2016
 */

private var inPacketCount: Long = 0
private var outPacketCount: Long = 0

class NadronClient(val model: Model) {

    companion object {
        private val taskExecutor = Executors.newSingleThreadScheduledExecutor()
        private val LOG = LoggerFactory.getLogger(NadronClient::class.java)
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss:SSS")
    }

    var latestServerUpdateTime: Long = 0
    var latestClientTime: Long = 0
    var session: Session? = null

    private inner class InBoundHandler(session: Session) : AbstractSessionEventHandler(session) {

        private val prototype: MessageLite = ServerInfo.ModelType.getDefaultInstance()

        override fun onDataIn(event: Event) {
            if (event.timeStamp > latestServerUpdateTime) {
                latestClientTime = System.currentTimeMillis()
                latestServerUpdateTime = event.timeStamp
                val buffer = event.source as NettyMessageBuffer
                val worldState = buffer.readObject { convertToProto(it) } as ServerInfo.ModelType
                ++inPacketCount
                LOG.debug("Received state timestamp = ${simpleDateFormat.format(Date(worldState.timestamp))} " +
                        "x= ${worldState.targetInfo.position.x}, y=${worldState.targetInfo.position.y} Packet number = $inPacketCount")
                updateModel(worldState)
                if (!taskExecutor.isShutdown) {
                    taskExecutor.shutdown()
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
        private fun updateModel(serverModel: ServerInfo.ModelType) {
            val worldState = serverModel.targetInfo
            with(model.target) {
                center.x = worldState.position.x
                center.y = worldState.position.y
                moveDir.x = worldState.moveDir.x
                moveDir.y = worldState.moveDir.y
                radius = worldState.radius
                speed = worldState.speed
            }
        }
    }


    fun update(delta: Float) {
        //TODO send update to server
        if (session != null) {
            sendUpdate()
        }
    }

    private fun sendUpdate() {

        val clientData = ClientData.newBuilder()
        model.stones.forEach {
            val stoneData = createStoneData(it)
            clientData.addStones(stoneData)
        }

        val worldStateBuffer = NettyMessageBuffer()
        val buffer = worldStateBuffer.writeObject({ convertToBuffer(it) }, clientData.build())
        val event = Events.networkEvent(buffer, DeliveryGuarantyOptions.FAST)
        ++outPacketCount
        session!!.onEvent(event)
    }

    fun convertToBuffer(obj : ClientProto.ClientData): ByteBuf {
        return Unpooled.wrappedBuffer((obj as MessageLite).toByteArray())
    }

    private fun createStoneData(s: Stone): ClientProto.Stone.Builder {
        val clientDelta = latestClientTime - s.creationTime
        return ClientProto.Stone.newBuilder()
                .setId(s.id)
                .setTimeDelta(latestServerUpdateTime + clientDelta)
                .setStartPoint(Common.Vector2.newBuilder().setX(s.startPoint.x).setY(s.startPoint.y))
                .setVelocity(Common.Vector2.newBuilder().setX(s.velocity.x).setY(s.velocity.y))

    }

    fun init() {
        val builder = LoginHelper.LoginBuilder().
                username("test" ).
                password("pass").
                connectionKey("defaultRoom").
                nadronTcpHostName("localhost").
                tcpPort(18090).
                nadronUdpHostName("localhost").
                udpPort(50122)

        val loginHelper = builder.build()
        val sessionFactory = SessionFactory(loginHelper)
        val clientSession = sessionFactory.createAndConnectSession()

        val task = Runnable {
            val messageBuffer = NettyMessageBuffer()
            messageBuffer.writeInt(1)
            messageBuffer.writeInt(2)
            val event = Events.networkEvent(messageBuffer, io.nadron.client.communication.DeliveryGuaranty.DeliveryGuarantyOptions.FAST)
            clientSession.onEvent(event)
        }

        taskExecutor.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS)
        val handler = InBoundHandler(clientSession)
        clientSession.addHandler(handler)
        session = clientSession
    }
}