package com.kivinecostone.yemek_tarif_uygulamasi.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converter {

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? =
        bitmap?.let {
            ByteArrayOutputStream().apply {
                it.compress(Bitmap.CompressFormat.PNG, 100, this)
            }.toByteArray()
        }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? =
        byteArray?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }
}
