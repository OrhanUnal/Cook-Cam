package com.kivinecostone.yemek_tarif_uygulamasi.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kivinecostone.yemek_tarif_uygulamasi.R
import com.kivinecostone.yemek_tarif_uygulamasi.database.ChatLogEntity

class SavedAdapter : RecyclerView.Adapter<SavedAdapter.NoteViewHolder>() {

    private var notes = listOf<ChatLogEntity>()

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.textViewSavedAiTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleView.text = note.title
        if (!note.is_user){
            holder.titleView.textSize = 50f
        }
    }

    override fun getItemCount(): Int = notes.size

    fun setData(newNotes: List<ChatLogEntity>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
