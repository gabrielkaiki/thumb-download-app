package com.gabrielkaiki.utubetools.activity.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gabrielkaiki.utubetools.R
import com.gabrielkaiki.utubetools.activity.FavoritesActivity
import com.gabrielkaiki.utubetools.activity.RecentsActivity
import com.gabrielkaiki.utubetools.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var binding: FragmentDashboardBinding? = null
    private lateinit var firstRow: TableRow
    private lateinit var secondRow: TableRow

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        //Components
        initializeComponents()

        return root
    }

    private fun initializeComponents() {
        firstRow = binding!!.FirstRow
        secondRow = binding!!.SecondRow

        val preferences = requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE)

        val booleamSwitch1 = preferences.getBoolean("favorites", true)
        val booleamSwitch2 = preferences.getBoolean("recents", true)

        //Listeners
        firstRow.setOnClickListener {
            it.setBackgroundResource(R.drawable.shape_rowclick)
            if (booleamSwitch1) {
                startActivity(Intent(requireContext(), FavoritesActivity::class.java))
            } else {
                Toast.makeText(requireContext(), "Favorites switch disabled on settings screen.", Toast.LENGTH_SHORT).show()
            }
        }

        secondRow.setOnClickListener {
            it.setBackgroundResource(R.drawable.shape_rowclick)
            if (booleamSwitch2) {
                startActivity(Intent(requireContext(), RecentsActivity::class.java))
            } else {
                Toast.makeText(requireContext(), "Recents switch disabled on settings screen.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onStart() {
        super.onStart()
        firstRow.setBackgroundResource(R.drawable.shape_row)
        secondRow.setBackgroundResource(R.drawable.shape_row)
    }
}