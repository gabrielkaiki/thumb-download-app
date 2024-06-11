package com.gabrielkaiki.utubetools.activity.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gabrielkaiki.utubetools.R
import com.gabrielkaiki.utubetools.activity.ToolsActivity
import com.gabrielkaiki.utubetools.adapter.AdapterVideo
import com.gabrielkaiki.utubetools.api.YouTubeApi
import com.gabrielkaiki.utubetools.databinding.FragmentHomeBinding
import com.gabrielkaiki.utubetools.helper.*
import com.gabrielkaiki.utubetools.model.Resultado
import com.gabrielkaiki.utubetools.model.Video
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var recyclerSearch: RecyclerView
    private lateinit var adapterVideo: AdapterVideo
    private lateinit var linearSearch: LinearLayout
    private lateinit var buttonSearch: Button
    private lateinit var searchText: EditText
    private var listVideos: ArrayList<Video> = ArrayList()
    private var listRecents: ArrayList<Video> = ArrayList()
    private lateinit var menu: Menu
    lateinit var mAdView: AdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        setHasOptionsMenu(true)

        //Componentes
        inicializaComponentes()

        //RecyclerView
        inicializaRecyclerView()

        return root
    }

    private fun inicializaComponentes() {
        recyclerSearch = binding!!.recyclerSearch
        linearSearch = binding!!.linearSearch
        buttonSearch = binding!!.buttonSearch
        searchText = binding!!.editTextSearch
    }

    private fun inicializaRecyclerView() {
        adapterVideo = AdapterVideo(listVideos)
        recyclerSearch.setHasFixedSize(true)
        recyclerSearch.layoutManager = LinearLayoutManager(requireContext())
        recyclerSearch.adapter = adapterVideo

        //Click lsitener
        addClickListener()
    }

    private fun addClickListener() {
        recyclerSearch.addOnItemTouchListener(
            RecyclerItemClickListener(
                requireContext(),
                recyclerSearch, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        currentVideo = listVideos[position]
                        saveRecents(currentVideo!!)
                        val intent = Intent(requireContext(), ToolsActivity::class.java)
                        startActivity(intent)
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

                }
            ))

        buttonSearch.setOnClickListener {
            val search = searchText.text.toString()
            //it.setBackgroundColor(Color.BLUE)

            if (!search.isNullOrEmpty()) {
                search(search)
            } else {
                Toast.makeText(requireContext(), "Please enter a search.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun search(query: String) {
        val retrofit = getRetrofit()

        val request = retrofit.create(YouTubeApi::class.java)
        request.searchVideos("snippet", 50, API_KEY, query).enqueue(object : Callback<Resultado> {
            override fun onResponse(call: Call<Resultado>, response: Response<Resultado>) {
                if (response.isSuccessful) {
                    listVideos.clear()
                    val resultado = response.body()

                    for (item in resultado!!.items!!) {
                        val video = Video()

                        video.channelId = item.snippet!!.channelId
                        video.videoId = item.id!!.videoId
                        video.description = item.snippet!!.description
                        video.title = item.snippet!!.title
                        video.publishedAt = item.snippet!!.publishedAt
                        video.thumbnails = item.snippet!!.thumbnails

                        listVideos.add(video)
                    }
                    linearSearch.visibility = View.GONE
                    menu.getItem(0).isVisible = true
                    inicializaRecyclerView()
                }
            }

            override fun onFailure(call: Call<Resultado>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun saveRecents(videoSearch: Video) {
        val refRecents = getDataBase().child("recents").child(getUserId())
        refRecents.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var contemIdVideo = false
                var primeiroVideo: Video? = null
                var contador = 0
                if (snapshot.hasChildren()) {
                    for (video in snapshot.children) {
                        val videoObj = video.getValue(Video::class.java)!!
                        if (contador == 0) primeiroVideo = videoObj
                        if (videoObj.videoId == videoSearch.videoId) contemIdVideo = true
                        contador++
                    }
                }

                val refVideo = refRecents.child(videoSearch.videoId!!)
                if (!contemIdVideo && snapshot.childrenCount < 5) {
                    refVideo.setValue(videoSearch)
                } else if (!contemIdVideo && snapshot.childrenCount >= 5 && snapshot.hasChildren()) {
                    val videoAntigoExcluido = refRecents.child(primeiroVideo?.videoId!!)
                    videoAntigoExcluido.removeValue()
                    refVideo.setValue(videoSearch)
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        menu.getItem(0).isVisible = listVideos.size != 0
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search) {
            menu.getItem(0).isVisible = false
            linearSearch.visibility = View.VISIBLE
            listVideos.clear()
            adapterVideo.notifyDataSetChanged()
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        if (listVideos.size == 0) {
            linearSearch.visibility = View.VISIBLE
        } else {
            linearSearch.visibility = View.GONE
        }

        //Anúncio
        mAdView = binding!!.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}