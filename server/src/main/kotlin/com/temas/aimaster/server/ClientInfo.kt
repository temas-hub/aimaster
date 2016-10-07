package com.temas.aimaster.server

import java.net.InetSocketAddress

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 26.01.2016
 */
public data class ClientInfo(val name : String, val address : InetSocketAddress) {
}