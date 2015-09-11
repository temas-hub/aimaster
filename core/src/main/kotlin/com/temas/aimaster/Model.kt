package com.temas.aimaster.model

import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.FixedList
import java.util.*
import kotlin.properties.Delegates

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 09/09/2015
 */


class Model {

    public val lastPoints: FixedList<Vector2> = FixedList(10, javaClass<Vector2>())

    fun update(delta: Float) {
        //TODO
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

}