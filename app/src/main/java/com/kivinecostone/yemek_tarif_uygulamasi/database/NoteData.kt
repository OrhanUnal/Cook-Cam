package com.kivinecostone.yemek_tarif_uygulamasi.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kivinecostone.yemek_tarif_uygulamasi.HomeFragmentDB.RecipeDao
import com.kivinecostone.yemek_tarif_uygulamasi.HomeFragmentDB.RecipeTableEntity

@TypeConverters(Converter::class)
@Database(entities = [ChatLogEntity::class, RecipeTableEntity::class], version = 10, exportSchema = false)
abstract class NoteData: RoomDatabase(){
    abstract fun dao():NoteDao
    abstract fun recipe(): RecipeDao
}