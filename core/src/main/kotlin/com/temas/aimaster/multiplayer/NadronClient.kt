package com.temas.aimaster.multiplayer

import com.google.protobuf.MessageLite
import com.temas.aimaster.ModelType
import com.temas.aimaster.model.Model
import io.nadron.client.app.Session
import io.nadron.client.app.impl.SessionFactory
import io.nadron.client.communication.NettyMessageBuffer
import io.nadron.client.event.Event
import io.nadron.client.event.Events
import io.nadron.client.event.impl.AbstractSessionEventHandler
import io.nadron.client.util.LoginHelper
import io.netty.buffer.ByteBuf
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 22.09.2016
 */

private var inPacketCount: Long = 0
class NadronClient(val model: Model) {

    companion object {
        private val taskExecutor = Executors.newSingleThreadScheduledExecutor()
        private val LOG = LoggerFactory.getLogger(NadronClient::class.java)
    }

    private inner class InBoundHandler(session: Session) : AbstractSessionEventHandler(session) {

        private val prototype: MessageLite = ModelType.TargetInfo.getDefaultInstance()
        private var latestUpdateTime: Long = 0

        override fun onDataIn(event: Event) {
            if (event.timeStamp > latestUpdateTime) {
                latestUpdateTime = event.timeStamp
                val buffer = event.source as NettyMessageBuffer
                val worldState = buffer.readObject { convertToProto(it) } as ModelType.TargetInfo
                ++inPacketCount
                LOG.debug("Received state x= ${worldState.position.x}, y=${worldState.position.y} Packet number = $inPacketCount")
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
    }

    private fun updateModel(worldState: ModelType.TargetInfo) {
        with(model.target) {
            center.x = worldState.position.x
            center.y = worldState.position.y
            moveDir.x = worldState.moveDir.x
            moveDir.y = worldState.moveDir.y
            radius = worldState.radius
            speed = worldState.speed
        }
    }

    fun update(delta: Float) {
        //TODO send update to server
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
        val session = sessionFactory.createAndConnectSession()

        val task = Runnable {
            val messageBuffer = NettyMessageBuffer()
            messageBuffer.writeInt(1)
            messageBuffer.writeInt(2)
            val event = Events.networkEvent(messageBuffer, io.nadron.client.communication.DeliveryGuaranty.DeliveryGuarantyOptions.FAST)
            session.onEvent(event)
        }

        taskExecutor.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS)
        val handler = InBoundHandler(session)
        session.addHandler(handler)
    }
}