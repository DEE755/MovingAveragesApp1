package il.kod.movingaverageapplication1.ui

import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.databinding.DetailsStockLayoutBinding
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class DetailsStockFragment: Fragment() {


    @Inject
    lateinit var glide: RequestManager

    var _binding : DetailsStockLayoutBinding?= null
    val binding get()=_binding!!

    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailsStockLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        val clickedStock : MutableLiveData<Stock> = viewModelDetailStock.clickedStock
        clickedStock.let {stock->
            if (!stock.value!!.isSelected){
                binding.addButton.visibility = View.VISIBLE
            } else {
                binding.addButton.visibility = View.GONE
            }
            binding.stockSymbol.text ="Ticker: ${stock.value?.symbol}"

            binding.stockCompany.text =stock.value?.name?:"N/A"
            binding.stockPrice.text = if (stock.value?.current_price != null) {
                "Current prices: ${stock.value?.current_price}"
            } else {
                "Follow to get live updates"
            }
            //binding.numberOfShares.text = clickedStock.value?.movingAverage.toString()
            //Glide.with(requireContext()).load(clickedStock.value?.imageUri?.toUri()).into(binding.itemImage)
            glide.load(stock.value?.logo_url?.toUri()).into(binding.itemImage)


            binding.addButton.setOnClickListener {

                Log.d("DetailsStockFragment", "stoclk clicked: ${stock.value}")
                viewModelAllStocks.followStock(stock.value!!, true)

                findNavController().popBackStack()
                Toast.makeText(requireContext(), "${stock.value?.name} was Added to selected stocks", Toast.LENGTH_SHORT).show()

            }
            binding.askAi.setOnClickListener {
                findNavController().navigate(R.id.action_detailsItemFragment_to_askAIFragment)
            }

            binding.returnButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}