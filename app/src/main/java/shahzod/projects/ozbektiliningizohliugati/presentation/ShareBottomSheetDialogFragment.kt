package shahzod.projects.ozbektiliningizohliugati.presentation

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import shahzod.projects.ozbektiliningizohliugati.R
import shahzod.projects.ozbektiliningizohliugati.databinding.FragmentShareBottomSheetBinding
import java.io.File
import java.io.FileOutputStream

class ShareBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentShareBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var word: String = ""
    private var description: String = ""

    override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            word = it.getString(ARG_WORD, "")
            description = it.getString(ARG_DESCRIPTION, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.includeShareCard.tvShareWord.text = word
        binding.includeShareCard.tvShareDescription.text = description

        binding.cardContainer.alpha = 0f
        binding.cardContainer.scaleX = 0.92f
        binding.cardContainer.scaleY = 0.92f
        binding.cardContainer.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(280)
            .start()

        binding.btnShareImage.setOnClickListener {
            shareCardAsImage()
        }

        binding.btnShareText.setOnClickListener {
            shareAsText()
        }
    }

    private fun shareCardAsImage() {
        val cardView = binding.includeShareCard.shareCardRoot

        val width = if (cardView.width > 0) cardView.width else cardView.measuredWidth
        val height = if (cardView.height > 0) cardView.height else cardView.measuredHeight

        if (width <= 0 || height <= 0) {
            Toast.makeText(requireContext(), "Rasm tayyorlanmoqda, iltimos qayta urining", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        cardView.draw(canvas)

        try {
            val imagesDir = File(requireContext().cacheDir, "images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }
            imagesDir.listFiles()?.forEach { it.delete() }
            val imageFile = File(imagesDir, "share_card_${System.currentTimeMillis()}.png")
            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            val contentUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                imageFile
            )

            val formattedText = getString(R.string.share_formatting_template, word, description)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, formattedText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)))
            dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Rasm yaratishda xatolik yuz berdi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareAsText() {
        val formattedText = getString(R.string.share_formatting_template, word, description)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, formattedText)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)))
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_WORD = "arg_word"
        private const val ARG_DESCRIPTION = "arg_description"

        fun newInstance(word: String, description: String): ShareBottomSheetDialogFragment {
            val fragment = ShareBottomSheetDialogFragment()
            val args = Bundle().apply {
                putString(ARG_WORD, word)
                putString(ARG_DESCRIPTION, description)
            }
            fragment.arguments = args
            return fragment
        }
    }
}