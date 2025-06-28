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
import androidx.core.content.edit
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
import il.kod.movingaverageapplication1.ui.viewmodel.SyncManagementViewModel
import il.kod.movingaverageapplication1.utils.lockOrientation
import il.kod.movingaverageapplication1.utils.showConfirmationDialog
import il.kod.movingaverageapplication1.utils.unlockOrientation
import kod.il.movingaverageapplication1.utils.sessionManager
import kotlinx.coroutines.Dispatchers

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
    private val syncManagementviewModel: SyncManagementViewModel by activityViewModels()



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
           lifecycleScope.launch(Dispatchers.IO) {
               Log.d(
                   "StockSelectionFragment",
                   "there is at start ${viewModelAllStocks.availableStockCount}"
               )
           }

            //CSDViewModel.getAllStocks()

            CSDViewModel.fetchedStockFlag = true //set flag to true so it wont fetch again
        }

        setupPagingRecyclerView()



        binding.returntoselected.setOnClickListener {

            if( !(sessionManager.allStocksPackHaveBeenFetch() && sessionManager.userFollowedStocksHaveBeenRetrievedOrNone()))
                Toast.makeText(requireContext(), "Please wait for completion before leaving", Toast.LENGTH_SHORT).show()

            else{
                findNavController().navigate(R.id.action_stockSelection3_to_selectedStocks)
            }


        }


        binding.searchView.apply { //TODO(fix this when searching + click on stock --> crash)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(searchText: String?): Boolean {
                    binding.progressBarnoview.isVisible=false
                    if (searchText.isNullOrEmpty()) {
                        binding.recyclerView.isVisible=true
                        binding.searchingRecyclerView.isVisible=false

                        viewModelAllStocks.filteredStocksList.value = stockList
                        updateCountView(stockList.size)

                    }
                    else {
                            binding.recyclerView.isVisible=false
                            binding.searchingRecyclerView.isVisible=true}

                    viewModelAllStocks.filterStocksByName(searchText)
                    updateCountView(viewModelAllStocks.searchStockCount)


                    return true
                }
            })
        }


        return binding.root
    }


    fun setupPagingRecyclerView() {
        val pagingAdapter = StockPagingAdapterFragment(
            callBack = object : StockPagingAdapterFragment.StockClickListener {
                override fun onStockClicked(stock: Stock) {
                    viewModelDetailStock.setStock(stock)
                    findNavController().navigate(R.id.action_stockSelection3_to_detailsItemFragment)
                }

                override fun onStockLongClicked(stock: Stock) {}
                override fun onItemSwiped(stock: Stock) {
                    TODO("Not yet implemented")
                }
            },
            glide = glide
        )

        pagingAdapter.addLoadStateListener { loadState ->
            val binding = _binding ?: return@addLoadStateListener // Ensure binding is valid

            // Show progress bar during loading
            binding.progressBarPager.isVisible = loadState.source.refresh is LoadState.Loading
            binding.progressBarPager.progress = CSDViewModel.percentoge
            binding.loadingText.isVisible = loadState.source.refresh is LoadState.Loading

            requireActivity().lockOrientation()

            // Handle empty state
            val isEmpty =
                loadState.source.refresh is LoadState.NotLoading && pagingAdapter.itemCount == 0
            binding.loadingText.isVisible = isEmpty
            binding.recyclerView.isVisible = !isEmpty

            // Handle error state (optional)
            val errorState = loadState.source.refresh as? LoadState.Error
            errorState?.let {
                Toast.makeText(requireContext(), "Error: ${it.error.message}", Toast.LENGTH_SHORT)
                    .show()
            }

            CSDViewModel.fetchedStocksCount = pagingAdapter.itemCount
            updateCountView(CSDViewModel.fetchedStocksCount)

        }

        //PAGING RECYCLER VIEW
        binding.recyclerView.adapter = pagingAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        //SEARCHING RECYCLER VIEW (For efficiency purpose we use a different recycler view)
        binding.searchingRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        val searchingAdapter=
            StockRecyclerAdapterFragment(
            emptyList(),
            callBack = object : StockRecyclerAdapterFragment.SearchedStockClickListener {
                override fun onSearchedStockClicked(index: Int) {
                    viewModelAllStocks.filteredStocksList.value?.get(index)
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

                override fun onSearchedStockLongClicked(index: Int) { //TODO(CHANGE THE WAY OF DELETION)
                    val clickedStock = viewModelAllStocks.followedStocks.value?.get(index)

                    val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(index)
                    viewHolder?.itemView?.findViewById<View>(R.id.stock_card_view)/*?.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.red))*/


                    //TODO(put in viewlmodel)
                    showConfirmationDialog(
                        context = requireContext(),
                        title = getString(R.string.deletion_stock_title),
                        message = getString(R.string.delete_stock_message, clickedStock?.name),
                        onYes = {

                            viewModelAllStocks.setUserFollowsStockData(clickedStock!!, false)
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



        CSDViewModel.getAllStocks()
        CSDViewModel.newallStocks.observe(viewLifecycleOwner) {


            if (it != null) {


                if (sessionManager.allStocksPackHaveBeenFetch() && !sessionManager.userFollowedStocksHaveBeenRetrievedOrNone())
                {
                    syncManagementviewModel.pullAndInjectFollowedStocksFromRemote()
                    sessionManager.preferences.edit { putBoolean("user_followed_stocks_retrieved", true) }
                }

                lifecycleScope.launch {

                    pagingAdapter.submitData(lifecycle, it)//submit the data to the adapter
                    CSDViewModel.fetchedStocksCount = pagingAdapter.itemCount
                    CSDViewModel.calculatePercentageFetchStocks(
                        CSDViewModel.fetchedStocksCount,
                        true
                    )

                    Log.d("StocksSelectionFragment", "adapter item count: ${pagingAdapter.itemCount}, percentage: ${CSDViewModel.percentoge}")
                }

                    if (CSDViewModel.percentoge==0) {//DOWNLOAD DIDNT STARTED YET
                        binding.loadingText.isVisible=true
                        //binding.searchView.isVisible=false
                        binding.progressBarPager.isVisible=false
                        binding.progressBarPager.progress=CSDViewModel.percentoge
                        binding.progressBarnoview.isVisible=true
                    }
                if(CSDViewModel.percentoge==1){//DOWNLOAD STARTED
                    binding.progressBarnoview.isVisible=false
                    binding.progressBarPager.isVisible=true
                }

                    if (CSDViewModel.percentoge>95) {//DOWNLOAD FINISHING
                        binding.loadingText.isVisible = false
                        //binding.searchView.isVisible=true

                        if(CSDViewModel.percentoge==98) {//DOWNLOAD FINISHED WITH 2% MARGIN OF ERROR
                            Toast.makeText(
                                requireContext(),
                                "All stocks fetched successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            requireActivity().unlockOrientation()
                        }
                    }




            }

            updateCountView(CSDViewModel.fetchedStocksCount)

        }






        viewModelAllStocks.filteredStocksList.observe(viewLifecycleOwner) {

                lifecycleScope.launch {
                    searchingAdapter.updateData(it)//submit the data to the adapter
                    val filteredCount= searchingAdapter.itemCount

                    Log.d("StocksSelectionFragment", "adapter item count: ${filteredCount}")
                    updateCountView(filteredCount)
                }

        }





        fun updateCountView(count: Int) {
            if (!isAdded) return // Ensure the Fragment is attached to an activity
            var countText = "Available Stocks"
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
