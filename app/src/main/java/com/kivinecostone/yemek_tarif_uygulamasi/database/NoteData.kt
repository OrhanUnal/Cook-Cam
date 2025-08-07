package com.kivinecostone.yemek_tarif_uygulamasi.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [ChatLogEntity::class], version = 2)
abstract class NoteData: RoomDatabase(){
    companion object{
        const val NAME = "Notes"
    }
    abstract fun dao():NoteDao
}