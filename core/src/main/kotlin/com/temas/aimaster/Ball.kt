package com.temas.aimaster

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 12/09/2015
 */

public open class Ball(var starPoint: Vector2, var dir: Vector2) {
    public val pos3: Vector3 = Vector3(starPoint.x, starPoint.y, 0f)
}