package com.kivinecostone.yemek_tarif_uygulamasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AiChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userInput: EditText
    private lateinit var sendButton: Button
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    private val OPENAI_API_KEY = "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"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ai_chat, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewChat)
        userInput = view.findViewById(R.id.userInput)
        sendButton = view.findViewById(R.id.sendButton)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        sendButton.setOnClickListener {
            val text = userInput.text.toString().trim()
            if (text.isNotEmpty()) {
                addMessage(ChatMessage(text, isUser = true))
                userInput.setText("")
                showTypingIndicator()
                getAiResponse(text)
            }
        }

        return view
    }

    private fun addMessage(msg: ChatMessage) {
        messages.add(msg)
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun showTypingIndicator() {
        messages.add(ChatMessage("", isTyping = true))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun removeTypingIndicator() {
        val index = messages.indexOfFirst { it.isTyping }
        if (index != -1) {
            messages.removeAt(index)
            adapter.notifyItemRemoved(index)
        }
    }

    private fun getAiResponse(userQuestion: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()

                val prompt = """
Sen yemek tarifleri, yemek pişirme teknikleri, mutfak kültürü, gıda, beslenme, diyet ve sağlıklı yaşam konularında uzman bir asistansın.
Eğer soru yemek, yiyecek, içecek, mutfak malzemeleri, beslenme, diyet programları, protein, vitamin veya sağlıklı yaşam ile ilgiliyse mutlaka cevap ver.
Konu doğrudan yemek tarifleri olmasa bile, yemekle veya beslenmeyle dolaylı olarak ilgiliyse cevap vermeye çalış.
Sadece tamamen yemek, beslenme veya mutfak ile ilgisi olmayan soruları reddet.

Kullanıcı sorusu: $userQuestion
""".trimIndent()

                val messagesArray = JSONArray()
                messagesArray.put(JSONObject().put("role", "system").put("content", "Sen sadece yemek konularında konuşan bir asistansın."))
                messagesArray.put(JSONObject().put("role", "user").put("content", prompt))

                val json = JSONObject()
                json.put("model", "gpt-3.5-turbo")
                json.put("messages", messagesArray)
                json.put("max_tokens", 500)

                val requestBody = RequestBody.create(
                    "application/json".toMediaType(),
                    json.toString()
                )

                val request = Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $OPENAI_API_KEY")
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        activity?.runOnUiThread {
                            removeTypingIndicator()
                            addMessage(ChatMessage("Hata: ${e.message}", isUser = false))
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        activity?.runOnUiThread {
                            removeTypingIndicator()
                            if (response.isSuccessful && body != null) {
                                val content = JSONObject(body)
                                    .getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content")
                                addMessage(ChatMessage(content.trim(), isUser = false))
                            } else {
                                addMessage(ChatMessage("Hata: ${response.message}", isUser = false))
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    removeTypingIndicator()
                    addMessage(ChatMessage("Hata: ${e.message}", isUser = false))
                }
            }
        }
    }
}
