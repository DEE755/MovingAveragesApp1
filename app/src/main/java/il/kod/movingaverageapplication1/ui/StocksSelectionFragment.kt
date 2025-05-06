package il.kod.movingaverageapplication1.ui

import AllStocksViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import il.kod.movingaverageapplication1.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.databinding.FragmentAllStockSelectionBinding

class StocksSelectionFragment : Fragment() {

    private var _binding: FragmentAllStockSelectionBinding? = null
    private val binding get() = _binding!!



        //shared viewmodels
    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAllStockSelectionBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())



//if a new stock is added to the unselectedlist, it will be added to the recycler view

        viewModelAllStocks.unselectedStock.observe(viewLifecycleOwner)
        {

            binding.recyclerView.adapter = StockAdapterFragment(
                viewModelAllStocks.unselectedStock?.value ?: emptyList(),
                callBack = object : StockAdapterFragment.ItemListener {

                    override fun onItemClicked(index: Int) {
                        val clickedStock = viewModelAllStocks.unselectedStock?.value?.get(index)
                        clickedStock?.let {
                            viewModelDetailStock.setStock(clickedStock)
                            findNavController().navigate(
                                R.id.action_stockSelection3_to_detailsItemFragment)

                        }
                    }

                    override fun onItemLongClicked(index: Int) {

                    }


                })

        }





binding.returntoselected.setOnClickListener {
    findNavController().navigate(R.id.action_stockSelection3_to_selectedStocks)


}
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.available_stocks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}