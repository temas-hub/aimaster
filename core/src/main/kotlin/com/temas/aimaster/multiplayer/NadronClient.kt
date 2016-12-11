package com.temas.aimaster.multiplayer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.google.protobuf.MessageLite
import com.temas.aimaster.ClientProto
import com.temas.aimaster.ClientProto.ClientData
import com.temas.aimaster.Common
import com.temas.aimaster.ServerInfo
import com.temas.aimaster.model.Model
import com.temas.aimaster.model.Stone
import com.temas.aimaster.model.ThrownStone
import io.nadron.client.app.Session
import io.nadron.client.app.impl.SessionFactory
import io.nadron.client.communication.DeliveryGuaranty.*
import io.nadron.client.communication.NettyMessageBuffer
import io.nadron.client.event.Event
import io.nadron.client.event.Events
import io.nadron.client.event.impl.AbstractSessionEventHandler
import io.nadron.client.event.impl.StartEventHandler
import io.nadron.client.util.LoginHelper
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import kotlin.concurrent.write

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 22.09.2016
 */

private var inPacketCount: Long = 0


class NadronClient(val model: Model) {

    companion object {
        // server info
        val SERVER_HOST_NAME = "localhost"
        val DEFAULT_LOGIN = "default_login"
        val DEFAULT_PASSWORD = ""
        val DEFAULT_ROOM_NAME = "defaultRoom"
        val PLAYER_ID = "playerId"

        // logger
        private val LOG = LoggerFactory.getLogger(NadronClient::class.java)
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss:SSS")

        private var outPacketCount: Long = 0
    }

    @Volatile var latestServerUpdateTime: Long = 0
    @Volatile var latestClientTime: Long = 0
    lateinit var session: Session
    var playerId: Int = -1
        get() {
            if (field == -1) {
                LOG.error("Player id is not initialized")
            }
            return field
        }

    private inner class InBoundHandler(session: Session) : AbstractSessionEventHandler(session) {

        private val prototype: MessageLite = ServerInfo.ModelType.getDefaultInstance()

        override fun onDataIn(event: Event) {
            if (playerId == -1) return

            val buffer = event.source as NettyMessageBuffer
            val worldState = buffer.readObject { convertToProto(it) } as ServerInfo.ModelType
            if (worldState.timestamp > latestServerUpdateTime) {
                latestClientTime = System.currentTimeMillis()
                latestServerUpdateTime = worldState.timestamp
                ++inPacketCount
//                LOG.debug("Received state timestamp = ${simpleDateFormat.format(Date(worldState.timestamp))} " +
//                        "x= ${worldState.targetInfo.position.x}, y=${worldState.targetInfo.position.y} Packet number = $inPacketCount")
                updateModel(worldState)
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
            val serverTargetInfo = serverModel.targetInfo
            model.lock.write {
                with(model.target) {
                    center.x = serverTargetInfo.position.x
                    center.y = serverTargetInfo.position.y
                    moveDir.x = serverTargetInfo.moveDir.x
                    moveDir.y = serverTargetInfo.moveDir.y
                    radius = serverTargetInfo.radius
                    speed = serverTargetInfo.speed
                }
                serverModel.stonesList.forEach { s ->
                    val stone = model.stones.find { it.id == s.id && it.playerId == s.playerId }
                    if (stone == null) {
                        val newStone = Stone(s.id, s.playerId, startPoint = Vector2(s.position.x, s.position.y),
                                velocity = Vector2(s.velocity.x, s.velocity.y))
                        model.stones.add(newStone)
                        if (s.playerId == playerId) {
                            model.thrown.removeAll { it.id == s.id }
                        }
                    } else {
                        stone.updateFromServer(Vector2(s.position.x, s.position.y), Vector2(s.velocity.x, s.velocity.y))
                    }
                }
            }
        }
    }


    fun update(delta: Float) {
        //TODO send update to server
        if (session.isWriteable) {
            sendUpdate()
        }
    }

    private fun sendUpdate() {

        val clientData = ClientData.newBuilder().setTimeStamp(System.currentTimeMillis()).setPackId(++outPacketCount)
        model.thrown.filter { it.id > 0 }.forEach {
            val stoneData = createThrownStoneData(it)
            clientData.addThrowActions(stoneData)
        }

        val worldStateBuffer = NettyMessageBuffer()
        val buffer = worldStateBuffer.writeObject({ convertToBuffer(it) }, clientData.build())
        val event = Events.networkEvent(buffer, DeliveryGuarantyOptions.FAST)
        session.onEvent(event)
    }

    fun convertToBuffer(obj : ClientProto.ClientData): ByteBuf {
        return Unpooled.wrappedBuffer((obj as MessageLite).toByteArray())
    }

    private fun createThrownStoneData(s: ThrownStone): ClientProto.ThrownStone.Builder {
        return ClientProto.ThrownStone.newBuilder()
                .setId(s.id)
                .setStartPoint(Common.Vector2.newBuilder().setX(s.pos.x).setY(s.pos.y))
                .setVelocity(Common.Vector2.newBuilder().setX(s.vel.x).setY(s.vel.y))

    }

    fun init() {
        val preferences = Gdx.app.getPreferences("aimaster.saves")
        var pId = preferences.getInteger(PLAYER_ID)
        val login = if (model.playerId != null) model.playerId.toString()
                else if (pId == null) DEFAULT_LOGIN else pId.toString()

        val builder = LoginHelper.LoginBuilder().
                username(login).
                password(DEFAULT_PASSWORD).
                connectionKey(DEFAULT_ROOM_NAME).
                nadronTcpHostName(SERVER_HOST_NAME).
                tcpPort(18090).
                nadronUdpHostName(SERVER_HOST_NAME).
                udpPort(50122)

        val loginHelper = builder.build()
        val sessionFactory = SessionFactory(loginHelper)
        val clientSession = sessionFactory.createSession()
        val handler = InBoundHandler(clientSession)
        clientSession.addHandler(handler)
        if (model.playerId == null && pId == null) {
            clientSession.addHandler(object : StartEventHandler(clientSession) {
                override fun onEvent(event: Event) {
                    val buffer = event.source as NettyMessageBuffer
                    if (buffer.readableBytes() == 0) {
                        LOG.error("Player id was not received from server")
                        throw IllegalStateException()
                    }
                    playerId = buffer.readInt()
                    preferences.putInteger(PLAYER_ID, playerId)
                    preferences.flush()
                    session.removeHandler(this)
                }
            })
        } else {
            playerId = model.playerId ?: pId
        }
        sessionFactory.connectSession(clientSession)
        session = clientSession
    }
}