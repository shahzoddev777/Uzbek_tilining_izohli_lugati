package shahzod.projects.ozbektiliningizohliugati.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import shahzod.projects.ozbektiliningizohliugati.database.entity.AtamaEntity
import shahzod.projects.ozbektiliningizohliugati.database.entity.Entity
import shahzod.projects.ozbektiliningizohliugati.databinding.ItemAtamaBinding

class AtamaAdapter(
    private val list: MutableList<AtamaEntity>,
) : RecyclerView.Adapter<AtamaAdapter.VIewHolder>() {
    class VIewHolder(private val binding: ItemAtamaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(soz: AtamaEntity) {
            binding.tvWord.text = soz.word
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VIewHolder = VIewHolder(
        ItemAtamaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(
        holder: VIewHolder,
        position: Int
    ) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
    fun submitList(ls: List<AtamaEntity>) {
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }
}