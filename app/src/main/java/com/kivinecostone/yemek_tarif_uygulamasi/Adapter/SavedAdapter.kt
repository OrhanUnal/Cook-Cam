package com.kivinecostone.yemek_tarif_uygulamasi.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kivinecostone.yemek_tarif_uygulamasi.R
import com.kivinecostone.yemek_tarif_uygulamasi.database.ChatLogEntity

class SavedAdapter : RecyclerView.Adapter<SavedAdapter.NoteViewHolder>() {

    private var notes = listOf<ChatLogEntity>()
    private var userView = 0
    private var botView = 1
    private var dateBar = 2
    private var currentDate : String = ""

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.messageText)
        val messageTime: TextView = itemView.findViewById(R.id.messageTime)
        val avatarImage: ImageView = itemView.findViewById(R.id.avatarImage)
    }

    override fun getItemViewType(position: Int): Int {
        return if (notes[position].is_user) {
            userView
        } else if (!notes[position].is_user) {
            botView
        } else{
            dateBar
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return when (viewType){
            userView -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note_user, parent, false)
                NoteViewHolder(view)
            }
            botView -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note_bot, parent, false)
                NoteViewHolder(view)
            } else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note_date_bar, parent, false)
                NoteViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleView.text = note.title
        holder.messageTime.text = note.time
        if (note.is_user) {
            holder.avatarImage.setImageResource(R.drawable.ic_user)
        } else {
            holder.avatarImage.setImageResource(R.drawable.ic_bot_chef)
        }
    }

    override fun getItemCount(): Int = notes.size

    fun setData(newNotes: List<ChatLogEntity>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
