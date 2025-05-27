package il.kod.movingaverageapplication1.ui

import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint

import il.kod.movingaverageapplication1.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.ui.viewmodel.CustomServerDatabaseViewModel
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.databinding.FragmentAllStockSelectionBinding
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import javax.inject.Inject


@AndroidEntryPoint
class StocksSelectionFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private var _binding: FragmentAllStockSelectionBinding? = null
    private val binding get() = _binding!!




        //shared viewmodels
    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()
    private val  CSDViewModel : CustomServerDatabaseViewModel by activityViewModels()



    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAllStockSelectionBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())




        CSDViewModel.getAllStocks()
        CSDViewModel.allStocks.observe(viewLifecycleOwner) {
            Log.d("StockSelectionFragment", "observe called")
            when(it.status){
                is Loading->{binding.progressBar.isVisible=true
                    binding.loadingText.isVisible=true
                    Log.d("StockSelectionFragment", "Loading state")}

                is Success->{Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()

                    Log.d("StockSelectionFragment", "Success state")

                    binding.progressBar.isVisible=false
                    binding.loadingText.isVisible=false
                }


                is Error -> {Toast.makeText(requireContext(), "ERROR, TRY AGAIN", Toast.LENGTH_SHORT).show()
                    binding.progressBar.isVisible=false
                    binding.loadingText.isVisible=false
                    Log.d("StockSelectionFragment", "Error state")}}
        }




//if a new stock is added to the unselectedlist, it will be added to the recycler view

        viewModelAllStocks.unselectedStock.observe(viewLifecycleOwner) {setupRecyclerView(it,  R.id.action_stockSelection3_to_detailsItemFragment)}

        viewModelAllStocks.filteredStocksList.observe(viewLifecycleOwner){setupRecyclerView(it,  R.id.action_stockSelection3_to_detailsItemFragment)}


binding.returntoselected.setOnClickListener {
    findNavController().navigate(R.id.action_stockSelection3_to_selectedStocks)


}
        binding.searchView.apply {
            setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModelAllStocks.filterStocksByName(newText)
                    return true
                }
            })
        }


        return binding.root
    }




    fun setupRecyclerView(listOfStocks: List<Stock>?, actionId: Int) {
        binding.recyclerView.adapter = StockRecyclerAdapterFragment(
            listOfStocks?: emptyList(),
            callBack = object : StockRecyclerAdapterFragment.ItemListener {

                override fun onItemClicked(index: Int) {
                    val clickedStock =  listOfStocks?.get(index)
                    clickedStock?.let {
                        viewModelDetailStock.setStock(it)
                        findNavController().navigate(
                           actionId
                        )
                    }
                }
                override fun onItemLongClicked(index: Int) {}
            },
            glide = glide
        )

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