package com.temas.aimaster

import com.badlogic.gdx.math.Circle
import com.temas.aimaster.model.Intersections as I
import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.model.Stone
import org.slf4j.LoggerFactory
import java.util.*

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 14/09/2015
 */

class RoomTarget(var speed: Float, var radius: Float) {

    companion object {
        private val LOG = LoggerFactory.getLogger(RoomTarget::class.java)
    }

    private val NUM_OF_CHANGE_DIR_TRIES: Int = 10;
    private val CENTRAL_POINT = Vector2(Renderer.GAME_WIDTH / 2, Renderer.GAME_HEIGHT / 2)

    private val rnd = Random()

    val center: Vector2 = CENTRAL_POINT
    val moveDir: Vector2 = Vector2.Zero
    val stones = ArrayList<Stone>()


    fun update(delta: Float) {
        if (moveDir.isZero) {
            moveDir.set(getRandomVector())
        }
        var targetCircle = getNewPoistion(delta)
        targetCircle = makeSureItISWithInTheScreen(delta, targetCircle)
        center.set(targetCircle.x, targetCircle.y)

        stones.forEach {
            it.pos.set(it.pos.cpy().add(moveDir.cpy().scl(speed * delta)))
        }
    }

    private fun makeSureItISWithInTheScreen(delta: Float, targetCircle: Circle): Circle {
        var res = targetCircle
        if (I.intersects(res)) {
            for (i in 1..NUM_OF_CHANGE_DIR_TRIES) {
                if (I.intersects(res)) {
                    moveDir.set(getRandomVector())
                    res = getNewPoistion(delta)
                }
            }
            if (I.intersects(res)) {
                res = Circle(CENTRAL_POINT, radius)
            }
        }
        return res
    }

    fun circle() = Circle(center, radius)


    private fun getNewPoistion(delta: Float): Circle {
        var newCenter = center.cpy().add(moveDir.cpy().scl(speed * delta))
        val targetCircle = Circle(newCenter, radius)
        LOG.debug("New position is x=${newCenter.x}, y=${newCenter.y}")
        return targetCircle
    }

    private fun getRandomVector(): Vector2 {
        val x: Float = (rnd.nextInt(21) - 10) / 10f
        val y: Float = (rnd.nextInt(21) - 10) / 10f
        return Vector2(x,y).nor()
    }

    fun addStone(st: Stone) {
        stones.add(st)
    }

}
