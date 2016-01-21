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
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 21.01.2016
 */
class UDPServer {
    companion object {
        @JvmStatic public fun main (args: Array<String>) {
            UDPServer().start(args);
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
                                    ProtobufVarint32LengthFieldPrepender(),
                                    ProtobufEncoder(),
                                    ProtobufVarint32FrameDecoder(),
                                    //ProtobufDecoder(WorldStateProtoOuterClass.WorldStateProto.getDefaultInstance()),
                                    UDPObjectEchoServerHandler())
                        }
                    })

            // Bind and start to accept incoming connections.
            b.bind(PORT).sync().channel().closeFuture().sync();
        } finally {
            //bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

class DatagramReceiver : MessageToMessageDecoder<DatagramPacket>() {
    override fun decode(ctx: ChannelHandlerContext, msg: DatagramPacket, out: MutableList<Any>) {
        ctx.write(DatagramPacket(msg.content().retain(), msg.sender()))
        out.add(msg.content().retain())
        ctx.flush()
    }
}

class UDPObjectEchoServerHandler : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is DatagramPacket) {
            println(msg.content())
        }
        //ctx.write(msg)
        println("com.temas.netty.poc.Server read $msg")
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
        println("Server flushed")
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }

}