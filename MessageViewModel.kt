package com.example.reminderapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MessageViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).messageDao()
    val allMessages: LiveData<List<Message>> = dao.getAll()

    fun insert(text: String, backgroundUri: String?) = viewModelScope.launch {
        dao.insert(Message(text = text, backgroundUri = backgroundUri))
    }

    fun update(message: Message) = viewModelScope.launch {
        dao.update(message)
    }

    fun delete(message: Message) = viewModelScope.launch {
        dao.delete(message)
    }
}
