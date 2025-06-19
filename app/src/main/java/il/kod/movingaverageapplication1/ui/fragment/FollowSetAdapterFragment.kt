package il.kod.movingaverageapplication1.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.databinding.StockLayoutBinding

class FollowSetAdapterFragment(
    private var followSets: List<FollowSet>,
    private val callBack: ItemListener
) : RecyclerView.Adapter<FollowSetAdapterFragment.FollowSetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowSetViewHolder {
        val binding = StockLayoutBinding.inflate(
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

    inner class FollowSetViewHolder(private val binding: StockLayoutBinding) :
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
            binding.stockTitle.text = followSet.name
            binding.stockTicker.text = binding.root.context.getString(R.string.follow_set_description, followSet.size())
            Glide.with(binding.root)
                .load(followSet.imageUri)
                .error(R.mipmap.button_follow_set)
                .into(binding.stockImage)
        }
    }


    override fun onBindViewHolder(
        holder: FollowSetViewHolder,
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