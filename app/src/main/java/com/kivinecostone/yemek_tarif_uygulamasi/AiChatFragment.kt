package com.kivinecostone.yemek_tarif_uygulamasi

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kivinecostone.yemek_tarif_uygulamasi.Adapter.ChatAdapter
import com.kivinecostone.yemek_tarif_uygulamasi.database.ChatLogEntity
import com.kivinecostone.yemek_tarif_uygulamasi.database.NoteData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AiChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userInput: EditText
    private lateinit var sendButton: Button
    private lateinit var btnVoice: ImageButton
    private val messages = mutableListOf<ChatMessage>()
    private var notes = listOf<ChatLogEntity>()
    private lateinit var adapter: ChatAdapter

    private val OPENAI_API_KEY: String =
        com.kivinecostone.yemek_tarif_uygulamasi.BuildConfig.API_KEY

    private val SPEECH_REQUEST_CODE = 100

    private val noteDB: NoteData by lazy {
        Room.databaseBuilder(
            requireContext(),
            NoteData::class.java,
            "note_database"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    private lateinit var noteEntity: ChatLogEntity
    private lateinit var dateBar: ChatLogEntity
    var currentDate: String = ""

    private val MAX_CONTEXT_ITEMS = 3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ai_chat, container, false)
        var i = 0
        noteDB.dao().getAllNotes().observe(viewLifecycleOwner) { list ->
            notes = list
            while (notes.size > i) {
                if (notes[i].isUser == 2) {
                    currentDate = notes[i].date
                }
                i++
            }
        }
        recyclerView = view.findViewById(R.id.recyclerViewChat)
        userInput = view.findViewById(R.id.userInput)
        sendButton = view.findViewById(R.id.sendButton)
        btnVoice = view.findViewById(R.id.btnVoice)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        sendButton.setOnClickListener {
            val text = userInput.text.toString().trim()
            if (text.isNotEmpty()) {
                val nowTime = currentTime()
                val nowDate = currentDate()
                addMessage(ChatMessage(text, isUser = true, time = nowTime, date = nowDate))
                userInput.setText("")
                showTypingIndicator()
                getAiResponse(text)
            }
        }

        btnVoice.setOnClickListener { startSpeechToText() }

        return view
    }

    private fun currentTime(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    private fun currentDate(): String =
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Konuşabilirsiniz...")
        }
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            userInput.setText("Cihazınızda sesli giriş desteklenmiyor.")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = result?.get(0) ?: ""
            userInput.setText(spokenText)
        }
    }

    private fun addMessage(msg: ChatMessage) {
        messages.add(msg)
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun showTypingIndicator() {
        messages.add(
            ChatMessage(
                message = "",
                isUser = false,
                isTyping = true,
                time = currentTime(),
                date = currentDate()
            )
        )
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

    private fun typeWriterEffect(fullText: String, index: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val builder = StringBuilder()
            for (char in fullText) {
                builder.append(char)
                messages[index].message = builder.toString()
                adapter.notifyItemChanged(index)
                delay(5)
            }
        }
    }

    private fun buildMessagesForApi(): JSONArray {
        val arr = JSONArray()
        arr.put(
            JSONObject().put("role", "system").put(
                "content",
                "Sen yemek tarifleri, yemek pişirme teknikleri, mutfak kültürü, gıda, beslenme, diyet ve sağlıklı yaşam konularında uzman bir asistansın.\n" +
                        "Eğer kullanıcı mesajı yemek, yiyecek, içecek, mutfak malzemeleri, beslenme, diyet, protein, vitamin veya sağlıklı yaşam ile ilgiliyse net bir şekilde cevap ver.\n" +
                        "Eğer konu tamamen yemekle ilgisizse kibarca \"Bu konuda yardımcı olamıyorum.\" de.\n"
            )
        )
        val history =
            messages.filter { !it.isTyping && it.message.isNotBlank() }.takeLast(MAX_CONTEXT_ITEMS)
        for (m in history) {
            val role = if (m.isUser) "user" else "assistant"
            arr.put(JSONObject().put("role", role).put("content", m.message))
        }
        return arr
    }

    private fun getAiResponse(userQuestion: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()

                if (currentDate != currentDate()) {
                    dateBar =
                        ChatLogEntity(0, currentDate(), 2, currentTime(), currentDate(), null)
                    noteDB.dao().addNote(dateBar)
                    currentDate = currentDate()
                }
                noteEntity = ChatLogEntity(
                    0,
                    title = userQuestion,
                    0,
                    currentTime(),
                    currentDate(),
                    null
                )
                noteDB.dao().addNote(noteEntity)

                val messagesArray = buildMessagesForApi()

                val json = JSONObject().apply {
                    put("model", "gpt-3.5-turbo")
                    put("messages", messagesArray)
                    put("max_tokens", 500)
                }

                val requestBody =
                    json.toString().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $OPENAI_API_KEY")
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        activity?.runOnUiThread {
                            removeTypingIndicator()
                            addMessage(
                                ChatMessage(
                                    "Hata: ${e.message}",
                                    isUser = false,
                                    time = currentTime(),
                                    date = currentDate()
                                )
                            )
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        activity?.runOnUiThread {
                            removeTypingIndicator()
                            if (response.isSuccessful && body != null) {
                                try {
                                    val content = JSONObject(body)
                                        .getJSONArray("choices")
                                        .getJSONObject(0)
                                        .getJSONObject("message")
                                        .getString("content")
                                        .trim()

                                    messages.add(
                                        ChatMessage(
                                            "",
                                            isUser = false,
                                            time = currentTime(),
                                            date = currentDate()
                                        )
                                    )
                                    val botIndex = messages.size - 1
                                    adapter.notifyItemInserted(botIndex)
                                    typeWriterEffect(content, botIndex)

                                    noteEntity = ChatLogEntity(
                                        0,
                                        title = content,
                                        1,
                                        currentTime(),
                                        currentDate(),
                                        null
                                    )
                                    noteDB.dao().addNote(noteEntity)
                                } catch (e: Exception) {
                                    addMessage(
                                        ChatMessage(
                                            "Yanıt çözümlenemedi: ${e.message}",
                                            isUser = false,
                                            time = currentTime(),
                                            date = currentDate()
                                        )
                                    )
                                }
                            } else {
                                addMessage(
                                    ChatMessage(
                                        "Hata: ${response.message}",
                                        isUser = false,
                                        time = currentTime(),
                                        date = currentDate()
                                    )
                                )
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    removeTypingIndicator()
                    addMessage(
                        ChatMessage(
                            "Hata: ${e.message}",
                            isUser = false,
                            time = currentTime(),
                            date = currentDate()
                        )
                    )
                }
            }
        }
    }
}
