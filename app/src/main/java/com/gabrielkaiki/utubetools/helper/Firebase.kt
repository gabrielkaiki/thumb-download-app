package com.gabrielkaiki.utubetools.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private var auth: FirebaseAuth? = null
private var db: DatabaseReference? = null
private var storage: StorageReference? = null

fun getDataBase(): DatabaseReference {
    if (db == null) db = Firebase.database.reference
    return db!!
}

fun getAuth(): FirebaseAuth {
    if (auth == null) auth = FirebaseAuth.getInstance()
    return auth!!
}

fun getStorage(): StorageReference {
    if (storage == null) storage = FirebaseStorage.getInstance().reference
    return storage!!
}