package com.gabrielkaiki.utubetools.activity.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.gabrielkaiki.utubetools.R
import com.gabrielkaiki.utubetools.activity.ExcluirContaActivity
import com.gabrielkaiki.utubetools.activity.UpdatePasswordActivity
import com.gabrielkaiki.utubetools.databinding.FragmentSettingsBinding
import com.gabrielkaiki.utubetools.helper.currentUser
import com.gabrielkaiki.utubetools.helper.getStorage
import com.gabrielkaiki.utubetools.helper.getUserId
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

class SettingsFragment : Fragment() {

    private var binding: FragmentSettingsBinding? = null
    private lateinit var fieldName: TextInputEditText
    private lateinit var fieldEmail: TextInputEditText
    private lateinit var fieldPassword: TextInputEditText
    private lateinit var buttonEditName: Button
    private lateinit var buttonEditEmail: Button
    private lateinit var imagePerfil: CircleImageView
    private lateinit var buttonEditPassword: Button
    private lateinit var buttonEditImage: Button
    private lateinit var textUpdatePassword: TextView
    private lateinit var textDeleteAccount: TextView
    private lateinit var buttonSave: Button
    private lateinit var switch1: Switch
    private lateinit var switch2: Switch
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor
    private lateinit var laucher: ActivityResultLauncher<Intent>
    private var KEY_CHOOSE = 0
    private var urlPerfil: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        //Components
        inicializateComponents()

        //Definição de campos de dados
        populateFields()

        //Activity launcher
        iniciaLauncher()

        return root
    }

    private fun iniciaLauncher() {
        laucher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                var image: Bitmap? = null
                when (KEY_CHOOSE) {
                    0 -> {
                        val url = it.data!!.data
                        image =
                            MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                url
                            )
                    }

                    1 -> {
                        image = it.data!!.extras!!.get("data") as Bitmap
                    }
                }
                imagePerfil.setImageBitmap(image)
                uploadImage(image)
            }
        }
    }

    private fun uploadImage(image: Bitmap?) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setCancelable(false).setView(R.layout.loading_screen).create().apply { show() }

        val baos = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.PNG, 80, baos)
        val dadosImagens = baos.toByteArray()

        val path = "${getUserId()}/perfil/perfil.jpg"
        val storageRef = getStorage().child(path)

        storageRef.putBytes(dadosImagens).addOnCompleteListener {
            if (it.isSuccessful) {
                storageRef.downloadUrl.addOnCompleteListener {
                    urlPerfil = it.result.toString()
                }
            } else {
                Toast.makeText(requireContext(), "Error uploading image!", Toast.LENGTH_SHORT)
                    .show()
            }
            alertDialog.dismiss()
        }
    }

    private fun saveDataBase() {
        val name = fieldName.text.toString()
        val email = fieldEmail.text.toString()

        if (!name.isNullOrEmpty()) {
            if (!email.isNullOrEmpty()) {
                val user = currentUser
                user?.name = name
                user?.email = email
                user?.pathPhoto = urlPerfil
                if (user!!.salvar()) {
                    currentUser?.name = name
                    currentUser?.email = email
                    currentUser?.pathPhoto = urlPerfil
                    Toast.makeText(requireContext(), "Saved successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Error saving", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(requireContext(), "Please enter a e-mail!", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), "Please enter a name!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inicializateComponents() {
        fieldName = binding!!.textInputNameSettings
        fieldEmail = binding!!.textInputEmailSettings
        buttonEditName = binding!!.buttonEditNameSettings
        buttonEditEmail = binding!!.buttonEditEmailSettings
        buttonEditImage = binding!!.buttonEditImageSettings
        imagePerfil = binding!!.imagePerfilSettings
        switch1 = binding!!.switch1
        switch2 = binding!!.switch2
        buttonSave = binding!!.buttonSaveChanges
        textUpdatePassword = binding!!.textUpdatePassword
        textDeleteAccount = binding!!.textDeleteAccount

        sharedPreferences =
            requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    private fun populateFields() {
        fieldName.setText(currentUser!!.name)
        fieldEmail.setText(currentUser!!.email)

        val photoPerson = currentUser?.pathPhoto
        urlPerfil = photoPerson

        if (photoPerson.isNullOrEmpty()) {
            imagePerfil.setImageResource(R.drawable.person_perfil)
        } else {
            Picasso.get().load(Uri.parse(currentUser?.pathPhoto)).into(imagePerfil)
        }


        val booleamSwitch1 = sharedPreferences.getBoolean("recents", true)
        val booleamSwitch2 = sharedPreferences.getBoolean("favorites", true)

        switch1.isChecked = booleamSwitch1
        switch2.isChecked = booleamSwitch2

        clickEventButtons()
    }

    private fun clickEventButtons() {
        buttonEditName.setOnClickListener {
            fieldName.isEnabled = true
        }

        buttonEditEmail.setOnClickListener {
            fieldEmail.isEnabled = true
        }

        buttonEditImage.setOnClickListener {
            alertDialogImage()
        }

        switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) editor.putBoolean("recents", true) else editor.putBoolean(
                "recents",
                false
            )
            editor.apply()
        }

        switch2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) editor.putBoolean("favorites", true) else editor.putBoolean(
                "favorites",
                false
            )
            editor.apply()
        }

        buttonSave.setOnClickListener {
            saveDataBase()
        }

        textUpdatePassword.setOnClickListener {
            startActivity(Intent(requireContext(), UpdatePasswordActivity::class.java))
        }

        textDeleteAccount.setOnClickListener {
            startActivity(Intent(requireContext(), ExcluirContaActivity::class.java))
        }
    }

    private fun alertDialogImage() {
        val constructor = AlertDialog.Builder(requireContext())
            .setTitle("Choose one option")

        constructor.setPositiveButton("Gallery") { _, _ ->
            KEY_CHOOSE = 0
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            laucher.launch(intent)
        }

        constructor.setNegativeButton("Camera") { _, _ ->
            KEY_CHOOSE = 1
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            laucher.launch(intent)
        }

        constructor.setNeutralButton("Remove") { _, _ ->
            imagePerfil.setImageResource(R.drawable.person_perfil)
            urlPerfil = null
        }


        val dialog = constructor.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}