package shahzod.projects.ozbektiliningizohliugati.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
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
        binding.recyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        adapter.submitList(db.getWordDao().getAllWords())

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
                val searchResult = if (newText.isNullOrEmpty()) {
                    db.getWordDao().getAllWords()
                } else {
                    db.getWordDao().searchWords(newText)
                }
                adapter.submitList(searchResult)
                return true
            }
        })
    }
}