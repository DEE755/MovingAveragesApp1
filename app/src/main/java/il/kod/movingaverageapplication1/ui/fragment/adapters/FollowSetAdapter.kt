package il.kod.movingaverageapplication1.ui.fragment.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.databinding.StockLayoutBinding
import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.FollowSetViewModel
import il.kod.movingaverageapplication1.utils.createCombinedImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FollowSetAdapterFragment(
    private var followSets: List<FollowSet>,
    private val callBack: ItemListener,
    private val viewModelAllStock: AllStocksViewModel,
    private val viewModelFollowSet: FollowSetViewModel
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
            binding.pictureProgressBar.isVisible= true
        }

        override fun onClick(p0: View?) {
            callBack.onItemClicked(bindingAdapterPosition)
        }

        override fun onLongClick(p0: View?): Boolean {
            callBack.onItemLongClicked(bindingAdapterPosition)
            return true
        }

        fun bind(followSet: FollowSet) {

            if (followSet.notificationsPriceThreeshold > 0.0) {
               binding.stockPrice.text= "Alert set under ${followSet.notificationsPriceThreeshold}$"
            }
            else {
                binding.stockPrice.text = "Not Alert Set"
                binding.stockPrice.setTextColor(binding.root.context.getColor(R.color.black))
            }


            binding.stockTitle.text = followSet.name
            binding.stockTicker.text = binding.root.context.getString(R.string.follow_set_description, followSet.size())

            CoroutineScope(Dispatchers.Main).launch {


                val listOfStocks =
                    viewModelAllStock.getStocksByIds(*(followSet.set_ids).toIntArray())


                listOfStocks.let {
                    if (it.size == 1) {
                        Glide.with(binding.root)
                            .load(it[0].logo_url)
                            .error(R.mipmap.button_follow_set)
                            .apply(RequestOptions().transform(RoundedCorners(30)))
                            .into(binding.stockImage)

                             binding.pictureProgressBar.isVisible= false
                    } else {
                        var limit=4
                        var listOfUri: MutableList<String> = mutableListOf()
                        for(stock in it){
                            listOfUri.add(
                                stock.logo_url ?: R.mipmap.button_follow_set.toString()
                            )
                            --limit
                            if (limit == 0) break
                        }

                        val combinedBitmap=createCombinedImage(binding.stockImage, listOfUri, binding)

                        if (combinedBitmap != null) {
                            followSet.combinedBitmap = combinedBitmap
                            //viewModelFollowSet.followSetsToUpdate.add(followSet)

                            binding.stockImage.setImageBitmap(combinedBitmap)
                        } else {
                            binding.stockImage.setImageResource(R.mipmap.button_follow_set)
                        }
                    }
                }
            }

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