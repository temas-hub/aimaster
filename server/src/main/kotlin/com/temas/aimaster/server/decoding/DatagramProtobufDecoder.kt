package com.temas.aimaster.server.decoding

import com.google.protobuf.ExtensionRegistryLite
import com.google.protobuf.MessageLite
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.socket.DatagramPacket
import io.netty.handler.codec.MessageToMessageDecoder

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 27.01.2016
 */
open class DatagramProtobufDecoder(val prototype: MessageLite, val extensionRegistry: ExtensionRegistryLite?) : MessageToMessageDecoder<DatagramPacket>() {

    constructor(prototype: MessageLite) : this(prototype, null)

    companion object {
        var HAS_PARSER = false
        init {
            try {
                // MessageLite.getParsetForType() is not available until protobuf 2.5.0.
                MessageLite::class.java.getDeclaredMethod("getParserForType")
                HAS_PARSER = true
            } catch (t: Throwable) {
                // Ignore
            }
        }
    }

    @Throws(Exception::class)
    protected override fun decode(ctx: ChannelHandlerContext, msg: DatagramPacket, out: MutableList<Any>) {
        val messageContent : ByteBuf = msg.content().retain()
        val array: ByteArray
        val offset: Int
        val length = messageContent.readableBytes()
        if (messageContent.hasArray()) {
            array = messageContent.array()
            offset = messageContent.arrayOffset() + messageContent.readerIndex()
        } else {
            array = ByteArray(length)
            messageContent.getBytes(messageContent.readerIndex(), array, 0, length)
            offset = 0
        }

        if (extensionRegistry == null) {
            if (HAS_PARSER) {
                out.add(prototype.getParserForType().parseFrom(array, offset, length))
            } else {
                out.add(prototype.newBuilderForType().mergeFrom(array, offset, length).build())
            }
        } else {
            if (HAS_PARSER) {
                out.add(prototype.getParserForType().parseFrom(array, offset, length, extensionRegistry))
            } else {
                out.add(prototype.newBuilderForType().mergeFrom(array, offset, length, extensionRegistry).build())
            }
        }
    }


}