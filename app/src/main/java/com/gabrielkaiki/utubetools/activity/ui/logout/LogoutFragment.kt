package com.gabrielkaiki.utubetools.activity.ui.logout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gabrielkaiki.utubetools.activity.LoginActivity
import com.gabrielkaiki.utubetools.databinding.FragmentLogoutBinding
import com.gabrielkaiki.utubetools.helper.getAuth

class LogoutFragment : Fragment() {
    private lateinit var binding: FragmentLogoutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogoutBinding.inflate(layoutInflater, container, false)

        getAuth().signOut()
        startActivity(Intent(requireContext(), LoginActivity::class.java))

        return binding.root
    }
}