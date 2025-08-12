package com.kivinecostone.yemek_tarif_uygulamasi

data class ChatMessage(
    var message: String,
    val isUser: Boolean,
    val isTyping: Boolean = false,
    val time: String,
    val date: String
)
