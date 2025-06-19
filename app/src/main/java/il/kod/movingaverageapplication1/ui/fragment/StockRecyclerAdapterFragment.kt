package il.kod.movingaverageapplication1.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingData
import il.kod.movingaverageapplication1.databinding.StockLayoutBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.ui.fragment.StockRecyclerAdapterFragment.ItemViewHolder


class StockRecyclerAdapterFragment(private var stocks: List<Stock>, private val callBack: ItemListener, private val glide: RequestManager) : RecyclerView.Adapter<ItemViewHolder>()
{


    interface ItemListener {
        fun onItemClicked(index: Int)
        fun onItemLongClicked(index: Int)
    }

    inner class ItemViewHolder(private val binding: StockLayoutBinding) :
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

            binding.stockTitle.text = stock.name
            binding.stockTicker.text = stock.symbol
            if (stock.current_price==0.00 || stock.ma_25.isNaN() || stock.ma_25==0.00) {
                binding.stockPrice.visibility = View.GONE


            } else {
                binding.stockPrice.text= stock.current_price.toString()
                binding.stockPrice.visibility = View.VISIBLE

            }



            Log.d("StockAdapterFragment", "name: ${stock.name},image: ${stock.logo_url}")
            glide.load(stock.logo_url).error(R.mipmap.ic_launcher).into(binding.stockImage)


        }

    }





    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int
    ) = ItemViewHolder(
        StockLayoutBinding.inflate(
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
