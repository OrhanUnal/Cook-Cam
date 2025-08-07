package com.kivinecostone.yemek_tarif_uygulamasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kivinecostone.yemek_tarif_uygulamasi.Adapter.SavedAdapter
import com.kivinecostone.yemek_tarif_uygulamasi.database.NoteData

class SavedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SavedAdapter // Yeni bir adapter tanımlayacağız
    private lateinit var noteDB: NoteData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saved, container, false)

        recyclerView = view.findViewById(R.id.savedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SavedAdapter()
        recyclerView.adapter = adapter

        noteDB = Room.databaseBuilder(
            requireContext(),
            NoteData::class.java,
            "note_database"
        ).allowMainThreadQueries().build()

        noteDB.dao().getAllNotes().observe(viewLifecycleOwner) { notes ->
            adapter.setData(notes)
        }

        return view
    }
}
