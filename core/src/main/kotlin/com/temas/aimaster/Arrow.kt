package com.temas.aimaster

import com.badlogic.gdx.math.Vector2

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 14/09/2015
 */
public class Arrow () {
    public var len: Float = -1f
    public var dir: Vector2 = Vector2.Zero
    val throwKoef = 1.5f
    var firstPoint = Vector2(-1f, -1f)
    var lastPoint = Vector2()
    var endPoint = Vector2()

    public fun update(delta: Float) {
        if (firstPoint.x != -1f) {
            dir = lastPoint.cpy().sub(firstPoint).scl(-throwKoef) // mirror and scale throw dir
            len = dir.len()
            endPoint = firstPoint.cpy().add(dir)
        } else {
            len = -1f
        }
    }

}