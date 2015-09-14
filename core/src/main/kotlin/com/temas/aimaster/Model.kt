package com.temas.aimaster.model

import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.Arrow
import com.temas.aimaster.Ball
import com.temas.aimaster.FixedList
import java.util.*
import kotlin.properties.Delegates

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 09/09/2015
 */


class Model {

    public val lastPoints: FixedList<Vector2> = FixedList(10, javaClass<Vector2>())

    public var ball: Ball? = null

    public var arrow: Arrow = Arrow()

    companion object {
        public val flightSpeed: Float = 2f // whole distance per second
    }

    fun update(delta: Float) {

        updateArrow(delta)
        moveBall3(delta)

    }

    private fun updateArrow(delta: Float) {
        arrow.update(delta)
    }

    private fun moveBall3(delta: Float) {
        if (ball != null) {
            val b = ball!!
            val newX = b.pos3.x + b.dir.x * flightSpeed * delta
            val newY = b.pos3.y + b.dir.y * flightSpeed * delta
            val traveled = Vector2(newX, newY).sub(b.starPoint).len()
            val distKoef = 3f
            val totalDistance = b.dir.len() * distKoef
            if (traveled < totalDistance) {
                val verticalSpeed: Double = Math.sin(traveled / totalDistance * 2 * Math.PI) * 100
                val z = b.pos3.z + delta * verticalSpeed
                b.pos3.set(newX, newY, z.toFloat())
            }
        }
    }

    public fun distSq(p1: Vector2, p2: Vector2): Float {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return dx * dx + dy * dy
    }

    public fun smooth(input: FixedList<Vector2>, output: ArrayList<Vector2>) {
        //first element
        output.add(input.get(0));
        //average elements
        for (i in 0..input.size-2) {
            val p0 = input.get(i);
            val p1 = input.get(i+1);

            val Q = Vector2(0.75f * p0.x + 0.25f * p1.x, 0.75f * p0.y + 0.25f * p1.y);
            val R = Vector2(0.25f * p0.x + 0.75f * p1.x, 0.25f * p0.y + 0.75f * p1.y);
            output.add(Q);
            output.add(R);
        }

        //last element
        output.add(input.get(input.size-1));
    }

    fun launch(point: Vector2) {
        arrow.firstPoint.set(-1f, -1f)
    }

    fun createBall(point: Vector2): Ball {
        val a = arrow
        val revDirVect = a.dir
        ball = Ball(a.firstPoint.cpy(), Vector2(revDirVect.x, revDirVect.y))
        return ball as Ball
    }

}
