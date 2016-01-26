import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.netty.handler.codec.protobuf.ProtobufEncoder
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender
import java.util.*

import com.temas.aimaster.UpdateRequestOuterClass.UpdateRequest as Request
import com.temas.aimaster.UpdateRequestOuterClass.UserInfo as UserInfo

import com.temas.aimaster.UpdateResponseOuterClass.UpdateResponse as Response
import com.temas.aimaster.UpdateResponseOuterClass.TargetInfo as TargetInfo

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 21.01.2016
 */
class UDPServer {

    public var clients = ArrayList<ClientInfo>()

    companion object {
        @JvmStatic public fun main (args: Array<String>) {
            UDPServer().start(args);
        }
    }

    val requestDecoder = object : MessageToMessageDecoder<DatagramPacket>() {
        override fun decode(ctx: ChannelHandlerContext, msg: DatagramPacket, out: MutableList<Any>) {
            out.add(ClientInfo(msg.content().retain(), null, msg.sender()))
        }
    }

    val inboundHandler = object : ChannelInboundHandlerAdapter() {
        override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
            println("com.temas.netty.poc.Server read $msg")
            if (msg is ClientInfo)

            }
            println("Clients are $clients")
        }

        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            cause.printStackTrace()
        }
    }


    val responseEncoder = object : MessageToMessageEncoder<ByteBuf>() {
        override fun encode(ctx: ChannelHandlerContext?, msg: ByteBuf, out: MutableList<Any>) {
            out.add()
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
                                    DatagramReceiver(),
                                    //ProtobufVarint32LengthFieldPrepender(),
                                    ProtobufEncoder(),
                                    //ProtobufVarint32FrameDecoder(),
                                    ProtobufDecoder(Request.getDefaultInstance()),
                                    inboundHandler)
                        }
                    })

            // Bind and start to accept incoming connections.
            b.bind(PORT).sync().channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}

class DatagramReceiver : MessageToMessageDecoder<DatagramPacket>() {
    override fun decode(ctx: ChannelHandlerContext, msg: DatagramPacket, out: MutableList<Any>) {
        //ctx.write(DatagramPacket(msg.content().retain(), msg.sender()))
        out.add(msg.content().retain())
        out.add(msg.sender())
        //ctx.flush()
    }
}

class UDPObjectEchoServerHandler : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {

        //ctx.write(msg)
        println("com.temas.netty.poc.Server read $msg")
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        //ctx.flush()
        //println("Server flushed")
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }

}