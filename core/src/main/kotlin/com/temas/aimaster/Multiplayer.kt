package com.temas.aimaster

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.netty.handler.codec.protobuf.ProtobufEncoder
import io.netty.util.concurrent.GenericFutureListener
//import com.temas.aimaster.UpdateRequestOuterClass.UpdateRequest as Request
//import com.temas.aimaster.UpdateRequestOuterClass.UserInfo as UserInfo


/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 21.01.2016
 */
object Multiplayer {
    public var registered = false

    val HOST = System.getProperty("host", "127.0.0.1");
    val PORT = Integer.parseInt(System.getProperty("port", "8080"));

    val group = NioEventLoopGroup(1)
    val b = Bootstrap()
    val responseDecoder = object : MessageToMessageDecoder<DatagramPacket>() {
        override fun decode(ctx: ChannelHandlerContext, msg: DatagramPacket, out: MutableList<Any>) {
            out.add(msg.content().retain())
        }
    }
    val responseListener = object : ChannelInboundHandlerAdapter() {
        override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
            registered = true
            println(msg)
        }

        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            cause.printStackTrace()
        }
    }

    val channelHandler = object : ChannelInitializer<NioDatagramChannel>() {
        override fun initChannel(ch: NioDatagramChannel) {
            val pipeline  = ch.pipeline()
            pipeline.addLast(responseDecoder,
                            ProtobufEncoder(),
                            ProtobufDecoder(ServerInfo.TargetInfo.getDefaultInstance()),
                            responseListener)
        }
    }

    init{
        b.group(group).channel(NioDatagramChannel::class.java).handler(channelHandler)
    }

    public fun init() {
        // Start the connection attempt.
        try {
            val channelFuture = b.connect(HOST, PORT)
            channelFuture.addListener(GenericFutureListener
            { future ->
                if (!future.isSuccess) {
                    println("Error connecting to $HOST $PORT")
                }
             })
            val channel = channelFuture.sync().channel()
//            val request = Request.newBuilder().setUserInfo(UserInfo.newBuilder().setName(channel.localAddress().toString())).build()
//            channel.writeAndFlush(request)
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}