package com.example.reminderapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reminderapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MessageViewModel
    private lateinit var adapter: MessageAdapter

    private val notifPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op either way, reminders still show in-app */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MessageViewModel::class.java]

        adapter = MessageAdapter(
            onEdit = { message ->
                val intent = Intent(this, AddEditMessageActivity::class.java)
                intent.putExtra("message_id", message.id)
                startActivity(intent)
            },
            onDelete = { message -> viewModel.delete(message) }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.allMessages.observe(this) { messages ->
            adapter.submitList(messages)
            binding.emptyView.visibility = if (messages.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditMessageActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        askNotificationPermissionIfNeeded()

        // Lay down today's random notification times
        Scheduler.rescheduleToday(this)
    }

    private fun askNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
