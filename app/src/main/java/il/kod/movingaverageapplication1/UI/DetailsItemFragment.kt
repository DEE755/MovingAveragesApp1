package il.kod.movingaverageapplication1.UI

import AllStocksViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import il.kod.movingaverageapplication1.data.SelectedStocksViewModel
import il.kod.movingaverageapplication1.data.Stock
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

        arguments?.getBoolean("displayaddstockbutton", true)?.let {
            if (it) {
                binding.addButton.visibility = View.VISIBLE
            } else {
                binding.addButton.visibility = View.GONE
            }
        }
        arguments?. getParcelable<Stock>("stock")?.let {

            val clickedStock = it
            binding.stockSymbol.text =clickedStock.symbol

            //Log.d("DetailsItemFragment", "Stock symbol: ${reference_list.getOrNull(it)}")
            binding.stockCompany.text =clickedStock.name?:"N/A"
            binding.stockPrice.text = clickedStock.price.toString()
            binding.numberOfShares.text = clickedStock.movingAverage.toString()
            Glide.with(requireContext()).load(clickedStock.imageUri).into(binding.itemImage)


            binding.addButton.setOnClickListener {

                        viewModelSelectedStocks.addStock(clickedStock)
                        viewModelAllStocks.removeStock(clickedStock)

                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "${clickedStock.name} was Added to selected stocks", Toast.LENGTH_SHORT).show()

                }

            binding.okayButton.setOnClickListener {
                //findNavController().navigate(R.id.action_selectedStocks_to_stockSelection3)
                findNavController().popBackStack()
            }



        }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}