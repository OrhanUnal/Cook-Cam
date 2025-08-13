package com.kivinecostone.yemek_tarif_uygulamasi.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [ChatLogEntity::class], version = 7)
abstract class NoteData: RoomDatabase(){
    abstract fun dao():NoteDao
}