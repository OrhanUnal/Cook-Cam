package com.kivinecostone.yemek_tarif_uygulamasi.RecipeActivity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.kivinecostone.yemek_tarif_uygulamasi.HomeFragmentDB.RecipeTableEntity
import com.kivinecostone.yemek_tarif_uygulamasi.database.NoteData

class DeleteRecipeActivity : AppCompatActivity() {
    private val noteDB: NoteData by lazy {
        Room.databaseBuilder(
            this,
            NoteData::class.java,
            "note_database"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    private lateinit var recipeEntity: RecipeTableEntity
    private var recipeId = 0
    private var defaultTitle = ""
    private var defaultDesc = ""
    private var defaultImage : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.extras?.let {
            recipeId = it.getInt("bundle_id")
        }
        defaultTitle=noteDB.recipe().getRecipe(recipeId).recipeTitle
        defaultDesc=noteDB.recipe().getRecipe(recipeId).recipeSelf
        recipeEntity = RecipeTableEntity(recipeId,defaultDesc,defaultTitle, defaultImage)
        noteDB.recipe().deleteRecipe(recipeEntity)
        finish()
    }
}