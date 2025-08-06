package com.kivinecostone.yemek_tarif_uygulamasi
data class ChatMessage(
    val message: String,
    val isUser: Boolean = false,
    val isTyping: Boolean = false
)
