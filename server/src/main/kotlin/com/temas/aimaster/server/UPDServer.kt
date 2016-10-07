package com.temas.aimaster.server

import com.temas.aimaster.server.decoding.RequestDecoder
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.handler.codec.MessageToMessageEncoder
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import com.temas.aimaster.UpdateRequestOuterClass.UpdateRequest as Request
import com.temas.aimaster.UpdateRequestOuterClass.UserInfo as UserInfo

import com.temas.aimaster.UpdateResponseOuterClass.UpdateResponse as Response
import com.temas.aimaster.UpdateResponseOuterClass.TargetInfo as TargetInfo

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 21.01.2016
 */
public class UDPServer {

    public var clients = ArrayList<ClientInfo>()
    val clientUpdateThreadGroup = ThreadGroup("Clients update group")
    val executor = Executors.newSingleThreadScheduledExecutor { Thread(clientUpdateThreadGroup, it) }

    companion object {
        @JvmStatic public fun main (args: Array<String>) {
            UDPServer().start(args);
        }
    }

    /*val requestDecoder = object : MessageToMessageDecoder<DatagramPacket>() {
        override fun decode(ctx: ChannelHandlerContext, msg: DatagramPacket, out: MutableList<Any>) {
            out.add(ClientInfo(msg.content().retain(), null, msg.sender()))
        }
    } */

    val inboundHandler = object : ChannelInboundHandlerAdapter() {
        override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
            println("com.temas.netty.poc.Server read $msg")
            if (msg is ClientInfo) {
                clients.add(msg)
            }
            println("Clients are $clients")
        }

        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            cause.printStackTrace()
        }
    }


    val responseEncoder = object : MessageToMessageEncoder<UpdateMessage>() {
        override fun encode(ctx: ChannelHandlerContext?, msg: UpdateMessage, out: MutableList<Any>) {
            out.add(DatagramPacket(Unpooled.wrappedBuffer(msg.message.toByteArray()), msg.address))
        }
    }

    val PORT = Integer.parseInt(System.getProperty("port", "8080"));

    fun start(args: Array<String>) {
        val workerGroup = NioEventLoopGroup(1)
        try {
            val b = Bootstrap()
            b.group(workerGroup)
                    .channel(NioDatagramChannel::class.java)
                    .handler(object : ChannelInitializer<NioDatagramChannel>() {
                        override fun initChannel(ch : NioDatagramChannel)  {
                            val p = ch.pipeline();

                            p.addLast(
                                    RequestDecoder(Request.getDefaultInstance()),
                                    //ProtobufVarint32LengthFieldPrepender(),
                                    responseEncoder,
                                    //ProtobufVarint32FrameDecoder(),
                                    inboundHandler
                                    )
                        }
                    })

            // Bind and start to accept incoming connections.
            val channel = b.bind(PORT).sync().channel()
            executor.scheduleAtFixedRate({
                for (client in clients) {
                    val response = Response.newBuilder().setTarget(TargetInfo.newBuilder().setX(100).setY(100).build()).build()
                    channel.writeAndFlush(UpdateMessage(client.address, response))
                }

            }
            ,0,100, TimeUnit.MILLISECONDS)
            channel.closeFuture().sync();
        } finally {
            executor.shutdown()
            workerGroup.shutdownGracefully();
        }
    }
}
