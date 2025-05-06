package il.kod.movingaverageapplication1.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import il.kod.movingaverageapplication1.databinding.ItemLayoutBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.FollowSet
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.ui.StockAdapterFragment.ItemViewHolder



class StockAdapterFragment(private var stocks: List<Stock>, private val callBack: ItemListener) : RecyclerView.Adapter<ItemViewHolder>() {

    interface ItemListener {
        fun onItemClicked(index: Int)
        fun onItemLongClicked(index: Int)
    }

    inner class ItemViewHolder(private val binding: ItemLayoutBinding) :
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
            return false
        }


        fun bind(stock: Stock) {

            binding.itemTitle.text = stock.name
            binding.itemDescription.text = binding.root.context.getString(R.string.stock_details, stock.symbol, stock.price, stock.peRatio)
            Glide.with(binding.root)
                .load(stock.imageUri)
                .error(R.mipmap.ic_launcher)
                .into(binding.itemImage)
        }
    }


    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int
    ) = ItemViewHolder(
        ItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )


    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        holder.bind(stocks[position])

    }


    override fun getItemCount() = stocks.size


    fun updateData(newStocks: List<Stock>) {
        stocks = newStocks
        notifyDataSetChanged()
    }
    
}
