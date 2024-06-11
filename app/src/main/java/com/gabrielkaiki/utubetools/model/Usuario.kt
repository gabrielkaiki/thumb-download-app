package com.gabrielkaiki.utubetools.model

import com.gabrielkaiki.utubetools.helper.getDataBase
import com.google.firebase.database.Exclude
import java.io.Serializable

class Usuario : Serializable {

    var id: String? = null
    var name: String? = null
    var email: String? = null
    var pathPhoto: String? = null

    @set:Exclude
    @get:Exclude
    var senha: String? = null

    fun salvar(): Boolean {
        return try {
            val dataBase = getDataBase()
            val ref = dataBase.child("users").child(this.id!!)
            ref.setValue(this)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}