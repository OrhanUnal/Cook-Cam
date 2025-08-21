package com.kivinecostone.yemek_tarif_uygulamasi.RecipeActivity

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kivinecostone.yemek_tarif_uygulamasi.HomeFragmentDB.RecipeTableEntity
import com.kivinecostone.yemek_tarif_uygulamasi.R
import com.kivinecostone.yemek_tarif_uygulamasi.database.NoteData

class EditRecipeActivity: AppCompatActivity() {
    private lateinit var recipeTableEntity: RecipeTableEntity
    private lateinit var noteDB: NoteData
    private var recipeId = 0
    private var defaultImage : Bitmap? = null

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
        title.setText(noteDB.recipe().getRecipe(recipeId).recipeTitle)
        desc.setText(noteDB.recipe().getRecipe(recipeId).recipeSelf)
        val btnSave = findViewById<FloatingActionButton>(R.id.btnSave)
        btnSave.setOnClickListener() {
            if (title.text.isNotEmpty() and desc.text.isNotEmpty()) {
                recipeTableEntity =
                    RecipeTableEntity(recipeId, desc.text.toString(), title.text.toString(), null)
                noteDB.recipe().updateRecipe(recipeTableEntity)
                finish()
            } else {
                Toast.makeText(this, "Bos tarif kaydedilemez", Toast.LENGTH_SHORT).show()
            }
        }
    }
}