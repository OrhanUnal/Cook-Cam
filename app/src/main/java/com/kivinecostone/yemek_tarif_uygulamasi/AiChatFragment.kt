package com.kivinecostone.yemek_tarif_uygulamasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.kivinecostone.yemek_tarif_uygulamasi.ChatAdapter
import com.kivinecostone.yemek_tarif_uygulamasi.ChatMessage


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
                addMessage(ChatMessage(text, true))
                userInput.setText("")
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

    private fun getAiResponse(userQuestion: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()

                val prompt = """
                    Sen sadece yemek tarifleri, yemek pişirme teknikleri , kalori , gram , protein , karbonhidrat , yağ sporcu yemekleri ve mutfak ile ilgili sorulara cevap ver.
                    Eğer soru bunlarla ilgili değilse "Üzgünüm, sadece yemek ile ilgili sorulara cevap verebilirim." de.
                    
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
                            addMessage(ChatMessage("Hata: ${e.message}", false))
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        if (response.isSuccessful && body != null) {
                            val content = JSONObject(body)
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")

                            activity?.runOnUiThread {
                                addMessage(ChatMessage(content.trim(), false))
                            }
                        } else {
                            activity?.runOnUiThread {
                                addMessage(ChatMessage("Hata: ${response.message}", false))
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    addMessage(ChatMessage("Hata: ${e.message}", false))
                }
            }
        }
    }
}
