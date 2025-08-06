package com.kivinecostone.yemek_tarif_uygulamasi.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatLogEntity(
    @PrimaryKey(autoGenerate = true)
    val noteId: Int = 0,
    val title : String
)
