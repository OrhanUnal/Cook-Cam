package com.kivinecostone.yemek_tarif_uygulamasi.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteDao {
    @Query("SELECT * FROM ChatLogEntity")
    fun getAllNotes() : LiveData<List<ChatLogEntity>>

    @Query("Delete FROM ChatLogEntity where noteId = :id")
    fun deleteNote(id: Int)

    @Insert
    fun addNote(note : ChatLogEntity)
}