package il.kod.movingaverageapplication1.ui

import AllStocksViewModel
import android.os.Bundle
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
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.DetailStockViewModel
import javax.inject.Inject


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
                clickedStock.let {
            if (!clickedStock.value!!.isSelected){
                binding.addButton.visibility = View.VISIBLE
            } else {
                binding.addButton.visibility = View.GONE
            }
            binding.stockSymbol.text =clickedStock.value?.symbol


            binding.stockCompany.text =clickedStock.value?.name?:"N/A"
            binding.stockPrice.text = clickedStock.value?.current_price.toString()
            //binding.numberOfShares.text = clickedStock.value?.movingAverage.toString()
            //Glide.with(requireContext()).load(clickedStock.value?.imageUri?.toUri()).into(binding.itemImage)
                    glide.load(clickedStock.value?.logo_url?.toUri()).into(binding.itemImage)

            binding.addButton.setOnClickListener {

                        viewModelAllStocks.followStock(clickedStock.value!!)

                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "${clickedStock.value?.name} was Added to selected stocks", Toast.LENGTH_SHORT).show()

                }

            binding.okayButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}