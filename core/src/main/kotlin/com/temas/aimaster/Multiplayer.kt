package main.kotlin.com.temas.aimaster

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 21.01.2016
 */
object Multiplayer {
    val group = NioEventLoopGroup(1)
    val b = Bootstrap()

    init{
        b.group(group).channel(NioDatagramChannel::class.java)
    }

    fun register() {

    }
}