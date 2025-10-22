package com.kivinecostone.yemek_tarif_uygulamasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
    private lateinit var emptyText: TextView
    private lateinit var emptyImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.homeRecyclerView)
        emptyText = view.findViewById(R.id.emptyTextView)
        emptyImage = view.findViewById(R.id.emptyImageView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecipeAdapter(context)
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
        val btnAddRecipe: AppCompatImageButton = view.findViewById(R.id.btn_addRecipe)
        btnAddRecipe.setOnClickListener{
            startActivity(Intent(requireContext(), AddRecipeActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        noteDB.recipe().getAllRecipes().observe(viewLifecycleOwner){recipes ->
            adapter.setData(recipes)
        }
        val counter = noteDB.recipe().getSavedRecipeCount()
        if (counter > 0) {
            emptyText.visibility = View.GONE
            emptyImage.visibility = View.GONE
        }
        else {
            emptyText.visibility = View.VISIBLE
            emptyImage.visibility = View.VISIBLE
        }
    }
}
