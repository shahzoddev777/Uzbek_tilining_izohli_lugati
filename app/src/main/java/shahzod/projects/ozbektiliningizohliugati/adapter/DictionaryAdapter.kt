package shahzod.projects.ozbektiliningizohliugati.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import shahzod.projects.ozbektiliningizohliugati.database.entity.Entity
import shahzod.projects.ozbektiliningizohliugati.databinding.ItemDictionaryBinding

class DictionaryAdapter(
    private val list: MutableList<Entity>,
) : RecyclerView.Adapter<DictionaryAdapter.ViewHolder>() {

    private var onItemClickListener: ((Entity) -> Unit)? = null
    private var query: String = ""

    fun setOnItemClickListener(listener: (Entity) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemDictionaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Entity) {
            val word = data.word ?: ""
            if (query.isNotEmpty() && word.contains(query, ignoreCase = true)) {
                val spannable = SpannableString(word)
                val start = word.indexOf(query, ignoreCase = true)
                val end = start + query.length
                // Highlight color - using a blue shade that matches the app
                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor("#0D47A1")),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.tvWord.text = spannable
            } else {
                binding.tvWord.text = word
            }

            binding.root.setOnClickListener {
                onItemClickListener?.invoke(data)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        ItemDictionaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun submitList(ls: List<Entity>, query: String = "") {
        this.query = query
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }
}