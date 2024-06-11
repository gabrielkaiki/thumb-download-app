package com.gabrielkaiki.utubetools.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gabrielkaiki.utubetools.adapter.AdapterVideo
import com.gabrielkaiki.utubetools.databinding.ActivityFavoritesBinding
import com.gabrielkaiki.utubetools.helper.RecyclerItemClickListener
import com.gabrielkaiki.utubetools.helper.currentVideo
import com.gabrielkaiki.utubetools.helper.getDataBase
import com.gabrielkaiki.utubetools.helper.getUserId
import com.gabrielkaiki.utubetools.model.Video
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var recyclerFavorites: RecyclerView
    private lateinit var adapter: AdapterVideo
    private var listFavorites = arrayListOf<Video>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar
        val toolbar = binding.includeToolbarFav.toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        //Components
        initializeComponents()

        //RecyclerView
        configureRecycler()

        //Retrieve favorites
        retrieveFavorites()
    }

    private fun retrieveFavorites() {
        val path = "favorites/${getUserId()}"
        val refFav = getDataBase().child(path)

        refFav.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listFavorites.clear()
                if (snapshot.hasChildren()) {
                    for (video in snapshot.children) {
                        val vd = video.getValue(Video::class.java)!!
                        listFavorites.add(vd)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun initializeComponents() {
        recyclerFavorites = binding.recyclerFavorites
    }

    private fun configureRecycler() {
        adapter = AdapterVideo(listFavorites)
        recyclerFavorites.setHasFixedSize(true)
        recyclerFavorites.layoutManager = LinearLayoutManager(this)
        recyclerFavorites.adapter = adapter

        //Listener recycler
        addRecyclerListener()
    }

    private fun addRecyclerListener() {

        recyclerFavorites.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                recyclerFavorites,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        currentVideo = listFavorites[position]
                        startActivity(Intent(this@FavoritesActivity, ToolsActivity::class.java))
                    }

                    override fun onItemClick(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                    }

                    override fun onLongItemClick(view: View?, position: Int) {
                    }

                })
        )
    }

    override fun onStart() {
        super.onStart()
        retrieveFavorites()
    }
}