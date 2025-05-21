package com.example.finalproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.finalproject.databinding.FragmentStatsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.BufferedReader
import java.io.InputStreamReader

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    // ZenQuotes API data model + service
    interface ZenQuoteApi {
        @GET("today")
        suspend fun getQuote(): List<ZenQuoteResponse>
    }

    data class ZenQuoteResponse(
        val q: String,  // quote
        val a: String   // author
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load and count mood stats from moods.txt
        try {
            val file = requireContext().openFileInput("moods.txt")
            val reader = BufferedReader(InputStreamReader(file))
            val lines = reader.readLines()

            val happyCount = lines.count { it.contains("Mood: Happy") }
            val sadCount = lines.count { it.contains("Mood: Sad") }
            val angryCount = lines.count { it.contains("Mood: Angry") }

            binding.happyCount.text = "😊 Happy: $happyCount"
            binding.sadCount.text = "😔 Sad: $sadCount"
            binding.angryCount.text = "😤 Angry: $angryCount"

        } catch (e: Exception) {
            Log.e("StatsFragment", "Error reading mood file", e)
        }

        // Load quote of the day from ZenQuotes API
        val retrofit = Retrofit.Builder()
            .baseUrl("https://zenquotes.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ZenQuoteApi::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = api.getQuote().firstOrNull()
                result?.let {
                    binding.quoteText.text = "“${it.q}”\n— ${it.a}"
                } ?: run {
                    binding.quoteText.text = "No quote found today 😅"
                }
            } catch (e: Exception) {
                Log.e("StatsFragment", "Failed to fetch quote", e)
                binding.quoteText.text = "Error loading quote 🥲"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
