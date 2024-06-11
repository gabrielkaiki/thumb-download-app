package com.gabrielkaiki.utubetools.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gabrielkaiki.utubetools.adapter.AdapterVideo
import com.gabrielkaiki.utubetools.databinding.ActivityRecentsBinding
import com.gabrielkaiki.utubetools.helper.RecyclerItemClickListener
import com.gabrielkaiki.utubetools.helper.currentVideo
import com.gabrielkaiki.utubetools.helper.getDataBase
import com.gabrielkaiki.utubetools.helper.getUserId
import com.gabrielkaiki.utubetools.model.Video
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RecentsActivity : AppCompatActivity() {
    private lateinit var bindind: ActivityRecentsBinding
    private var listRecents = arrayListOf<Video>()
    private lateinit var adapter: AdapterVideo
    private lateinit var recyclerRecents: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindind = ActivityRecentsBinding.inflate(layoutInflater)
        setContentView(bindind.root)

        //Toolbar
        val toolbar = bindind.include.toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        //Components
        initializeComponents()

        //Retrieve recents
        retrieveRecents()

        //Recycler
        configureRecycler()
    }

    private fun configureRecycler() {
        adapter = AdapterVideo(listRecents)
        recyclerRecents.setHasFixedSize(true)
        recyclerRecents.layoutManager = LinearLayoutManager(this)
        recyclerRecents.adapter = adapter

        addRecyclerListener()
    }

    private fun initializeComponents() {
        recyclerRecents = bindind!!.recyclerRecents
    }

    private fun addRecyclerListener() {

        recyclerRecents.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                recyclerRecents,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        currentVideo = listRecents[position]
                        startActivity(Intent(this@RecentsActivity, ToolsActivity::class.java))
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

    private fun retrieveRecents() {
        val ref = getDataBase().child("recents").child(getUserId())
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listRecents.clear()
                if (snapshot.value != null) {
                    for (video in snapshot.children) {
                        val son = video.getValue(Video::class.java)
                        listRecents.add(son!!)
                    }
                }
                configureRecycler()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}