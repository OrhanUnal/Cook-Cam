package com.kivinecostone.yemek_tarif_uygulamasi.RecipeActivity

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kivinecostone.yemek_tarif_uygulamasi.HomeFragmentDB.RecipeTableEntity
import com.kivinecostone.yemek_tarif_uygulamasi.R
import com.kivinecostone.yemek_tarif_uygulamasi.database.NoteData

class AddRecipeActivity : AppCompatActivity()
{
    private lateinit var recipeTableEntity: RecipeTableEntity
    private lateinit var noteDB: NoteData

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

        val btnSave = findViewById<FloatingActionButton>(R.id.btnSave)
        btnSave.setOnClickListener(){
            val title = findViewById<EditText>(R.id.edtTitle)
            val desc = findViewById<EditText>(R.id.edtDesc)
            if (title.text.isNotEmpty() and desc.text.isNotEmpty()){
                recipeTableEntity =
                    RecipeTableEntity(0, desc.text.toString(), title.text.toString(), null)
                noteDB.recipe().addRecipe(recipeTableEntity)
                finish()
            }
            else{
                Toast.makeText(this, "Bos tarif kaydedilemez", Toast.LENGTH_SHORT).show()
            }
        }
    }
}