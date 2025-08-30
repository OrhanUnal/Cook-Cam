package com.kivinecostone.yemek_tarif_uygulamasi

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.kivinecostone.yemek_tarif_uygulamasi.HomeFragmentDB.RecipeTableEntity
import com.kivinecostone.yemek_tarif_uygulamasi.RecipeActivity.DeleteRecipeActivity
import com.kivinecostone.yemek_tarif_uygulamasi.RecipeActivity.EditRecipeActivity

class RecipeAdapter (var mContext: Context?) : RecyclerView.Adapter<RecipeAdapter.NoteViewHolder>() {

    private var recipes = listOf<RecipeTableEntity>()

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tv_recipe_self)
        val titleView: TextView = itemView.findViewById(R.id.tv_recipe_title)
        val ivImage: ImageView = itemView.findViewById(R.id.iv_image)
        val btnViewMore: ImageButton = itemView.findViewById(R.id.viewMoreButton)
        val btnShowLess: ImageButton = itemView.findViewById(R.id.showLessButton)
        val btnDelete: ImageButton = itemView.findViewById(R.id.deleteButton)
        val btnEdit: ImageButton = itemView.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = recipes[position]
        if (note.recipeSelf.length > 20)
            holder.textView.text = note.recipeSelf.removeRange(20, note.recipeSelf.length)
        else
            holder.textView.text = note.recipeSelf
        holder.titleView.text = note.recipeTitle
        holder.ivImage.setImageBitmap(note.image)
        holder.btnShowLess.visibility = View.GONE
        holder.btnEdit.visibility = View.GONE
        holder.btnDelete.visibility = View.GONE
        holder.btnViewMore.setOnClickListener {
            holder.textView.text = note.recipeSelf
            holder.btnShowLess.visibility = View.VISIBLE
            holder.btnViewMore.visibility = View.GONE
            holder.btnEdit.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.VISIBLE
        }
        holder.btnShowLess.setOnClickListener {
            if (note.recipeSelf.length > 20)
                holder.textView.text = note.recipeSelf.removeRange(20, note.recipeSelf.length)
            holder.btnShowLess.visibility = View.GONE
            holder.btnViewMore.visibility = View.VISIBLE
            holder.btnEdit.visibility = View.GONE
            holder.btnDelete.visibility = View.GONE
        }
        holder.btnDelete.setOnClickListener {
            val intent = Intent(mContext, DeleteRecipeActivity::class.java)
            intent.putExtra("bundle_id", note.id)
            mContext?.startActivity(intent)
            holder.btnViewMore.visibility = View.VISIBLE
        }
        holder.btnEdit.setOnClickListener {
            val intent = Intent(mContext, EditRecipeActivity::class.java)
            intent.putExtra("bundle_id", note.id)
            mContext?.startActivity(intent)
            holder.btnViewMore.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = recipes.size

    fun setData(newRecipes: List<RecipeTableEntity>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}
