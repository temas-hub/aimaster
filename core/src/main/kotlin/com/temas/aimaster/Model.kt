package com.temas.aimaster.model

import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.FixedList
import kotlin.properties.Delegates

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 09/09/2015
 */


class Model {

    var lstPoints: FixedList<Vector2>? = null

    fun update(delta: Float) {
        //TODO
    }

    fun saveLine(lastPoints: FixedList<Vector2>) {
        lstPoints = lastPoints
    }

}