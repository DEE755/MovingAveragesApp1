package il.kod.movingaverageapplication1.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.databinding.ItemCheckLayoutBinding

class MultipleStockAdapterFragment(
    private var stocks: List<Stock>,
    private val callBack: ItemListener
) : RecyclerView.Adapter<MultipleStockAdapterFragment.ItemViewHolder>() {

    interface ItemListener {
        fun onItemClicked(index: Int)
        fun onItemLongClicked(index: Int)
    }

    inner class ItemViewHolder(private val binding: ItemCheckLayoutBinding) :
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

        fun bind(stock: Stock) {
            binding.itemTitle.text = stock.name
            Glide.with(binding.root)
                .load(stock.logo_url)
                .error(R.mipmap.ic_launcher)
                .into(binding.itemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCheckLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(stocks[position])
    }

    override fun getItemCount(): Int = stocks.size

    fun updateData(newStocks: List<Stock>) {
        stocks = newStocks
        notifyDataSetChanged()
    }
}