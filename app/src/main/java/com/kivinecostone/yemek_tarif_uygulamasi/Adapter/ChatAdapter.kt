package com.kivinecostone.yemek_tarif_uygulamasi.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kivinecostone.yemek_tarif_uygulamasi.ChatMessage
import com.kivinecostone.yemek_tarif_uygulamasi.R

class ChatAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeUser = 0
    private val viewTypeBot = 1
    private val viewTypeTyping = 2

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val messageTime: TextView = itemView.findViewById(R.id.messageTime)
        val avatarImage: ImageView = itemView.findViewById(R.id.avatarImage)
    }

    inner class TypingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemViewType(position: Int): Int {
        return when {
            messages[position].isTyping -> viewTypeTyping
            messages[position].isUser -> viewTypeUser
            else -> viewTypeBot
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            viewTypeUser -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_user, parent, false)
                MessageViewHolder(view)
            }
            viewTypeBot -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_bot, parent, false)
                MessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_typing, parent, false)
                TypingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is MessageViewHolder) {
            holder.messageText.text = message.message
            holder.messageTime.text = "${message.time}  ·  ${message.date}"
            if (message.isUser) {
                holder.avatarImage.setImageResource(R.drawable.ic_user)
            } else {
                holder.avatarImage.setImageResource(R.drawable.ic_bot_chef)
            }
        }
    }

    override fun getItemCount(): Int = messages.size
}
