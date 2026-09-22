package shahzod.projects.ozbektiliningizohliugati.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.androidbroadcast.vbpd.viewBinding
import shahzod.projects.ozbektiliningizohliugati.R
import shahzod.projects.ozbektiliningizohliugati.databinding.FragmentSettingBinding

class SettingFragment : Fragment(R.layout.fragment_setting) {
    private val binding by viewBinding(FragmentSettingBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSozlamalar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cardview1.setOnClickListener {
            requireContext().startActivity(
                Intent(Intent.ACTION_VIEW, "https://t.me/shahzod_codes_bot".toUri())
            )
        }

        binding.cardview2.setOnClickListener {
            findNavController().navigate(R.id.action_Setting_to_starFragment)
        }

        val sharedPreferences = requireContext().getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        binding.switchDarkMode.isChecked = isDarkMode

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.cardview3.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.app_version), Toast.LENGTH_SHORT).show()
        }
    }
}