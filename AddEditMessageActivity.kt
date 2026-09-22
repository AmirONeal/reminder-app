package com.example.reminderapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.reminderapp.databinding.ActivityAddEditMessageBinding
import kotlinx.coroutines.launch

class AddEditMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditMessageBinding
    private lateinit var viewModel: MessageViewModel
    private var editingMessage: Message? = null
    private var pickedBackgroundUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // persist read permission so the notification can load it later too
            contentResolver.takePersistableUriPermission(
                uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            pickedBackgroundUri = uri
            binding.imagePreview.setImageURI(uri)
            binding.imagePreview.visibility = android.view.View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MessageViewModel::class.java]

        val messageId = intent.getLongExtra("message_id", -1L)
        if (messageId != -1L) {
            lifecycleScope.launch {
                val dao = AppDatabase.getInstance(applicationContext).messageDao()
                val existing = dao.getAllSync().find { it.id == messageId }
                existing?.let { populateForEdit(it) }
            }
        }

        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener { save() }
    }

    private fun populateForEdit(message: Message) {
        editingMessage = message
        binding.editMessageText.setText(message.text)
        message.backgroundUri?.let {
            pickedBackgroundUri = Uri.parse(it)
            binding.imagePreview.setImageURI(pickedBackgroundUri)
            binding.imagePreview.visibility = android.view.View.VISIBLE
        }
        binding.btnSave.text = getString(R.string.update_message)
    }

    private fun save() {
        val text = binding.editMessageText.text.toString().trim()
        if (text.isEmpty()) {
            binding.editMessageText.error = getString(R.string.message_cannot_be_empty)
            return
        }

        val bgUriString = pickedBackgroundUri?.toString()
        val existing = editingMessage

        if (existing != null) {
            viewModel.update(existing.copy(text = text, backgroundUri = bgUriString))
        } else {
            viewModel.insert(text, bgUriString)
        }
        finish()
    }
}
