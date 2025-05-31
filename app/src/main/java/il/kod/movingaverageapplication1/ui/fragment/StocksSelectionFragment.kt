package il.kod.movingaverageapplication1.ui.fragment

import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint

import il.kod.movingaverageapplication1.ui.viewmodel.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.ui.viewmodel.CustomServerDatabaseViewModel
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.databinding.FragmentAllStockSelectionBinding
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Resource
import il.kod.movingaverageapplication1.utils.Success
import kotlinx.coroutines.Dispatchers


import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class StocksSelectionFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private var _binding: FragmentAllStockSelectionBinding? = null //by AutoClearedValue<StocksSelectionFragment>
    private val binding get() = _binding!!




        //shared viewmodels
    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()
    private val  CSDViewModel : CustomServerDatabaseViewModel by activityViewModels()

   // lateinit var stockObserver: Observer<Int>



    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAllStockSelectionBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        if (!CSDViewModel.fetchedStockFlag) { //if stocks are not fetched for current launch, fetch them (if needed)
            Toast.makeText(requireContext(), "Fetching stocks from server", Toast.LENGTH_LONG)
                .show()
            viewModelAllStocks.getAvailableStockCount()//local stock count
            Log.d(
                "StockSelectionFragment",
                "there is at start ${viewModelAllStocks.availableStockCount.value}"
            )



            CSDViewModel.getNbOfStocksInRemoteDB()

            CSDViewModel.getAllStocks()


            CSDViewModel.fetchedStockFlag = true

            }



       // stockObserver = Observer { resource ->



       // }

// Attach the observer
       // viewModelAllStocks.availableStockCount.observe(viewLifecycleOwner, stockObserver)







           /* viewModelAllStocks.availableStockCount.observe(viewLifecycleOwner)
            {
                CSDViewModel.calculatePercentageFetchStocks(stockFetchCount = it)

                if (CSDViewModel.percentage.value!! < 95) {

                    binding.progressBar.progress = CSDViewModel.percentage.value ?: 0
                    Log.d(
                        "StockSelectionFragment",
                        "Fetching stocks from server, please wait..., percentage: $it"
                    )

                } else {
                    binding.progressBar.isVisible = false
                    binding.loadingText.isVisible = false
                    binding.recyclerView.isVisible = true
                    Toast.makeText(
                        requireContext(),
                        "Stocks fetched successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("StockSelectionFragment", "Stocks fetched successfully")
                }
            }
*/




//if a new stock is added to the unselectedlist, it will be added to the recycler view
//TODO(MAKE THOSE 2 AGAIN BUT WITH PAGINATION):
        setupRecyclerView(R.id.action_stockSelection3_to_detailsItemFragment)
        //viewModelAllStocks.unselectedStock.observe(viewLifecycleOwner) {setupRecyclerView(it,  R.id.action_stockSelection3_to_detailsItemFragment)}

        //viewModelAllStocks.filteredStocksList.observe(viewLifecycleOwner){setupRecyclerView(it,  R.id.action_stockSelection3_to_detailsItemFragment)}


binding.returntoselected.setOnClickListener {
    findNavController().navigate(R.id.action_stockSelection3_to_selectedStocks)


}
        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModelAllStocks.filterStocksByName(newText)
                    updateCountView(viewModelAllStocks.searchStockCount)


                    return true
                }
            })
        }


        return binding.root
    }


    fun setupRecyclerView(actionId: Int) {
        val adapter = StockPagingAdapterFragment(
            callBack = object : StockPagingAdapterFragment.ItemListener {
                override fun onItemClicked(stock: Stock) {
                    viewModelDetailStock.setStock(stock)
                    findNavController().navigate(actionId)
                }

                override fun onItemLongClicked(stock: Stock) {}
            },
            glide = glide
        )

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        CSDViewModel.allStocks.observe(viewLifecycleOwner) { pagingData ->
            pagingData?.status?.data?.let { data ->
                adapter.submitData(lifecycle, data)
            } ?: run {
                Log.e("StocksSelectionFragment", "PagingData or its data is null")
            }
        }
        }





    fun updateCountView(count: Int) {
   var countText: String= "Available Stocks"
        if (count>0) countText=countText.plus(": $count stocks")

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Available Stocks: $count stocks"
    }
    override fun onResume() {
        super.onResume()
        updateCountView(viewModelAllStocks.availableStockCount.value ?: 0)
        viewModelAllStocks.getAvailableStockCount()
        viewModelAllStocks.availableStockCount.observe(viewLifecycleOwner) {
            updateCountView(it)


//            // Update the progress bar based on the available stock count
            CSDViewModel.calculatePercentageFetchStocks(it)
            val percentage = CSDViewModel.percentage.value ?: 0
            // and for the whole fetch
            //CSDViewModel.calculatePercentageFetchStocks(CSDViewModel.nbOfStocksFetched)//default chunck size is 1
            Log.d(
                "StockSelectionFragment",
                "percentage: ${percentage}"
            )

            if (percentage>10 && percentage < 95) {
                binding.progressBar.isVisible = true
                binding.loadingText.isVisible = true
                binding.recyclerView.isVisible = false
            }
            else if (percentage == 100) {
                binding.progressBar.isVisible = false
                binding.loadingText.isVisible = false
                binding.recyclerView.isVisible = true
            }
            if (percentage < 95) {

                binding.progressBar.progress = percentage
                Log.d(
                    "StockSelectionFragment",
                    "Fetching stocks from server, please wait..., percentage: $percentage"
                )

                // Stop observing when done

                Log.d(
                    "StockSelectionFragment",
                    "Success state, stocks fetched: ${viewModelAllStocks.availableStockCount}"
                )
            }
            else {
                binding.progressBar.isVisible = false
                binding.loadingText.isVisible = false
                binding.recyclerView.isVisible = true
                Toast.makeText(
                    requireContext(),
                    "Stocks fetched successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }


    }}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}