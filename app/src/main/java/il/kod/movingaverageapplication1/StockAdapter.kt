package il.kod.movingaverageapplication1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import il.kod.movingaverageapplication1.databinding.ItemLayoutBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import il.kod.movingaverageapplication1.StockAdapter.ItemViewHolder



class StockAdapter(private val stocks: List<Stock>, private val callBack: ItemListener) : RecyclerView.Adapter<ItemViewHolder>() {

    interface ItemListener {
        fun onItemClicked(index: Int)
        fun onItemLongClicked(index: Int)
    }

    inner class ItemViewHolder(private val binding: ItemLayoutBinding) :RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener
    {
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





        fun bind(stock : Stock){
            binding.itemTitle.text= stock.name
            binding.itemDescription.text = "Marketcap: ${stock.symbol} - Price: ${stock.price} -${stock.peRatio}"
            Glide.with(binding.root)
                .load(stock.imageUri)
                .error(R.mipmap.ic_launcher)
                .into(binding.itemImage)
        }



    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    )=ItemViewHolder(ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))



    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
       holder.bind(stocks[position])
//this really does something
        /*holder.itemView.setOnClickListener {
            val clickedStock = stocks[position]


            Toast.makeText(holder.itemView.context, "Successfully added: ${clickedStock.name}", Toast.LENGTH_SHORT).show()
            SelectedStocks.selectedStList.add(clickedStock)

        }*/

    }

    override fun getItemCount() = stocks.size






    fun getCount(): Int = stocks.size
    fun getItem(position: Int): Any = stocks[position]
    override fun getItemId(position: Int): Long = position.toLong()





/*
    fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)
        val stock = getItem(position) as Stock

        val text1: TextView = view.findViewById(android.R.id.text1)
        val text2: TextView = view.findViewById(android.R.id.text2)

        text1.text = stock.name
        text2.text = "Price: \$${stock.price}, PE Ratio: ${stock.peRatio}"

        return view
    }*/



}