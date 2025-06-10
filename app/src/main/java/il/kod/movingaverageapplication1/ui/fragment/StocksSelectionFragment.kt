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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint

import il.kod.movingaverageapplication1.ui.viewmodel.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.ui.viewmodel.CustomServerDatabaseViewModel
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.databinding.FragmentAllStockSelectionBinding
import il.kod.movingaverageapplication1.utils.AutoClearedValue
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import kotlinx.coroutines.Dispatchers


import javax.inject.Inject
import kotlinx.coroutines.launch
import androidx.paging.LoadState
import kotlinx.coroutines.CoroutineScope
import retrofit2.Response

@AndroidEntryPoint
class StocksSelectionFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    interface ResultCallback<T> {
        fun onResult(result: T)
        fun onError(error: Throwable)
    }

    private var _binding: FragmentAllStockSelectionBinding by AutoClearedValue<FragmentAllStockSelectionBinding>(this)
    private val binding get() = _binding




        //shared viewmodels
    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()
    private val  CSDViewModel : CustomServerDatabaseViewModel by activityViewModels()



    private var lastUpdateTime = 0L
    private val updateInterval = 500L
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
            CSDViewModel.getNbOfStocksInRemoteDB()
            Log.d(
                "StockSelectionFragment",
                "there is at start ${viewModelAllStocks.availableStockCount.value}"
            )

            CSDViewModel.getAllStocks()

            CSDViewModel.fetchedStockFlag= true //set flag to true so it wont fetch again
        }



//if a new stock is added to the unselectedlist, it will be added to the recycler view
//TODO(MAKE THOSE 2 AGAIN BUT WITH PAGINATION):
        setupRecyclerView(R.id.action_stockSelection3_to_detailsItemFragment)
        //viewModelAllStocks.unselectedStock.observe(viewLifecycleOwner) {setupRecyclerView(it,  R.id.action_stockSelection3_to_detailsItemFragment)}

        viewModelAllStocks.filteredStocksList.observe(viewLifecycleOwner){setupRecyclerView(R.id.action_stockSelection3_to_detailsItemFragment)}

binding.testbutton.setOnClickListener { }//CSDViewModel.testUserFollowsStock("AAPL", true) }

       CSDViewModel.AI_Answer.observe(viewLifecycleOwner) { answer ->
            answer?.let {
                when(it.status) {
                    is Loading -> {
                        binding.progressBar.isVisible = true

                    }
                    is Error -> {

                        Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT).show()
                        binding.progressBar.isVisible = false
                    }
                    is Success -> {
                        binding.progressBar.isVisible = false

                    }
                }
            }
        }

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

adapter.addLoadStateListener { binding.progressBarpager.isVisible=it.source.refresh is  LoadState.Loading }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        CSDViewModel.allStocks.observe(viewLifecycleOwner) {
        if (it != null) {
            lifecycleScope.launch {

                CSDViewModel.calculatePercentageFetchStocks(++CSDViewModel.fetchedStocksCount, true)
            }

                    if (CSDViewModel.percentoge>5 && CSDViewModel.percentoge < 95) {

                        binding.loadingText.text= "Indexing large amount of data, please wait..."
                        binding.loadingText.textSize= 12f
                        val layoutParams = binding.progressBar.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams.topMargin = 0 // Set the desired top margin
                        layoutParams.width = 900
                        binding.progressBar.layoutParams = layoutParams

                        binding.recyclerView.isVisible = true


                    } else if (CSDViewModel.percentoge >= 95) {
                        binding.progressBar.isVisible = false
                        binding.loadingText.isVisible = false
                        binding.recyclerView.isVisible = true
                    }

                    if (CSDViewModel.percentoge < 95) {

                        binding.progressBar.progress = CSDViewModel.percentoge
                        updateCountView(CSDViewModel.fetchedStocksCount)

                        //TODO(DAO UTILITY: SET DAO IN NEW UTILITY TABLE FLAG SO FETCHING WONT HAPPEN FOR NEXT TIME)_
                        //TODO(OR FIND OUT WITH NB OF STOCKS IN DB BUT DOESNT WORK FOR NOW)

                    } else {
                        binding.progressBar.isVisible = false
                        binding.loadingText.isVisible = false
                        binding.recyclerView.isVisible = true
//                            Toast.makeText(
//                                requireContext(),
//                                "Stocks fetched successfully",
//                                Toast.LENGTH_SHORT
//                            ).show()


                    }
                }


            //RECYCLER
            it?.let { data ->
                adapter.submitData(lifecycle, data)//submit the data to the adapter

            } ?: run {
                Log.e("StocksSelectionFragment", "PagingData or its data is null")
            }
        }}





    fun updateCountView(count: Int) {
   var countText: String= "Available Stocks"
        if (count>0) countText=countText.plus(": $count stocks")
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Available Stocks: $count stocks"
    }
    override fun onResume() {
        super.onResume()
        //updateCountView(CSDViewModel.fetchedStocksCount)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }


}