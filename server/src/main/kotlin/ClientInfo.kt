import io.netty.buffer.ByteBuf
import java.net.InetSocketAddress

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 26.01.2016
 */
public data class ClientInfo(val envelope: ByteBuf, val name : String?, val address : InetSocketAddress?) {
}