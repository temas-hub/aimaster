package com.temas.aimaster.model

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.model.Arrow
import com.temas.aimaster.FixedList
import com.temas.aimaster.model.Target
import java.util.*

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 09/09/2015
 */


open class Model {

    companion object {
        val TARGET_SPEED = 150f
        val TARGET_RADIUS = 100f
    }

    val lastPoints: FixedList<Vector2> = FixedList(10, Vector2::class.java)
    val arrow: Arrow = Arrow()
    val target: Target by lazy {createTarget()}

    //var ball: Ball? = null
    val stones = ArrayList<Stone>()



    open fun update(delta: Float) {
        target.update(delta)
        arrow.update(delta)
        //ball?.update(delta)
        stones.forEach {
            it.update(delta)
        }
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

//    fun createBall(point: Vector2): Ball {
//        val a = arrow
//        val revDirVect = a.dir
//        ball = Ball(a.firstPoint.cpy(), Vector2(revDirVect.x, revDirVect.y))
//        return ball!!
//    }

    open fun createStone(): Stone{
        return Stone(startPoint = arrow.firstPoint, velocity = arrow.dir)
    }

    open fun createTarget(): Target {
        return Target(TARGET_SPEED, TARGET_RADIUS)
    }


    fun checkStop(st: Stone) {
        if (st.state == Stone.STATE.MOVE) {
            if (st.velocity.len() <= 0.0005f) {
                st.velocity.set(0f,0f)
                st.state = Stone.STATE.STAY
            }
            if (st.state != Stone.STATE.STICKED &&
                    Intersector.overlaps(st.circle(), target.circle())) {
                target.addStone(st)
                st.state = Stone.STATE.STICKED
            }
        }
    }
}


