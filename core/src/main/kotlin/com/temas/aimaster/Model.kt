package com.temas.aimaster.model

import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.Arrow
import com.temas.aimaster.Ball
import com.temas.aimaster.FixedList
import com.temas.aimaster.Target
import java.util.*

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 09/09/2015
 */


class Model {

    public val lastPoints: FixedList<Vector2> = FixedList(10, Vector2::class.java)
    public val arrow: Arrow = Arrow()
    public val target: Target = Target(150f, 100f)

    public var ball: Ball? = null


    fun update(delta: Float) {
        target.update(delta)
        arrow.update(delta)
        ball?.update(delta)
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
