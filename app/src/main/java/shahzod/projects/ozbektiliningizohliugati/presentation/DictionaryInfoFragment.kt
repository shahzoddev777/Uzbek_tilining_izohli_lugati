package shahzod.projects.ozbektiliningizohliugati.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.androidbroadcast.vbpd.viewBinding
import shahzod.projects.ozbektiliningizohliugati.R
import shahzod.projects.ozbektiliningizohliugati.databinding.FragmentDictionaryInfoBinding

class DictionaryInfoFragment: Fragment(R.layout.fragment_dictionary_info) {
    private val binding by viewBinding(FragmentDictionaryInfoBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val word = arguments?.getString("word")
        val description = arguments?.getString("description")

        binding.tvWord.text = word
        binding.tvDescription.text = description

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}