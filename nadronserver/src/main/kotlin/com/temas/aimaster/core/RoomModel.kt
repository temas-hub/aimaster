package com.temas.aimaster.core

import com.badlogic.gdx.math.Intersector
import com.temas.aimaster.RoomTarget
import com.temas.aimaster.model.Stone
import java.util.*


/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 28.09.2016
 */
class RoomModel {
    val target: RoomTarget = RoomTarget(speed = 0.05f, radius = 100f)

    val stones = ArrayList<Stone>()

    fun update(delta: Float) {
        target.update(delta)
        stones.forEach {
            it.update(delta)
            // TODO collisions
            checkStop(it)
        }
    }

    private fun checkStop(st: Stone) {
        if (st.state == Stone.STATE.MOVE) {
            if (st.velocity <= 0.0005f) {
                st.velocity = 0f
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