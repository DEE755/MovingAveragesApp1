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
import androidx.core.content.ContextCompat
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


import javax.inject.Inject
import kotlinx.coroutines.launch
import androidx.paging.LoadState
import il.kod.movingaverageapplication1.data.objectclass.Stock.Companion.stockList
import il.kod.movingaverageapplication1.utils.showConfirmationDialog

@AndroidEntryPoint
class StocksSelectionFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    interface ResultCallback<T> {
        fun onResult(result: T)
        fun onError(error: Throwable)
    }

    private var _binding: FragmentAllStockSelectionBinding by AutoClearedValue<FragmentAllStockSelectionBinding>(
        this
    )
    private val binding get() = _binding


    //shared viewmodels
    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()
    private val CSDViewModel: CustomServerDatabaseViewModel by activityViewModels()



     //lateinit var currentObserver: Observer<List<Stock>>


    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAllStockSelectionBinding.inflate(inflater, container, false)




        if (!CSDViewModel.fetchedStockFlag) { //if stocks are not fetched for current launch, fetch them (if needed)
            Toast.makeText(requireContext(), "Fetching stocks from server", Toast.LENGTH_LONG)
                .show()
            viewModelAllStocks.getAvailableStockCount()//local stock count
            CSDViewModel.getNbOfStocksInRemoteDB()
            Log.d(
                "StockSelectionFragment",
                "there is at start ${viewModelAllStocks.availableStockCount.value}"
            )

            //CSDViewModel.getAllStocks()

            CSDViewModel.fetchedStockFlag = true //set flag to true so it wont fetch again
        }


//if a new stock is added to the unselectedlist, it will be added to the recycler view
//TODO(MAKE THOSE 2 AGAIN BUT WITH PAGINATION):
        setupRecyclerView(R.id.action_stockSelection3_to_detailsItemFragment)
        //viewModelAllStocks.unselectedStock.observe(viewLifecycleOwner) {setupRecyclerView(it,  R.id.action_stockSelection3_to_detailsItemFragment)}



        binding.returntoselected.setOnClickListener {


            findNavController().navigate(R.id.action_stockSelection3_to_selectedStocks)




        }


        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        binding.recyclerView.isVisible=true
                        binding.searchingRecyclerView.isVisible=false
                        viewModelAllStocks.filteredStocksList.value = stockList
                        updateCountView(stockList.size)

                    }
                    else {
                            binding.recyclerView.isVisible=false
                            binding.searchingRecyclerView.isVisible=true}


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

        adapter.addLoadStateListener { loadState ->
            val binding = _binding ?: return@addLoadStateListener // Ensure binding is valid

            // Show progress bar during loading
            binding.progressBarpager.isVisible = loadState.source.refresh is LoadState.Loading
            binding.loadingText.isVisible = loadState.source.refresh is LoadState.Loading

            // Handle empty state
            val isEmpty = loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
            binding.loadingText.isVisible = isEmpty
            binding.recyclerView.isVisible = !isEmpty

            // Handle error state (optional)
            val errorState = loadState.source.refresh as? LoadState.Error
            errorState?.let {
                Toast.makeText(requireContext(), "Error: ${it.error.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        //PAGING RECYCLER VIEW
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        //SEARCHING RECYCLER VIEW
        binding.searchingRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        val searchingAdapter=
            StockRecyclerAdapterFragment(
            emptyList(),
            callBack = object : StockRecyclerAdapterFragment.ItemListener {
                override fun onItemClicked(index: Int) {

                    viewModelAllStocks.followedStocks.value?.get(index)
                        ?.let { selectedStock ->
                            viewModelDetailStock.clickedStock.value = selectedStock
                        ///TODO(FIX THIS ITS NOT DOING ANYTHING -->CHANGE THE COLOR)
                            val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(index)
                            viewHolder?.itemView?.findViewById<View>(R.id.stock_card_view)?.setBackgroundColor(
                                ContextCompat.getColor(requireContext(), R.color.green)
                            )
                            findNavController().navigate(R.id.action_selectedStocks_to_detailsItemFragment)
                        }
                }

                override fun onItemLongClicked(index: Int) {
                    val clickedStock = viewModelAllStocks.followedStocks.value?.get(index)


                    val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(index)
                    viewHolder?.itemView?.findViewById<View>(R.id.stock_card_view)?.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )


                    showConfirmationDialog(
                        context = requireContext(),
                        title = getString(R.string.deletion_stock_title),
                        message = getString(R.string.delete_stock_message, clickedStock?.name),
                        onYes = {
                            //TODO(FIX THIS ITS NOT DOING ANYTHING -->CHANGE THE COLOR)

                            viewModelAllStocks.followStock(clickedStock!!, false)
                            (binding.recyclerView.adapter as? StockRecyclerAdapterFragment)?.notifyItemRemoved(
                                index
                            )

                            Toast.makeText(
                                requireContext(),
                                getString(R.string.successfully_removed, clickedStock.name),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onNo = {}
                    )
                }
            },
            glide = glide
        )

        binding.searchingRecyclerView.adapter =searchingAdapter



        CSDViewModel.allStocks.observe(viewLifecycleOwner) {
            if (it != null) {
                Log.d("StocksSelectionFragment", "Observing")
                lifecycleScope.launch {
                    adapter.submitData(lifecycle, it)//submit the data to the adapter
                    CSDViewModel.fetchedStocksCount = adapter.itemCount
                    CSDViewModel.calculatePercentageFetchStocks(
                        CSDViewModel.fetchedStocksCount,
                        true
                    )
                    Log.d("StocksSelectionFragment", "adapter item count: ${adapter.itemCount}")
                }



                if (CSDViewModel.percentoge > 5 && CSDViewModel.percentoge < 95) {

                    binding.loadingText.text = "Indexing large amount of data, please wait..."
                    binding.loadingText.textSize = 12f
                    val layoutParams =
                        binding.progressBar.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.topMargin = 0 // Set the desired top margin
                    layoutParams.width = 900
                    binding.progressBar.layoutParams = layoutParams

                    binding.recyclerView.isVisible = true


                } else if (CSDViewModel.percentoge >= 95 || CSDViewModel.percentoge == 0) {
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

                }
            }


        }






        viewModelAllStocks.filteredStocksList.observe(viewLifecycleOwner) {

                lifecycleScope.launch {
                    searchingAdapter.updateData(it)//submit the data to the adapter
                    val filteredCount= searchingAdapter.itemCount

                    Log.d("StocksSelectionFragment", "adapter item count: ${filteredCount}")
                    updateCountView(filteredCount)
                }

        }
    }




        fun updateCountView(count: Int) {
            if (!isAdded) return // Ensure the Fragment is attached to an activity
            var countText: String = "Available Stocks"
            if (count > 0) countText = countText.plus(": $count stocks")
            (requireActivity() as AppCompatActivity).supportActionBar?.title =
                "Available Stocks: $count stocks"
        }

        override fun onResume() {
            super.onResume()
            //updateCountView(CSDViewModel.fetchedStocksCount)
        }

        override fun onDestroyView() {
            super.onDestroyView()

        }


    }
