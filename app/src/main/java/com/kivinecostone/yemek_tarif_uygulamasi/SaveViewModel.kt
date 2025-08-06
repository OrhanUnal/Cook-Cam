package com.kivinecostone.yemek_tarif_uygulamasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.kivinecostone.yemek_tarif_uygulamasi.database.ChatLogEntity

class SaveViewModel : ViewModel() {
    val NoteDao = SavedFragment.noteData.dao()
    val NoteList : LiveData<List<ChatLogEntity>> = NoteDao.getAllNotes()

    fun addNote(title: String){
        viewModelScope.launch(Dispatchers.IO) {
            NoteDao.addNote(ChatLogEntity(title = title))
        }
    }

    fun deleteNote(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            NoteDao.deleteNote(id)
        }
    }
}