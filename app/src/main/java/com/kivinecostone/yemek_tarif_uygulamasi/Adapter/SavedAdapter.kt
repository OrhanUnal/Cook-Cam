package com.kivinecostone.yemek_tarif_uygulamasi.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.kivinecostone.yemek_tarif_uygulamasi.R
import com.kivinecostone.yemek_tarif_uygulamasi.database.ChatLogEntity

class SavedAdapter : RecyclerView.Adapter<SavedAdapter.NoteViewHolder>() {

    private var notes = listOf<ChatLogEntity>()
    private var userView = 0
    private var botView = 1
    private var dateBar = 2
    private var imageBar = 3

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.messageText)
        val messageTime: TextView = itemView.findViewById(R.id.messageTime)
        val avatarImage: ImageView = itemView.findViewById(R.id.avatarImage)
        val ivImage: ImageView = itemView.findViewById(R.id.iv_image)
    }

    override fun getItemViewType(position: Int): Int {
        return when (notes[position].isUser) {
            0 -> userView
            1 -> botView
            2 -> dateBar
            else -> imageBar
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
            } dateBar -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note_date_bar, parent, false)
                NoteViewHolder(view)
            } else -> { val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note_image, parent, false)
                NoteViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleView.text = note.title
        holder.messageTime.text = note.time
        when (note.isUser) {
            0 -> {
                holder.avatarImage.setImageResource(R.drawable.ic_user)
                holder.ivImage.visibility = View.GONE
            }
            1 -> {
                holder.avatarImage.setImageResource(R.drawable.ic_bot_chef)
                holder.ivImage.visibility = View.GONE
            }
            2 -> {
                holder.avatarImage.visibility = View.GONE
                holder.messageTime.visibility = View.GONE
                holder.ivImage.visibility = View.GONE
            }else -> {
            holder.avatarImage.setImageResource(R.drawable.ic_user)
            holder.ivImage.setImageBitmap(note.image)
            holder.titleView.visibility = View.GONE
            holder.messageTime.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = notes.size

    fun setData(newNotes: List<ChatLogEntity>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
