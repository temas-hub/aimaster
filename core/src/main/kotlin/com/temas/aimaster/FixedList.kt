package com.temas.aimaster

/**
 * * A simple extension of Array that allows inserting an element
 * at the head (index 0) without ever growing the backing array.
 * Elements are shifted right and eventually discarded to make
 * way for new additions.
 *
 * @param <T> generic type
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 10/09/2015
 */

import com.badlogic.gdx.utils.Array;

/**
 * Safely creates a list that is backed by an array which
 * can be directly accessed.
 *
 * @param capacity the fixed-size capacity of this list
 * @param type the class type of the elements in this list
 *
 */
open class FixedList<T>(capacity: Int, type: Class<T>): Array<T>(false, capacity, type)  {
    /**
     * Inserts the item into index zero, shifting all items to the right,
     * but without increasing the list's size past its array capacity.
     * @param t the element to insert
     */
    public fun insert(t: T) {
        items = this.items

        // increase size if we have a new point
        size = Math.min(size + 1, items.size)

        // shift elements right
        for(i in size-1 downTo  1) {
            items[i] = items[i - 1]
        }
        // insert new item at first index
        items[0] = t
    }


}
