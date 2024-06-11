package com.gabrielkaiki.utubetools.activity

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.gabrielkaiki.utubetools.R
import com.gabrielkaiki.utubetools.adapter.AdapterGrid
import com.gabrielkaiki.utubetools.databinding.ActivityToolsBinding
import com.gabrielkaiki.utubetools.helper.Permissao
import com.gabrielkaiki.utubetools.helper.currentVideo
import com.gabrielkaiki.utubetools.helper.getUserId
import com.gabrielkaiki.utubetools.model.ItemGrid
import com.squareup.picasso.Picasso


class ToolsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityToolsBinding
    private lateinit var thumb: ImageView
    private lateinit var textTitle: TextView
    private lateinit var textDescription: TextView
    private lateinit var textDate: TextView
    private lateinit var gridView: GridView
    private lateinit var buttonFavorites: Button
    private var listaOptionsTools = ArrayList<ItemGrid>()
    private var permissoes =
        arrayOf(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityToolsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar
        val toolbar = binding.includeToolbarTools.toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        //Permissões
        val permissao = Permissao.validarPermissoes(permissoes, this, 1)

        //Components
        initializeComponents()

        //Lista
        listaOptionsTools.add(ItemGrid(R.drawable.img, "Download thumbnail"))

        val adapterGrid = AdapterGrid(this, listaOptionsTools)
        gridView.adapter = adapterGrid

        gridView.setOnItemClickListener { parent, view, position, id ->
            val card: CardView = view as CardView

            val colorFrom = resources.getColor(R.color.card_color)
            val colorTo = resources.getColor(R.color.card_colorclick)
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 250 // milliseconds

            colorAnimation.addUpdateListener { animator -> card.setCardBackgroundColor(animator.animatedValue as Int) }
            colorAnimation.start()

            /*card.setCardBackgroundColor(
                resources.getColor(R.color.card_colorclick)
            )*/
            when (position) {
                0 -> {
                    downloadImg()
                }
            }
        }

        //Add properts
        Picasso.get().load(Uri.parse(currentVideo?.thumbnails?.high?.url)).into(thumb)
        textTitle.text = currentVideo?.title
        textDescription.text = currentVideo?.description
        textDate.text = currentVideo?.publishedAt
    }

    private fun downloadImg() {
        val request =
            DownloadManager.Request(Uri.parse(currentVideo!!.thumbnails!!.high!!.url))
                .setTitle("${currentVideo!!.title} is downloading...")
                .setDescription("${currentVideo!!.description}")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_PICTURES,
            "${currentVideo!!.title}.jpg"
        )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    private fun initializeComponents() {
        thumb = binding.imageThumbnailTools
        textTitle = binding.textToolsTitle
        textDescription = binding.textToolsDescription
        textDate = binding.textToolsPublishedAt
        gridView = binding.grid
        buttonFavorites = binding.buttonFavorites

        if (currentVideo!!.favorite) buttonFavorites.text =
            "Remove favorites" else buttonFavorites.text = "Add favorites"
        //Thumb listener
        addThumbListener()

        //Button listener
        addFavoritesListener()
    }

    private fun addFavoritesListener() {
        buttonFavorites.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(R.layout.loading_screen)
                .create().apply { show() }

            if (!currentVideo!!.favorite) {
                if (currentVideo!!.saveFavorites(getUserId())) {
                    buttonFavorites.text = "Remove favorites"
                    currentVideo!!.favorite = true
                    Toast.makeText(this, "Success saving video!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Error saving video!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            } else {
                if (currentVideo!!.removeFavorites(getUserId())) {
                    buttonFavorites.text = "Add favorites"
                    currentVideo!!.favorite = false
                    Toast.makeText(this, "Success removing video!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Error removing video!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
    }

    private fun addThumbListener() {
        thumb.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permissao in grantResults) {
            if (permissao == PackageManager.PERMISSION_DENIED) {
                alertPermission()
            }
        }

    }

    private fun alertPermission() {
        val constructor = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("You must accept all permissions to open the next screen.")

        constructor.setNegativeButton("Close") { _, _ ->
            finish()
        }

        val dialog = constructor.create()
        dialog.show()
    }
}