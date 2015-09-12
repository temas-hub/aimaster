package com.temas.aimaster.model

import com.badlogic.gdx.math.Vector2
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

    public val throwDirection: ThrowDirection = ThrowDirection()

    public var ball: Ball? = null

    companion object {
        public val defSpeed: Float = 30f;
    }

    fun update(delta: Float) {
        moveBall3(delta)

    }

    private fun moveBall(delta: Float) {
        if (ball != null) {
            val b = ball!!
            val sigDir = Vector2(Math.signum(b.dir.x),Math.signum(b.dir.y))
            //b.velocity = 20 * b.altitude
            //b.speed += b.velocity * delta
            b.speed = 5000 * b.altitude

            val dist = sigDir.scl(b.speed).scl(delta)
            b.pos.add(dist)
            val traveled = b.pos.cpy().sub(b.starPoint)
            val radDist = traveled.len() / b.dir.len()
            b.altitude = Math.sin(radDist * 2 * Math.PI).toFloat()
        }
    }


    private fun moveBall3(delta: Float) {
        if (ball != null) {
            val b = ball!!
            val flightTime = 3f
            val newX = b.pos3.x + b.dir.x * flightTime * delta
            val newY = b.pos3.y + b.dir.y * flightTime * delta
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

    public fun simplify(points: FixedList<Vector2>, sqTolerance: Float, out: FixedList<Vector2>) {
        val len = points.size;

        var point = Vector2();
        var prevPoint = points.get(0);

        out.clear();
        out.add(prevPoint);

        for (i in 1..len - 1) {
            point = points.get(i);
            if (distSq(point, prevPoint) > sqTolerance) {
                out.add(point);
                prevPoint = point;
            }
        }
        if (!prevPoint.equals(point)) {
            out.add(point);
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
        throwDirection.firstPoint.set(-1f, -1f)
    }

    fun createBall(point: Vector2): Ball {
        val dir = throwDirection
        val revDirVect = dir.lastPoint.cpy().sub(dir.firstPoint)
        //val dirVect = dir.firstPoint.cpy().sub(revDirVect.scl(2f))
        ball = Ball(dir.firstPoint.cpy(), defSpeed, 25f, 0.01f, Vector2(-revDirVect.x, -revDirVect.y))
        return ball as Ball
    }

}

data class ThrowDirection {
    var firstPoint = Vector2(-1f, -1f)
    var lastPoint = Vector2()


}
