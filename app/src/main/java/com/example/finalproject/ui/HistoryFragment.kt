package com.example.finalproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.databinding.FragmentHistoryBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val moodEntries = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadMoodEntries()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = MoodEntryAdapter(moodEntries)
    }

    private fun loadMoodEntries() {
        moodEntries.clear()
        try {
            val file = requireContext().openFileInput("moods.txt")
            val reader = BufferedReader(InputStreamReader(file))
            var line: String?
            var entry = ""

            while (reader.readLine().also { line = it } != null) {
                if (line == "---") {
                    moodEntries.add(entry.trim())
                    entry = ""
                } else {
                    entry += "$line\n"
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
