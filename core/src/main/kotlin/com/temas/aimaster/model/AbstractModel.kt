package com.temas.aimaster.model

import java.util.*

/**
 * Created by temas on 10/23/2016.
 */
open class AbstractModel<T: Stone>(val physics: PhysicsWorld) {


    companion object {
        val TARGET_SPEED = 150f
        val TARGET_RADIUS = 100f
    }

    val arrow: Arrow = Arrow()
    val target: Target by lazy {createTarget()}

    //var ball: Ball? = null
    val stones = ArrayList<T>()

    open fun createTarget(): Target {
        return Target(TARGET_SPEED, TARGET_RADIUS)
    }
}