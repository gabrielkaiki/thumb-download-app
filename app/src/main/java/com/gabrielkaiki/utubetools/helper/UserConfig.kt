package com.gabrielkaiki.utubetools.helper

import com.gabrielkaiki.utubetools.model.Usuario


var currentUser: Usuario? = null

fun getUserId(): String {
    return getAuth()!!.currentUser!!.uid
}