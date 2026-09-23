package shahzod.projects.ozbektiliningizohliugati.presentation

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.androidbroadcast.vbpd.viewBinding
import shahzod.projects.ozbektiliningizohliugati.R
import shahzod.projects.ozbektiliningizohliugati.databinding.FragmentDictionaryInfoBinding
import java.util.Locale

class DictionaryInfoFragment : Fragment(R.layout.fragment_dictionary_info), TextToSpeech.OnInitListener {
    private val binding by viewBinding(FragmentDictionaryInfoBinding::bind)
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val word = arguments?.getString("word")
        val description = arguments?.getString("description")

        binding.tvWord.text = word
        binding.tvDescription.text = description

        tts = TextToSpeech(requireContext(), this)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.imgTranscriptionIcon.setOnClickListener {
            if (!isTtsReady) {
                Toast.makeText(requireContext(), "Ovozli xizmat tayyorlanmoqda...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val textToSpeak = binding.tvWord.text.toString().lowercase()
            if (textToSpeak.isNotEmpty()) {
                tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "")
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("uz"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale("tr"))
            }
            tts?.setSpeechRate(0.85f)
            isTtsReady = true
        } else {
            isTtsReady = false
            Toast.makeText(requireContext(), getString(R.string.speech_service_error), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroyView()
    }
}