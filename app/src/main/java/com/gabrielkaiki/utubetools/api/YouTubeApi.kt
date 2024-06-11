package com.gabrielkaiki.utubetools.api

import com.gabrielkaiki.utubetools.model.Resultado
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YouTubeApi {

    @GET("search")
    fun searchVideos(
        @Query("part") part: String,
        @Query("maxResults") max: Int,
        @Query("key") key: String,
        @Query("q") search: String
    ): Call<Resultado>

    @GET("single/{FTYPE}")
    fun getMp3(
        @Path("FTYPE") ftype: String,
        @Query("VIDEO_URL") videoUrl: String
    ): Call<String>
}