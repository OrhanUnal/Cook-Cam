package com.kivinecostone.yemek_tarif_uygulamasi.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(Converter::class)
@Database(entities = [ChatLogEntity::class], version = 8)
abstract class NoteData: RoomDatabase(){
    abstract fun dao():NoteDao
}