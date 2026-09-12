package shahzod.projects.ozbektiliningizohliugati.presentation

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.androidbroadcast.vbpd.viewBinding
import shahzod.projects.ozbektiliningizohliugati.R
import shahzod.projects.ozbektiliningizohliugati.adapter.DictionaryAdapter
import shahzod.projects.ozbektiliningizohliugati.database.AppDatabase
import shahzod.projects.ozbektiliningizohliugati.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val db by lazy { AppDatabase.getInstance(requireContext()) }
    private lateinit var adapter: DictionaryAdapter
    private var searchJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DictionaryAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        loadWords()

        val searchEditText =
            binding.searchBox.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText?.filters = arrayOf(InputFilter.LengthFilter(30))

        adapter.setOnItemClickListener { entity ->
            val bundle = Bundle().apply {
                putString("word", entity.word)
                putString("description", entity.description)
            }
            findNavController().navigate(R.id.action_house_to_dictionaryInfoFragment, bundle)
        }

        binding.searchBox.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText ?: ""
                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(250)
                    val result = withContext(Dispatchers.IO) {
                        if (query.isEmpty()) db.getWordDao().getAllWords()
                        else db.getWordDao().searchWords(query)
                    }
                    adapter.submitList(result, query)
                    binding.layoutEmpty.visibility =
                        if (result.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerView.visibility =
                        if (result.isEmpty()) View.GONE else View.VISIBLE
                }
                return true
            }
        })
    }

    private fun loadWords() {
        viewLifecycleOwner.lifecycleScope.launch {
            val words = withContext(Dispatchers.IO) { db.getWordDao().getAllWords() }
            adapter.submitList(words)
        }
    }
}