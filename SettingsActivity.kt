package com.example.reminderapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.reminderapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(
                uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Prefs.setGlobalBackground(this, uri.toString())
            binding.imagePreview.setImageURI(uri)
            binding.imagePreview.visibility = android.view.View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pre-fill current values
        binding.sliderCount.value = Prefs.getDailyCount(this).toFloat()
        binding.labelCount.text = getString(R.string.reminders_per_day, Prefs.getDailyCount(this))
        Prefs.getGlobalBackground(this)?.let {
            binding.imagePreview.setImageURI(Uri.parse(it))
            binding.imagePreview.visibility = android.view.View.VISIBLE
        }

        binding.sliderCount.addOnChangeListener { _, value, _ ->
            binding.labelCount.text = getString(R.string.reminders_per_day, value.toInt())
        }

        binding.btnPickBackground.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSaveSettings.setOnClickListener {
            val count = binding.sliderCount.value.toInt()
            Prefs.setDailyCount(this, count)
            // Keep a simple fixed active window (9am-10pm); could expose start/end pickers later
            Prefs.setWindow(this, 9, 22)
            Scheduler.rescheduleToday(this)
            finish()
        }
    }
}
