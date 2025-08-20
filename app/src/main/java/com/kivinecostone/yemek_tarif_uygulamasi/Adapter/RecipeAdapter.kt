package com.kivinecostone.yemek_tarif_uygulamasi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.kivinecostone.yemek_tarif_uygulamasi.HomeFragmentDB.RecipeTableEntity
import com.kivinecostone.yemek_tarif_uygulamasi.database.ChatLogEntity

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.NoteViewHolder>() {

    private var recipes = listOf<RecipeTableEntity>()

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tv_recipe_self)
        val titleView: TextView = itemView.findViewById(R.id.tv_recipe_title)
        val ivImage: ImageView = itemView.findViewById(R.id.iv_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = recipes[position]
        holder.textView.text = note.recipeSelf
        holder.titleView.text = note.recipeTitle
        holder.ivImage.setImageBitmap(note.image)
    }

    override fun getItemCount(): Int = recipes.size

    fun setData(newRecipes: List<RecipeTableEntity>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}
