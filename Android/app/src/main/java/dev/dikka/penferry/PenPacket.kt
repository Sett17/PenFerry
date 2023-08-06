package dev.dikka.penferry

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

enum class PenEvent(val id: Byte) {
    HOVER_MOVE(0x10),
    HOVER_EXIT(0x11),
    CONTACT_MOVE(0x20),
    CONTACT_DOWN(0x22),
    CONTACT_UP(0x21),
    SUPP_ACTION(0x0F),
    DUMMY(0x00);

    companion object {
        fun find(value: Byte): PenEvent? = values().find { it.id == value }
    }
}

data class PenPacket(val event: PenEvent, val arg1: Float = 0f, val arg2: Float = 0f, val arg3: Float = 0f, val buttonPressed: Boolean = false) {
    companion object {
        val size = PenPacket(PenEvent.DUMMY).encode().size
    };
    override fun toString(): String {
        return "${this.event.name}, ${this.arg1}, ${this.arg2}, ${this.arg3}, ${this.buttonPressed}"
    }
}

fun PenPacket.send(address: String, port: Int) {
    CoroutineScope(Dispatchers.IO).launch {
        withContext(Dispatchers.IO) {
            DatagramSocket().use { socket ->
                socket.broadcast = true
                val data = this@send.encode()
                socket.send(DatagramPacket(data, data.size, InetAddress.getByName(address), port))
                output + "${this@send}"
            }
        }
    }
}

fun PenPacket.encode(): ByteArray {
    return byteArrayOf(this.event.id) + arg1.toBytes() + arg2.toBytes() + arg3.toBytes() + buttonPressed.toByte()
}

fun Float.toBytes(): ByteArray {
    val buffer = ByteArray(4)
    for (i in 0..3) buffer[i] = (this.toRawBits() shr (i * 8)).toByte()
    return buffer
}

fun ByteArray.toFloat(): Float {
    return Float.fromBits(
        (this[3].toInt() shl 24) or
                (this[2].toInt() and 0xff shl 16) or
                (this[1].toInt() and 0xff shl 8) or
                (this[0].toInt() and 0xff)
    )
}

fun Boolean.toByte(): Byte = if (this) 0x01 else 0x00