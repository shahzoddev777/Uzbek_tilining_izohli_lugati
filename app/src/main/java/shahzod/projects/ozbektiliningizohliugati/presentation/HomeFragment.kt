package shahzod.projects.ozbektiliningizohliugati.presentation

import android.os.Bundle
import android.text.InputFilter
import android.view.View
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DictionaryAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        
        val allWords = db.getWordDao().getAllWords()
        adapter.submitList(allWords)

        // Set max length for SearchView (e.g., 30 characters)
        val searchEditText = binding.searchBox.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText?.filters = arrayOf(InputFilter.LengthFilter(30))

        adapter.setOnItemClickListener { entity ->
            val bundle = Bundle().apply {
                putString("word", entity.word)
                putString("description", entity.description)
            }
            findNavController().navigate(R.id.action_house_to_dictionaryInfoFragment, bundle)
        }

        binding.searchBox.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText ?: ""
                val searchResult = if (query.isEmpty()) {
                    db.getWordDao().getAllWords()
                } else {
                    db.getWordDao().searchWords(query)
                }
                
                adapter.submitList(searchResult, query)
                
                if (searchResult.isEmpty()) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.layoutEmpty.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
                return true
            }
        })
    }
}