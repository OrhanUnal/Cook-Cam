package com.kivinecostone.yemek_tarif_uygulamasi.RecipeActivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kivinecostone.yemek_tarif_uygulamasi.CameraFragment.Companion.CAMERA_PERMISSION_CODE
import com.kivinecostone.yemek_tarif_uygulamasi.CameraFragment.Companion.CAMERA_REQUEST_CODE
import com.kivinecostone.yemek_tarif_uygulamasi.HomeFragmentDB.RecipeTableEntity
import com.kivinecostone.yemek_tarif_uygulamasi.R
import com.kivinecostone.yemek_tarif_uygulamasi.database.NoteData

class EditRecipeActivity: AppCompatActivity() {
    private lateinit var recipeTableEntity: RecipeTableEntity
    private lateinit var noteDB: NoteData
    private var recipeId = 0
    private var defaultImage : Bitmap? = null
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        noteDB = Room.databaseBuilder(
            this,
            NoteData::class.java,
            "note_database"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        intent.extras?.let {
            recipeId = it.getInt("bundle_id")
        }
        val title = findViewById<EditText>(R.id.edtTitle)
        val desc = findViewById<EditText>(R.id.edtDesc)
        image = findViewById(R.id.iv_image)
        image.setImageBitmap(noteDB.recipe().getRecipe(recipeId).image)
        title.setText(noteDB.recipe().getRecipe(recipeId).recipeTitle)
        desc.setText(noteDB.recipe().getRecipe(recipeId).recipeSelf)
        val btnSave = findViewById<FloatingActionButton>(R.id.btnSave)
        btnSave.setOnClickListener() {
            if (title.text.isNotEmpty() and desc.text.isNotEmpty()) {
                recipeTableEntity =
                    RecipeTableEntity(recipeId, desc.text.toString(), title.text.toString(), defaultImage)
                noteDB.recipe().updateRecipe(recipeTableEntity)
                finish()
            } else {
                Toast.makeText(this, "Bos tarif kaydedilemez", Toast.LENGTH_SHORT).show()
            }
        }
        image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(this, "Kamera izni kabul edilmedi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
            defaultImage = data!!.extras!!.get("data") as Bitmap
            image.setImageBitmap(defaultImage)
        }
    }
}