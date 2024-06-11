package com.gabrielkaiki.utubetools.activity

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.gabrielkaiki.utubetools.R
import com.gabrielkaiki.utubetools.databinding.ActivityNavegacaoBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavegacaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavegacaoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavegacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Toolbar
        criaToolbar()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_navegacao)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_settings,
                R.id.navigation_logout
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_actionbar, menu)
        return true
    }

    private fun criaToolbar() {
        val toolbar = binding.includeToolbar.toolbar
        setSupportActionBar(toolbar)
    }
}