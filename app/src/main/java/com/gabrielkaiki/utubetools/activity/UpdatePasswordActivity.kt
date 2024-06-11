package com.gabrielkaiki.utubetools.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.gabrielkaiki.utubetools.databinding.ActivityUpdatePasswordBinding
import com.gabrielkaiki.utubetools.helper.getAuth
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider

class UpdatePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePasswordBinding
    private lateinit var buttonSave: Button
    private lateinit var fieldCurrentPassword: TextInputEditText
    private lateinit var fieldNewPassword: TextInputEditText
    private lateinit var fieldConfirmPassword: TextInputEditText
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar
        val toolbar = binding.includeToolbar.toolbar
        toolbar.title = "Update Password"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        //Componentes
        componentes()
    }

    private fun componentes() {
        buttonSave = binding.buttonSaveResetPass
        fieldConfirmPassword = binding.textConfirmPassResetPass
        fieldNewPassword = binding.textNewResetPass
        fieldCurrentPassword = binding.textCurrentResetPass
        progressBar = binding.progressBarUpdatePass

        buttonSave.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            refreshLogin()
        }
    }

    private fun refreshLogin() {
        val usuarioAuth = getAuth().currentUser

        if (validateFields()) {
            val email = usuarioAuth!!.email
            val senha = fieldCurrentPassword.text.toString()
            val novaSenha = fieldNewPassword.text.toString()

            val data = EmailAuthProvider.getCredential(email!!, senha)

            usuarioAuth.reauthenticate(data).addOnSuccessListener {
                saveNewPassword(novaSenha)
            }.addOnFailureListener {
                Toast.makeText(
                    this@UpdatePasswordActivity, it.message, Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            progressBar.visibility = View.GONE
        }
    }

    private fun validateFields(): Boolean {
        val currentPassword = fieldCurrentPassword.text.toString()
        val newPassword = fieldNewPassword.text.toString()
        val confirmNewPassword = fieldConfirmPassword.text.toString()

        val areEquals = newPassword == confirmNewPassword

        if (!currentPassword.isNullOrEmpty()) {
            if (!newPassword.isNullOrEmpty()) {
                if (!confirmNewPassword.isNullOrEmpty()) {
                    if (areEquals) {
                        return true
                    } else {
                        Toast.makeText(this, "The passwords are not equals!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Please enter a confirm new password!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Please enter a new password!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please enter a current password!", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun saveNewPassword(newPassword: String) {
        val user = getAuth().currentUser!!

        user.updatePassword(newPassword).addOnSuccessListener {
            progressBar.visibility = View.GONE
            Toast.makeText(
                this@UpdatePasswordActivity,
                "Success updating password!",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            Toast.makeText(
                this@UpdatePasswordActivity,
                it.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}