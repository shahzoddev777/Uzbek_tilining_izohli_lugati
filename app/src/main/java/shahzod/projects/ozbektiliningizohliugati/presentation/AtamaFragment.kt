package shahzod.projects.ozbektiliningizohliugati.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import shahzod.projects.ozbektiliningizohliugati.R
import shahzod.projects.ozbektiliningizohliugati.adapter.AtamaAdapter
import shahzod.projects.ozbektiliningizohliugati.database.AppDatabase
import shahzod.projects.ozbektiliningizohliugati.database.entity.AtamaEntity
import shahzod.projects.ozbektiliningizohliugati.databinding.FragmentAtamaBinding

class AtamaFragment : Fragment(R.layout.fragment_atama) {
    private val binding by viewBinding(FragmentAtamaBinding::bind)
    private val db by lazy { AppDatabase.getInstance(requireContext()) }
    private lateinit var adapter: AtamaAdapter
    private var allWords = listOf<AtamaEntity>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AtamaAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            allWords = withContext(Dispatchers.IO) {
                db.getAtamaDao().getAllAtamaWords()
            }
            adapter.submitList(allWords)
        }

        binding.searchBox.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterWords(newText)
                return true
            }
        })
    }

    private fun filterWords(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            allWords
        } else {
            allWords.filter {
                it.word.contains(query, ignoreCase = true)
            }
        }
        adapter.submitList(filteredList)
    }
}