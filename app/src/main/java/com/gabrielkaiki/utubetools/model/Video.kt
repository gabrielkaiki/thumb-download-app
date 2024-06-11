package com.gabrielkaiki.utubetools.model

import com.gabrielkaiki.utubetools.helper.getDataBase
import java.io.Serializable

class Video : Serializable {
    fun saveFavorites(userId: String): Boolean {
        return try {
            this.favorite = true
            val ref = getDataBase().child("favorites").child(userId).child(this.videoId!!)
            ref.setValue(this)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun removeFavorites(userId: String): Boolean {
        return try {
            val ref = getDataBase().child("favorites").child(userId).child(this.videoId!!)
            ref.removeValue()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    var videoId: String? = null
    var channelId: String? = null
    var title: String? = null
    var description: String? = null
    var publishedAt: String? = null
    var thumbnails: Thumbnails? = null
    var favorite: Boolean = false
}