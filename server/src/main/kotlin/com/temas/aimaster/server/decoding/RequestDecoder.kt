package com.temas.aimaster.server.decoding

import com.google.protobuf.MessageLite
import com.temas.aimaster.server.ClientInfo
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.socket.DatagramPacket
import java.util.*
import com.temas.aimaster.UpdateRequestOuterClass.UpdateRequest as Request
import com.temas.aimaster.UpdateRequestOuterClass.UserInfo as UserInfo

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 27.01.2016
 */
class RequestDecoder(val messageObject: MessageLite) : DatagramProtobufDecoder(messageObject){
    override fun decode(ctx: ChannelHandlerContext, msg: DatagramPacket, out: MutableList<Any>) {
        val parsedObjects = ArrayList<Any>()
        super.decode(ctx, msg, parsedObjects)
        val parsedObject =  parsedObjects[0]
        if (parsedObject is Request) {
            out.add(ClientInfo(parsedObject.userInfo.name, msg.sender()))
        }
    }
}