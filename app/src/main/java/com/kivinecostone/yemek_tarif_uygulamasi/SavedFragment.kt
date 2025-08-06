package com.kivinecostone.yemek_tarif_uygulamasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.kivinecostone.yemek_tarif_uygulamasi.database.NoteData

class SavedFragment : Fragment() {
    companion object {
        lateinit var noteData: NoteData
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Room.databaseBuilder(
            requireContext(),
            NoteData::class.java,
            NoteData.NAME
        ).build()
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }
}
