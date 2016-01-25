package com.temas.aimaster

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.protobuf.ProtobufEncoder
import io.netty.util.concurrent.GenericFutureListener

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 21.01.2016
 */
object Multiplayer {
    val HOST = System.getProperty("host", "127.0.0.1");
    val PORT = Integer.parseInt(System.getProperty("port", "8080"));

    val group = NioEventLoopGroup(1)
    val b = Bootstrap()
    val packetToByteBufDecoder = object : MessageToMessageDecoder<DatagramPacket>() {
        override fun decode(ctx: ChannelHandlerContext, msg: DatagramPacket, out: MutableList<Any>) {
            out.add(msg.content().retain())
        }
    }
    val handler = object : ChannelInitializer<NioDatagramChannel>() {
        override fun initChannel(ch: NioDatagramChannel) {
            val pipeline  = ch.pipeline()
            pipeline.addLast(packetToByteBufDecoder, ProtobufEncoder())
        }
    }

    init{
        b.group(group).channel(NioDatagramChannel::class.java).handler(handler)
    }

    fun register() {
        // Start the connection attempt.
        try {
            val channelFuture = b.connect(HOST, PORT)
            channelFuture.addListener(GenericFutureListener
            { future ->
                if (!future.isSuccess) {
                    println("Error connecting to $HOST $PORT")
                }
             })
            channelFuture.sync().channel()
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}