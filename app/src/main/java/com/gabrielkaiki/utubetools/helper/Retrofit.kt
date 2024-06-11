package com.gabrielkaiki.utubetools.helper

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun getRetrofit(): Retrofit {
    return Retrofit.Builder().baseUrl(URL_BASE).addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun getRetrofitMp3(): Retrofit {
    return Retrofit.Builder().baseUrl(URL_BASEMP3)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}