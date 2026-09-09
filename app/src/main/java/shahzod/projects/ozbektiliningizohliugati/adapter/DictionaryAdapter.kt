package shahzod.projects.ozbektiliningizohliugati.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import shahzod.projects.ozbektiliningizohliugati.database.entity.Entity
import shahzod.projects.ozbektiliningizohliugati.databinding.ItemDictionaryBinding

class DictionaryAdapter(
    private val list: MutableList<Entity>,
) : RecyclerView.Adapter<DictionaryAdapter.ViewHolder>() {

    private var onItemClickListener: ((Entity) -> Unit)? = null

    fun setOnItemClickListener(listener: (Entity) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemDictionaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Entity) {
            binding.tvWord.text = data.word
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

    fun submitList(ls: List<Entity>) {
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }
}