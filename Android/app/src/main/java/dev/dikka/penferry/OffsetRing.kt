package dev.dikka.penferry

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
class OffsetRing(val size: Int) {
    private var buffer: Array<Offset> = Array(size) { Offset(0f, 0f) }
    private var lifeTimeElements by mutableStateOf(0)
    private var activePoints by mutableStateOf(0)

    var lastTime by mutableStateOf(TimeSource.Monotonic.markNow())

    fun push(offset: Offset) {
        if (lifeTimeElements == 0 && activePoints == 0) {
            buffer = Array(size) { offset }
            lifeTimeElements = size
        }
        lifeTimeElements++
        if (activePoints < size) activePoints++
        for (i in size - 1 downTo 1) {
            buffer[i] = buffer[i - 1]
        }
        buffer[0] = offset
        lastTime = TimeSource.Monotonic.markNow()
    }

    fun decayPoint(): Boolean {
        if (activePoints > 0) activePoints--
        return activePoints != 0
    }

    fun full(): Boolean {
        return lifeTimeElements >= size
    }

    fun list(): List<Offset> {
        return buffer.slice(0 until activePoints)
    }

    fun get(index: Int): Offset {
        return buffer[index]
    }

    fun get(index: Int, default: Offset): Offset {
        return if (index < size) buffer[index] else default
    }
}
