package il.kod.movingaverageapplication1.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.FollowSet
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.databinding.ItemCheckLayoutBinding
import il.kod.movingaverageapplication1.databinding.ItemLayoutBinding

class FollowSetAdapterFragment(
    private var followSets: List<FollowSet>,
    private val callBack: ItemListener
) : RecyclerView.Adapter<FollowSetAdapterFragment.FollowSetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowSetViewHolder {
        val binding = ItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FollowSetViewHolder(binding)
    }

    interface ItemListener {
        fun onItemClicked(index: Int)
        fun onItemLongClicked(index: Int)
    }

    inner class FollowSetViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {
            callBack.onItemClicked(adapterPosition)
        }

        override fun onLongClick(p0: View?): Boolean {
            callBack.onItemLongClicked(adapterPosition)
            return true
        }

        fun bind(followSet: FollowSet) {
            binding.itemTitle.text = followSet.name
            Glide.with(binding.root)
                .load(followSet.imageUri)
                .error(R.mipmap.ic_launcher)
                .into(binding.itemImage)
        }
    }



    override fun onBindViewHolder(
        holder: FollowSetAdapterFragment.FollowSetViewHolder,
        position: Int
    ) {
        holder.bind(followSets[position])
    }



    override fun getItemCount(): Int = followSets.size

    fun updateData(newFollowSets: List<FollowSet>) {
        followSets = newFollowSets
        notifyDataSetChanged()
    }
}