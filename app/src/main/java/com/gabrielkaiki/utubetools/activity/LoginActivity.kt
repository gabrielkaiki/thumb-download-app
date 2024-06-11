package com.gabrielkaiki.utubetools.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.gabrielkaiki.utubetools.R
import com.gabrielkaiki.utubetools.databinding.ActivityLoginBinding
import com.gabrielkaiki.utubetools.helper.currentUser
import com.gabrielkaiki.utubetools.helper.getAuth
import com.gabrielkaiki.utubetools.helper.getDataBase
import com.gabrielkaiki.utubetools.helper.getUserId
import com.gabrielkaiki.utubetools.model.Usuario
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.sn.lib.NestedProgress

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var buttonLogin: Button
    private lateinit var email: TextInputEditText
    private lateinit var linearLayout: LinearLayout
    private lateinit var name: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var switch: Switch
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var layoutName: TextInputLayout
    private var auth = getAuth()
    private var dataBase = getDataBase()
    private lateinit var progressBar: NestedProgress
    private lateinit var esqueceuSenha: TextView
    private lateinit var mAdView: AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}

        //Componentes
        inicializaComponentes()
    }

    private fun inicializaComponentes() {
        buttonLogin = binding.buttonLogin
        switch = binding.switchLogin
        email = binding.textInputEmail
        password = binding.textInputPassword
        name = binding.textInputName
        layoutName = binding.textInputLayoutName
        progressBar = binding.progressBar
        esqueceuSenha = binding.textRedefinirSenha
        linearLayout = binding.linearLayoutPrincipal
        constraintLayout = binding.constraint

        //Listener switch
        addListenerSwitch()

        //Listener botão
        addListenerButton()

        //Listener redefinir senha
        addListenerRedefinirSenha()
    }

    private fun addListenerRedefinirSenha() {
        esqueceuSenha.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RedefinirSenhaActivity::class.java))
        }
    }

    private fun addListenerButton() {
        buttonLogin.setOnClickListener {
            if (switch.isChecked) signUp() else signIn()
        }
    }

    private fun signIn() {
        progressBar.visibility = View.VISIBLE

        val usuario = getUserAndCheckFields()
        if (usuario != null) {
            auth.signInWithEmailAndPassword(usuario.email!!, usuario.senha!!)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "login success", Toast.LENGTH_SHORT)
                            .show()
                        retrieveUser()
                    } else {
                        progressBar.visibility = View.GONE
                        try {
                            throw it.exception!!
                        } catch (e: Exception) {
                            Toast.makeText(this@LoginActivity, "${e.message}", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
        }
    }

    private fun signUp() {
        progressBar.visibility = View.VISIBLE
        val usuario = getUserAndCheckFields()
        if (usuario != null) {
            auth.createUserWithEmailAndPassword(usuario.email!!, usuario.senha!!)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        usuario.id = getUserId()
                        if (usuario.salvar()) {
                            Toast.makeText(
                                this@LoginActivity, "Success saving user!", Toast.LENGTH_SHORT
                            ).show()
                            retrieveUser()
                        } else {
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@LoginActivity, "Error saving user!", Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        try {
                            throw it.exception!!
                        } catch (e: Exception) {
                            Toast.makeText(this, "${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, NavegacaoActivity::class.java)
        intent.putExtra("usuário", currentUser)
        startActivity(intent)
        finish()
        progressBar.visibility = View.GONE
    }

    private fun getUserAndCheckFields(): Usuario? {
        val textName = name.text.toString()
        val textEmail = email.text.toString()
        val textPassword = password.text.toString()

        if (!textName.isNullOrEmpty() || !switch.isChecked) {
            if (!textEmail.isNullOrEmpty()) {
                if (!textPassword.isNullOrEmpty()) {
                    val user = Usuario()
                    if (switch.isChecked) user.name = textName
                    user.email = textEmail
                    user.senha = textPassword

                    return user
                } else {
                    Toast.makeText(this, "Please enter a password!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a e-mail!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please enter a name!", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    private fun addListenerSwitch() {
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                buttonLogin.text = "Sign up"
                layoutName.visibility = View.VISIBLE
                esqueceuSenha.visibility = View.GONE
            } else {
                buttonLogin.text = "Sign in"
                layoutName.visibility = View.GONE
                esqueceuSenha.visibility = View.VISIBLE
            }
        }
    }

    override fun onStart() {
        //auth.signOut()
        if (auth.currentUser != null) {
            progressBar.visibility = View.VISIBLE
            retrieveUser()
        }

        //Anúncio
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        super.onStart()
    }

    private fun retrieveUser() {
        val userRef = dataBase.child("users").child(getUserId()!!)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(Usuario::class.java)!!

                startMainActivity()
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
            }

        })
    }
}