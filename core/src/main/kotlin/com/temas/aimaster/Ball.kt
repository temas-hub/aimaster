package com.temas.aimaster

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 12/09/2015
 */

class Ball(var starPoint: Vector2, var dir: Vector2) {
    val pos3: Vector3 = Vector3(starPoint.x, starPoint.y, 0f)

    companion object {
        val flightSpeed: Float = 2f // whole distance per second
    }

    fun update(delta: Float) {
        val newX = pos3.x + dir.x * flightSpeed * delta
        val newY = pos3.y + dir.y * flightSpeed * delta
        val traveled = Vector2(newX, newY).sub(starPoint).len()
        val distKoef = 3f
        val totalDistance = dir.len() * distKoef
        if (traveled < totalDistance) {
            val verticalSpeed: Double = Math.sin(traveled / totalDistance * 2 * Math.PI) * 100
            val z = pos3.z + delta * verticalSpeed
            pos3.set(newX, newY, z.toFloat())
        }
    }
}