package com.temas.aimaster.dao

import org.mapdb.DBMaker
import org.mapdb.Serializer
import org.mapdb.serializer.SerializerArrayTuple

/**
 * @author Artem Zhdanov <temas_coder@yahoo.com>
 * @since 10.11.2016
 */
object UserRepository {

    val db = DBMaker.fileDB("users.db").transactionEnable().closeOnJvmShutdown().make()

    val usersDb = db.hashMap("users", Serializer.INTEGER, SerializerArrayTuple(Serializer.STRING, Serializer.STRING)).createOrOpen()

    val lastUserId = db.atomicInteger("lastUserId").createOrOpen()


    fun findRecordById(id: Int): Pair<String,String>? {
        if (usersDb[id] != null) {
            return Pair(usersDb[id].get(0) as String, usersDb[id].get(1) as String)
        }
        return null
    }

    fun storeUser(id: Int, name: String, email: String) {
        usersDb.put(id, arrayOf(name, email))
        usersDb.commit()
    }


}