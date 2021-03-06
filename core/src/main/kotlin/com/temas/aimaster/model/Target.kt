package com.temas.aimaster.model

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.Renderer
import com.temas.aimaster.model.Intersections as I
import java.util.*

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 14/09/2015
 */

open class Target(var speed: Float, var radius: Float) {
    private val NUM_OF_CHANGE_DIR_TRIES: Int = 10
    protected val CENTRAL_POINT = Vector2(Renderer.GAME_WIDTH / 2, Renderer.GAME_HEIGHT / 2)

    private val rnd = Random()


    var center: Vector2 = CENTRAL_POINT;
    var moveDir: Vector2 = Vector2.Zero

    val stickedStones = ArrayList<Stone>()

    open fun update(delta: Float) {
        center.add(moveDir.cpy().scl(speed * delta))
        //var newCenter = center.cpy().add(moveDir.cpy().scl(delta))
        //center.set(newCenter)
        /*if (moveDir.isZero) {
            moveDir = getRandomVector()
        }
        var targetCircle = getNewPoistion(delta)
        targetCircle = makeSureItISWithInTheScreen(delta, targetCircle)
        center.set(targetCircle.x, targetCircle.y)*/
    }



//    private fun makeSureItISWithInTheScreen(delta: Float, targetCircle: Circle): Circle {
//        var res = targetCircle
//        if (I.intersects(res)) {
//            for (i in 1..NUM_OF_CHANGE_DIR_TRIES) {
//                if (I.intersects(res)) {
//                    moveDir = getRandomVector()
//                    res = getNewPoistion(delta)
//                }
//            }
//            if (I.intersects(res)) {
//                res = Circle(CENTRAL_POINT, radius)
//            }
//        }
//        return res
//    }

//    fun getNewPoistion(delta: Float): Circle {
//        var newCenter = center.cpy().add(moveDir.cpy().scl(speed * delta))
//        val targetCircle = Circle(newCenter, radius)
//        return targetCircle
//    }

    protected fun getRandomVector(): Vector2 {
        val x: Float = (rnd.nextInt(21) - 10) / 10f
        val y: Float = (rnd.nextInt(21) - 10) / 10f
        return Vector2(x,y).nor()
    }

    fun addStone(st: Stone) {
        stickedStones.add(st)
    }

    fun circle() = Circle(center, radius)

}
