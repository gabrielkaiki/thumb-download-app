package com.gabrielkaiki.utubetools.activity

import android.os.Bundle
import android.widget.Toast
import com.gabrielkaiki.utubetools.R
import com.gabrielkaiki.utubetools.helper.API_KEY
import com.gabrielkaiki.utubetools.helper.currentVideo
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView

class PlayerActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    var youTubePlayer: YouTubePlayerView? = null
    var playbackEventListener: YouTubePlayer.PlaybackEventListener? = null
    var playerStateChangeListener: YouTubePlayer.PlayerStateChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playbackEventListener = object : YouTubePlayer.PlaybackEventListener {
            override fun onPlaying() {}
            override fun onPaused() {}
            override fun onStopped() {}
            override fun onBuffering(b: Boolean) {}
            override fun onSeekTo(i: Int) {}
        }

        youTubePlayer = findViewById(R.id.viewPlayer)
        youTubePlayer?.initialize(API_KEY, this@PlayerActivity)
    }

    override fun onInitializationSuccess(
        p0: YouTubePlayer.Provider?,
        p1: YouTubePlayer?,
        p2: Boolean
    ) {
        p1!!.setFullscreen(true)
        p1.setShowFullscreenButton(false)
        p1.loadVideo(currentVideo!!.videoId)
    }

    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        p1: YouTubeInitializationResult?
    ) {
        Toast.makeText(this, "${p1!!.name}", Toast.LENGTH_SHORT).show()
    }
}