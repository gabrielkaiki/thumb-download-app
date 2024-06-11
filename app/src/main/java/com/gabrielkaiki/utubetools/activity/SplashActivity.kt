package com.gabrielkaiki.utubetools.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.gabrielkaiki.utubetools.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(
            {
                openMainScreen()
            }, 3000
        )
    }

    private fun openMainScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}