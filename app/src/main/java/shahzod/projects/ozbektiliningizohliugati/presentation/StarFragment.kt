package shahzod.projects.ozbektiliningizohliugati.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.androidbroadcast.vbpd.viewBinding
import shahzod.projects.ozbektiliningizohliugati.R
import shahzod.projects.ozbektiliningizohliugati.databinding.FragmentStarBinding

class StarFragment : Fragment(R.layout.fragment_star) {
    private val binding by viewBinding(FragmentStarBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvLater.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRate.setOnClickListener {
            val rating = binding.ratingBar.rating
            if (rating > 0) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.thank_you_rating),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_stars),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}