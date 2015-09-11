package com.temas.aimaster.model

import com.badlogic.gdx.math.Vector2
import com.temas.aimaster.FixedList
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

}