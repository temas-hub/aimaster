package com.temas.aimaster.server

import com.google.protobuf.MessageLite
import java.net.InetSocketAddress

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 27.01.2016
 */
public data class UpdateMessage(val address: InetSocketAddress, val message: MessageLite)