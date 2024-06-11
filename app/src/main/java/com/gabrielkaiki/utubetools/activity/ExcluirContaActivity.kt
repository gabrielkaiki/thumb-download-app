package com.gabrielkaiki.utubetools.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gabrielkaiki.utubetools.databinding.ActivityExcluirContaBinding
import com.gabrielkaiki.utubetools.helper.getAuth
import com.gabrielkaiki.utubetools.helper.getDataBase
import com.gabrielkaiki.utubetools.helper.getStorage
import com.gabrielkaiki.utubetools.helper.getUserId
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser

class ExcluirContaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExcluirContaBinding
    private lateinit var botaoExcluir: Button
    private lateinit var textoMotivoExclusaoConta: EditText
    private lateinit var usuarioAtual: FirebaseUser
    lateinit var authCredential: AuthCredential

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExcluirContaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar
        val toolbar = binding.include.toolbar
        toolbar.title = "Delete account"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        //Components
        initializeComponents()
    }

    private fun initializeComponents() {
        textoMotivoExclusaoConta = binding.editTextMotivo
        botaoExcluir = binding.buttonExcluir
        usuarioAtual = getAuth().currentUser!!

        //Botão de excluir usuário listener
        botaoExcluir.setOnClickListener {
            val idUser = getUserId()

            var campoSenha = TextInputEditText(this)
            campoSenha.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            var builder = AlertDialog.Builder(this)
                .setTitle("Please enter your password.")
                .setView(campoSenha)

            builder.setPositiveButton("Confirm", null)

            builder.setNegativeButton("Close") { _, _ ->

            }

            var alert = builder.create()
            alert.show()

            var positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                if (!campoSenha.text.isNullOrEmpty()) {
                    val senha = campoSenha.text.toString()
                    authCredential = EmailAuthProvider.getCredential(usuarioAtual.email!!, senha)
                    usuarioAtual.reauthenticate(authCredential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            removerDadosDoUsuario(idUser, senha)
                            alert.dismiss()
                        } else {
                            Toast.makeText(
                                this@ExcluirContaActivity,
                                "Incorrect password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@ExcluirContaActivity,
                        "To delete the account, you must enter the password again for security.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun removerDadosDoUsuario(idUser: String, senha: String) {
        val refPerfilStorage = getStorage().child(idUser).child("perfil").child("perfil.jpg")

        val excluidos = hashMapOf<String, Any?>()
        excluidos["/favorites/${idUser}"] = null
        excluidos["/recents/${idUser}"] = null
        excluidos["/users/${idUser}"] = null
        getDataBase().updateChildren(excluidos)

        refPerfilStorage.delete()

        //Se houver um motivo da exclusão, salvar no banco de dados
        if (!textoMotivoExclusaoConta.text.isNullOrEmpty())
            salvarMotivoExclusao(idUser)

        //Deletar autenticação do usuário
        usuarioAtual.delete()
        getAuth().signOut()

        //Redirecionar para tela de login
        startActivity(Intent(this@ExcluirContaActivity, LoginActivity::class.java))
    }

    private fun salvarMotivoExclusao(idUsuarioLogado: String) {
        val motivoExclusaoRef =
            getDataBase().child("contas").child("exclusao").child(idUsuarioLogado)
                .child("motivo")

        val motivo = textoMotivoExclusaoConta.text.toString()
        motivoExclusaoRef.setValue(motivo)
    }
}