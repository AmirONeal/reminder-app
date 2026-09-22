package com.example.reminderapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reminderapp.databinding.ItemMessageBinding

class MessageAdapter(
    private val onEdit: (Message) -> Unit,
    private val onDelete: (Message) -> Unit
) : ListAdapter<Message, MessageAdapter.MessageViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.textMessage.text = message.text

            if (message.isFarsi()) {
                // Farsi/Persian script: RTL layout + Farsi-friendly font (Vazirmatn)
                binding.textMessage.textDirection = View.TEXT_DIRECTION_RTL
                binding.textMessage.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                binding.textMessage.typeface = FontProvider.farsiFont(binding.root.context)
            } else {
                // English/Latin: LTR layout + default English font
                binding.textMessage.textDirection = View.TEXT_DIRECTION_LTR
                binding.textMessage.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                binding.textMessage.typeface = FontProvider.englishFont(binding.root.context)
            }

            binding.root.setOnClickListener { onEdit(message) }
            binding.btnDelete.setOnClickListener { onDelete(message) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(old: Message, new: Message) = old.id == new.id
            override fun areContentsTheSame(old: Message, new: Message) = old == new
        }
    }
}
