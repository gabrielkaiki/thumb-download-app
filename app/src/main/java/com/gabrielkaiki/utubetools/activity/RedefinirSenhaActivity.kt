package com.gabrielkaiki.utubetools.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.gabrielkaiki.utubetools.databinding.ActivityRedefinirSenhaBinding
import com.gabrielkaiki.utubetools.helper.getAuth
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RedefinirSenhaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRedefinirSenhaBinding
    private lateinit var botaoEnviarEmail: Button
    private lateinit var inputEmail: TextInputEditText
    private lateinit var textMensagemRedefinicaoSenha: TextView
    private lateinit var autenticacao: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedefinirSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar
        configurarToolbar()

        //Inicializar componentes
        inicializarComponentes()
    }

    private fun inicializarComponentes() {
        botaoEnviarEmail = binding.buttonEnviarEmailRecuperacao
        inputEmail = binding.inputEmailRecuperacao
        textMensagemRedefinicaoSenha = binding.textMensagemRedefinicaoSenha
        autenticacao = getAuth()

        botaoEnviarEmail.setOnClickListener {
            val email = inputEmail.text.toString()
            if (!email.isNullOrEmpty()) {
                autenticacao.sendPasswordResetEmail(email).addOnSuccessListener {
                    textMensagemRedefinicaoSenha.visibility = View.VISIBLE
                }.addOnFailureListener {
                    Toast.makeText(
                        this@RedefinirSenhaActivity,
                        "Error: ${it.message}.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "Please enter your e-mail!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarToolbar() {
        val toolbar = binding.includeToolbar.toolbar
        toolbar.title = "Recover password"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
