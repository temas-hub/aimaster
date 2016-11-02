package com.temas.aimaster.model

import com.badlogic.gdx.math.Vector2

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 02.11.2016
 */
data class ThrownStone(val pos: Vector2, val vel: Vector2, val id: Int = ++Stone.ID_COUNTER)