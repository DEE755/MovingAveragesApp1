package il.kod.movingaverageapplication1.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.databinding.StockLayoutBinding

class StockPagingAdapterFragment(
    private val callBack: StockClickListener,
    private val glide: RequestManager
) : PagingDataAdapter<Stock, StockPagingAdapterFragment.ItemViewHolder1>(DIFF_CALLBACK) {

    interface StockClickListener {
        fun onStockClicked(stock: Stock)
        fun onStockLongClicked(stock: Stock)
        fun onItemSwiped(stock: Stock)
    }

    inner class ItemViewHolder1(private val binding: StockLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        private var currentStock: Stock? = null

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        fun bind(stock: Stock) {
            currentStock = stock
            binding.stockTitle.text = stock.name
            binding.stockTicker.text = stock.symbol
            binding.stockPrice.isVisible=false

            glide.load(stock.logo_url)
                .error(R.mipmap.empty_stock_logo)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
                .into(binding.stockImage)
        }

        override fun onClick(p0: View?) {
            currentStock?.let { callBack.onStockClicked(it) }
        }

        override fun onLongClick(p0: View?): Boolean {
            currentStock?.let { callBack.onStockLongClicked(it) }
            return true
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder1 {
        val binding = StockLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder1(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder1, position: Int) {
        val stock = getItem(position)
        if (stock != null) {
            holder.bind(stock)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Stock>() {
            override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem.symbol == newItem.symbol
            }

            override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem == newItem
            }
        }
    }
}
