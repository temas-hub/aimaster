package com.temas.gameserver.aimmaster

import com.google.protobuf.MessageLite
import com.google.protobuf.MessageLiteOrBuilder
import com.temas.aimaster.Common
import com.temas.aimaster.ServerInfo
import com.temas.aimaster.model.Model
import com.temas.aimaster.model.PhysicsWorld
import io.nadron.app.PlayerSession
import io.nadron.app.impl.GameRoomSession
import io.nadron.communication.DeliveryGuaranty.*
import io.nadron.communication.NettyMessageBuffer
import io.nadron.event.Events
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 30.08.2016
 */

private var outPacketCount: Long = 0

class ServerGameRoom(builder: GameRoomSessionBuilder) : GameRoomSession(builder) {

    val model = Model(PhysicsWorld())

    companion object {
        private val LOG = LoggerFactory.getLogger(ServerGameRoom::class.java)
        private val updateThreadGroup = ThreadGroup("Update thread group")
        private val scheduler = Executors.newSingleThreadScheduledExecutor { r -> Thread(updateThreadGroup, r) }
        private val UPDATE_RATE = 20 //times per second
        private val UPDATE_DELAY = 1000L / UPDATE_RATE
    }

    val players = mutableListOf<PlayerSession>()

    override fun onLogin(playerSession: PlayerSession) {
        LOG.debug("Player with name: '${playerSession.player.name}' was logged in")
        playerSession.addHandler(SessionEventHandler(playerSession))
        if (players.isEmpty()) {
            startUpdateSchedule()
        }
        players.add(playerSession)
    }

    private fun startUpdateSchedule() {
        scheduler.scheduleWithFixedDelay(updateTask, 0, UPDATE_DELAY, TimeUnit.MILLISECONDS)
    }

    private val updateTask = Runnable {
        try {
            model.update(UPDATE_DELAY.toFloat())
            val targetDataPtoto = buildUpdateData()
            getSessions().forEach {
                val worldStateBuffer = NettyMessageBuffer()
                val buffer = worldStateBuffer.writeObject({ convertToBuffer(it) }, targetDataPtoto)
                val event = Events.networkEvent(buffer, DeliveryGuarantyOptions.FAST)
                ++outPacketCount
                it.onEvent(event)
            }

            LOG.debug("Send data packet number = $outPacketCount")
        } catch (ex: Exception) {
            LOG.error("Error during update task", ex)
        }
    }


    fun convertToBuffer(obj : MessageLiteOrBuilder): ByteBuf {
        return Unpooled.wrappedBuffer((obj as MessageLite).toByteArray())
    }

    private fun buildUpdateData(): MessageLiteOrBuilder {

        return ServerInfo.TargetInfo.newBuilder().
            setPosition(
                    Common.Vector2.newBuilder().setX(model.target.center.x).
                            setY(model.target.center.y).build()).
            setMoveDir(Common.Vector2.newBuilder().setX(model.target.moveDir.x).
                    setY(model.target.moveDir.y).build()).
            setRadius(model.target.radius).
            setSpeed(model.target.speed).build()

    }


}