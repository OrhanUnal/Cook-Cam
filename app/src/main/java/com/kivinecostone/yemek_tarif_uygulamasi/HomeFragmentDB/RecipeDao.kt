package com.kivinecostone.yemek_tarif_uygulamasi.HomeFragmentDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface RecipeDao {
    @Insert
    fun addRecipe(recipe: RecipeTableEntity)

    @Delete
    fun deleteRecipe(recipe: RecipeTableEntity)

    @Update
    fun updateRecipe(recipe: RecipeTableEntity)

    @Query("SELECT * FROM RecipeTableEntity")
    fun getAllRecipes(): LiveData<List<RecipeTableEntity>>

    @Query("SELECT * FROM RecipeTableEntity WHERE id LIKE :id")
    fun getRecipe(id : Int) : RecipeTableEntity

    @Query("SELECT COUNT(*) FROM RecipeTableEntity")
    fun getSavedRecipeCount() : Int
}