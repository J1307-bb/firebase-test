package com.jairbb.firebase_test

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import org.checkerframework.common.subtyping.qual.Bottom
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botton_take = findViewById<Button>(R.id.btn_camara)

        imageView = findViewById(R.id.imageView)
        botton_take.setOnClickListener {
            dispachTakePictureIntent()
        }

        val db = FirebaseFirestore.getInstance()

        db.collection("ciudades")
            .document("wVZ3DsF3MsnPUQrCga3K")
            .get().addOnSuccessListener { document ->
                document?.let {
                    Log.d("Firebase", "documentoSnapshot data: ${document.data}")
                    val nombre = document.getString("nombre")
                    val poblacion = document.getLong("poblacion")
                    Log.d("Firebase", "La ciudad: ${nombre}, tiene una poblacion de: ${poblacion} personas.")
                }
        }.addOnFailureListener() { exception ->
            Log.e("Firebase", "Error en la BaseDeFuego: ", exception)
        }

    }

    private fun dispachTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Log.e("Error", "No se pudo abrir la camara")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            uploadPicture(imageBitmap)
        }
    }

    private fun uploadPicture(bitmap: Bitmap) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images")
        val imageRef = imagesRef.child("image.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Log.e("Firebase", "Error al subir la imagen")
        }.addOnSuccessListener {
            Log.d("Firebase", "Imagen subida con exito")
        }
    }



}