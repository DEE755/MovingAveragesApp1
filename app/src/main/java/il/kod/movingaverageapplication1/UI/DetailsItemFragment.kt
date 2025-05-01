package il.kod.movingaverageapplication1.UI

import AllStocksViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.SelectedStocksViewModel
import il.kod.movingaverageapplication1.databinding.DetailsStockLayoutBinding

class DetailsItemFragment: Fragment() {
    var _binding : DetailsStockLayoutBinding?= null
    val binding get()=_binding!!

    //private val viewModel: SelectedStocksViewModel by activityViewModels()

    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
    private val viewModelSelectedStocks : SelectedStocksViewModel by activityViewModels()




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
        arguments?.getInt("item")?.let {
            binding.stockSymbol.text = viewModelAllStocks.stockList.value?.get(it)?.symbol


            binding.stockCompany.text = viewModelAllStocks.stockList.value?.get(it)?.name?:"N/A"
            binding.stockPrice.text = viewModelAllStocks.stockList.value?.get(it)?.price.toString()
            binding.numberOfShares.text = viewModelAllStocks.stockList.value?.get(it)?.movingAverage.toString()
            Glide.with(requireContext()).load(viewModelAllStocks.stockList.value?.get(it)?.imageUri).into(binding.itemImage)


        binding.okayButton.setOnClickListener {
            //findNavController().navigate(R.id.action_selectedStocks_to_stockSelection3)
            findNavController().popBackStack()
        }

            binding.addButton.setOnClickListener {
                arguments?.getInt("item")?.let { index ->
                    val clickedStock = viewModelAllStocks.onItemClicked(index)
                    clickedStock?.let { stock ->
                        viewModelSelectedStocks.addStock(stock)
                        viewModelAllStocks.removeStock(stock)

                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "${clickedStock.name} was Added to selected stocks", Toast.LENGTH_SHORT).show()
                    }
                }



        }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}