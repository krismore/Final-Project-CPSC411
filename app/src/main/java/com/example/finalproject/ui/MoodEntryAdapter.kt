package com.example.finalproject.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.ItemMoodBinding

class MoodEntryAdapter(private val entries: List<String>) :
    RecyclerView.Adapter<MoodEntryAdapter.MoodViewHolder>() {

    inner class MoodViewHolder(private val binding: ItemMoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String) {
            binding.moodText.text = text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount(): Int = entries.size
}
