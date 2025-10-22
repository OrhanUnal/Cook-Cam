// HomeFragment.kt
package com.kivinecostone.yemek_tarif_uygulamasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kivinecostone.yemek_tarif_uygulamasi.RecipeActivity.AddRecipeActivity
import com.kivinecostone.yemek_tarif_uygulamasi.database.NoteData

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tutorialContainer: View
    private lateinit var adapter: RecipeAdapter
    private lateinit var noteDB: NoteData
    private var recipeCounter = 0

    private val addRecipeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        refreshOnce()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.homeRecyclerView)
        tutorialContainer = view.findViewById(R.id.tutorialContainer)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecipeAdapter(requireContext())
        recyclerView.adapter = adapter

        noteDB = Room.databaseBuilder(
            requireContext(),
            NoteData::class.java,
            "note_database"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        refreshOnce()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAddRecipe: AppCompatImageButton = view.findViewById(R.id.btn_addRecipe)
        btnAddRecipe.setOnClickListener {
            addRecipeLauncher.launch(Intent(requireContext(), AddRecipeActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshOnce()
    }

    private fun refreshOnce() {
        noteDB.recipe().getAllRecipes().observeOnce(viewLifecycleOwner) { recipes ->
            adapter.setData(recipes)
            recipeCounter = recipes.size
            tutorialContainer.visibility = if (recipeCounter == 0) VISIBLE else GONE
        }
    }

    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
        val o = object : Observer<T> {
            override fun onChanged(t: T) {
                removeObserver(this)
                observer(t)
            }
        }
        observe(owner, o)
    }
}
