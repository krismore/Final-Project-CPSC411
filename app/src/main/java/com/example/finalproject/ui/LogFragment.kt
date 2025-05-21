package com.example.finalproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentLogBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LogFragment : Fragment() {

    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val moodStore = MoodDataStore(requireContext())

        // Observe ViewModel state
        viewModel.selectedMood.observe(viewLifecycleOwner) { mood ->
            Log.d("MoodSnap", "observed mood: $mood")
        }

        viewModel.reflectionText.observe(viewLifecycleOwner) { text ->
            binding.reflectionInput.setText(text)
        }

        // Restore saved mood from DataStore
        lifecycleScope.launch {
            moodStore.lastMoodFlow.collect { savedMood ->
                savedMood?.let {
                    viewModel.setMood(it)
                    Log.d("MoodSnap", "restored mood from datastore: $it")
                }
            }
        }

        // Mood button clicks
        binding.happyImage.setOnClickListener {
            viewModel.setMood("Happy")
            lifecycleScope.launch { moodStore.saveMood("Happy") }
            Toast.makeText(requireContext(), getString(R.string.toast_mood_happy), Toast.LENGTH_SHORT).show()
        }

        binding.sadImage.setOnClickListener {
            viewModel.setMood("Sad")
            lifecycleScope.launch { moodStore.saveMood("Sad") }
            Toast.makeText(requireContext(), getString(R.string.toast_mood_sad), Toast.LENGTH_SHORT).show()
        }

        binding.angryImage.setOnClickListener {
            viewModel.setMood("Angry")
            lifecycleScope.launch { moodStore.saveMood("Angry") }
            Toast.makeText(requireContext(), getString(R.string.toast_mood_angry), Toast.LENGTH_SHORT).show()
        }

        // Save reflection
        binding.saveReflectionButton.setOnClickListener {
            val reflectionText = binding.reflectionInput.text.toString()
            val mood = viewModel.selectedMood.value

            viewModel.setReflection(reflectionText)

            if (reflectionText.isNotBlank() && mood != null) {
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                val entry = "Mood: $mood\nReflection: $reflectionText\nTimestamp: $timestamp\n---\n"

                try {
                    requireContext().openFileOutput("moods.txt", android.content.Context.MODE_APPEND).use {
                        it.write(entry.toByteArray())
                    }
                    Toast.makeText(requireContext(), getString(R.string.toast_reflection_saved), Toast.LENGTH_SHORT).show()
                    Log.d("MoodSnap", "saved entry:\n$entry")

                    // 🔁 REST POST mood + reflection
                    lifecycleScope.launch {
                        try {
                            val response = QuoteApiService.postApi.postMood(
                                MoodPostBody(mood, reflectionText)
                            )
                            Log.i("MoodSnap", "POST success: ${response.id} at ${response.createdAt}")
                        } catch (e: Exception) {
                            Log.e("MoodSnap", "POST failed", e)
                        }
                    }

                    // Reset UI
                    binding.reflectionInput.setText("")
                    viewModel.setMood("")
                } catch (e: Exception) {
                    Log.e("MoodSnap", "error saving to file", e)
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.toast_write_something), Toast.LENGTH_SHORT).show()
            }
        }

        binding.viewHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.historyFragment)
        }

        binding.viewStatsButton?.setOnClickListener {
            findNavController().navigate(R.id.statsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
