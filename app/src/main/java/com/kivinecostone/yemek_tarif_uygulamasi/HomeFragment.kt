package com.kivinecostone.yemek_tarif_uygulamasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kivinecostone.yemek_tarif_uygulamasi.RecipeActivity.AddRecipeActivity
import com.kivinecostone.yemek_tarif_uygulamasi.database.NoteData

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var noteDB: NoteData
    private lateinit var emptyState: View
    private lateinit var btnAddRecipe: AppCompatImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.homeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        adapter = RecipeAdapter(context)
        recyclerView.adapter = adapter

        emptyState = view.findViewById(R.id.emptyState)
        btnAddRecipe = view.findViewById(R.id.btn_addRecipe)

        noteDB = Room.databaseBuilder(
            requireContext(),
            NoteData::class.java,
            "note_database"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        noteDB.recipe().getAllRecipes().observe(viewLifecycleOwner) { recipes ->
            adapter.setData(recipes)
            toggleEmptyState(recipes.isEmpty())
        }

        btnAddRecipe.setOnClickListener {
            startActivity(Intent(requireContext(), AddRecipeActivity::class.java))
        }

        return view
    }

    private fun toggleEmptyState(isEmpty: Boolean) {
        emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}
