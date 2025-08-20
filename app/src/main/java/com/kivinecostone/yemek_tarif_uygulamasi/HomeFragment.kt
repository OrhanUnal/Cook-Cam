package com.kivinecostone.yemek_tarif_uygulamasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kivinecostone.yemek_tarif_uygulamasi.HomeFragmentDB.RecipeTableEntity
import com.kivinecostone.yemek_tarif_uygulamasi.database.NoteData

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var noteDB: NoteData
    private lateinit var recipeTableEntity: RecipeTableEntity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.homeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecipeAdapter()
        recyclerView.adapter = adapter

        noteDB = Room.databaseBuilder(
            requireContext(),
            NoteData::class.java,
            "note_database"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        noteDB.recipe().getAllRecipes().observe(viewLifecycleOwner){recipes ->
            adapter.setData(recipes)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btn_addRecipe: AppCompatImageButton = view.findViewById<AppCompatImageButton>(R.id.btn_addRecipe)
        btn_addRecipe.setOnClickListener{
            startActivity(Intent(requireContext(), AddNoteActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        noteDB.recipe().getAllRecipes().observe(viewLifecycleOwner){recipes ->
            adapter.setData(recipes)
        }
    }
}
