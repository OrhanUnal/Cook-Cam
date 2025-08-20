package com.kivinecostone.yemek_tarif_uygulamasi.HomeFragmentDB

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeTableEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeSelf: String,
    val recipeTitle : String,
    val image: Bitmap? = null
)