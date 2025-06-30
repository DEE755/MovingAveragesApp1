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

@AndroidEntryPoint
class StocksSelectionFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private var _binding: FragmentAllStockSelectionBinding by AutoClearedValue<FragmentAllStockSelectionBinding>(
        this
    )
    private val binding get() = _binding


    //shared viewmodels
    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()
    private val CSDViewModel: CustomServerDatabaseViewModel by activityViewModels()
    private val syncManagementviewModel: SyncManagementViewModel by activityViewModels()


    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAllStockSelectionBinding.inflate(inflater, container, false)



        //FETCH STOCKS DATA STATUS
            viewModelAllStocks.getAvailableStockCount()//local stock count
            CSDViewModel.getNbOfStocksInRemoteDB()

        Log.d("StockSelectionFragment", "there is at start ${viewModelAllStocks.availableStockCountLocal}")

        /////////////////////////////////////////////




        //SETUP BOTH RECYCLERS AND GET REFERENCES TO THEIR ADAPTERS TO AVOID CASTING AND LONG REFERENCING VIA BINDING
        val pagingAdapter=setupPagingRecyclerView()
        val searchingAdapter=setupSearchingRecyclerView()
        ///////////////////////



        //SET RECYCLERS VIEWS
        updateRecyclerVisibilityAccordingly()
        Log.d("StockSelectionFragment", "setting up recycler paging view to true")

        //SETUP RETURN BUTTON
        binding.returntoselected.setOnClickListener {
            if (!(sessionManager.allStocksPackHaveBeenFetch() && sessionManager.userFollowedStocksHaveBeenRetrievedOrNone()))
                Toast.makeText(
                    requireContext(),
                    "Please wait for completion before leaving",
                    Toast.LENGTH_SHORT
                ).show()
            else {
                findNavController().navigate(R.id.action_stockSelection3_to_selectedStocks)
            }

        }


        //SETUP STOCKS SEARCH VIEW
        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(searchText: String?): Boolean {
                    // Update the query in the ViewModel to save status even when leaving fragment and know status even in new onCreateView
                    viewModelAllStocks.isOnQuerySearch = searchText.isNullOrEmpty().not()

                    binding.progressBarNoview.isVisible = false
                    if (searchText.isNullOrEmpty()) {
                        binding.pagingRecyclerView.isVisible = true
                        binding.searchingRecyclerView.isVisible = false
                        viewModelAllStocks.isOnQuerySearch=false

                        viewModelAllStocks.filteredStocksList.value = stockList
                        updateCountView(stockList.size)

                    } else {
                        binding.pagingRecyclerView.isVisible = false
                        binding.searchingRecyclerView.isVisible = true
                    }

                    viewModelAllStocks.filterStocksByName(searchText)
                    updateCountView(viewModelAllStocks.searchStockCount)


                    return true
                }
            })
        }



//OBSERVING ALL AVAILABLE STOCKS FOR PAGING RECYCLER VIEW
    CSDViewModel.getAllStocks()
    CSDViewModel.allStocks.observe(viewLifecycleOwner)
    { updateRecyclerVisibilityAccordingly()

        if (it != null) {
            if (sessionManager.allStocksPackHaveBeenFetch() && !sessionManager.userFollowedStocksHaveBeenRetrievedOrNone()) {
                syncManagementviewModel.pullAndInjectFollowedStocksFromRemote()
                sessionManager.preferences.edit {
                    putBoolean(
                        "user_followed_stocks_retrieved",
                        true
                    )
                }
            }


            lifecycleScope.launch {
                binding.pagingRecyclerView.adapter=pagingAdapter//submit the data to the adapter
                pagingAdapter.submitData(lifecycle, it)//submit the data to the adapter
                CSDViewModel.fetchedStocksCount = pagingAdapter.itemCount
                CSDViewModel.calculatePercentageFetchStocks(
                    CSDViewModel.fetchedStocksCount,
                    true
                )

                Log.d(
                    "StocksSelectionFragment",
                    "adapter item count: ${pagingAdapter.itemCount}, percentage: ${CSDViewModel.percentage}"
                )
            }

        }

        updateCountView(CSDViewModel.fetchedStocksCount)

    }



        //OBSERVING ALL FOLLOWED STOCKS FOR SEARCH RECYCLER VIEW
    viewModelAllStocks.filteredStocksList.observe(viewLifecycleOwner)
    {
        updateRecyclerVisibilityAccordingly()
       binding.searchingRecyclerView.isVisible=it.isEmpty().not()
        binding.pagingRecyclerView.isVisible = it.isEmpty()

        lifecycleScope.launch {
            searchingAdapter.updateData(it)//submit the data to the adapter
            val filteredCount = searchingAdapter.itemCount

            Log.d("StocksSelectionFragment", "adapter item count: ${filteredCount}")
            updateCountView(filteredCount)
            if (filteredCount == 0)
            {
                binding.pagingRecyclerView.isVisible= false
                binding.searchingRecyclerView.isVisible=false
            }
            else {
                binding.loadingText.isVisible = false
                updateRecyclerVisibilityAccordingly()
            }
        }

    }
        return binding.root

}


    fun setupPagingRecyclerView(): StockPagingAdapterFragment {
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
            val binding = _binding

            // Show progress bar during loading
            binding.progressBarPager.progress = CSDViewModel.percentage

            when(loadState.source.refresh){
                is LoadState.NotLoading ->{
                    updateRecyclerVisibilityAccordingly()
                    if (pagingAdapter.itemCount==0 )//DOWNLOAD NOT STARTED YET
                    {
                        binding.loadingText.isVisible = true
                        binding.pagingRecyclerView.isVisible = false
                        binding.searchingRecyclerView.isVisible= false
                        binding.progressBarNoview.isVisible = true

                    }
                    else //DOWNLOAD FINISHED-->ALSO WHEN NEW VIEW CREATED AND NOT FIRST TIME
                    {
                        CSDViewModel.fetchedStocksCount= pagingAdapter.itemCount
                        updateCountView(CSDViewModel.fetchedStocksCount)
                        binding.progressBarPager.isVisible = false
                        binding.loadingText.isVisible = false
                        sessionManager.setUserFollowedStocksHaveBeenRetrievedOrNone(true)

                    }
                }

                is LoadState.Error ->
                    {
                        binding.progressBarNoview.isVisible = false
                        binding.loadingText.isVisible= true
                        binding.loadingText.text = "Error loading stocks"
                    val errorState = loadState.source.refresh as? LoadState.Error
                    errorState?.let {
                        Toast.makeText(requireContext(), "Error: ${it.error.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    }

                LoadState.Loading -> {
                    updateRecyclerVisibilityAccordingly()
                    binding.progressBarNoview.isVisible = false
                    binding.loadingText.isVisible= true
                    binding.loadingText.text = "Loading stocks...Progress: ${CSDViewModel.percentage}%"
                    binding.progressBarPager.isVisible=true
                    if (CSDViewModel.percentage == 1) {requireActivity().lockOrientation() }//DOWNLOAD STARTED

                    if (CSDViewModel.percentage == 98) {//DOWNLOAD IS ABOUT TO FINISH
                    Toast.makeText(
                        requireContext(),
                        "All stocks fetched successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                        updateRecyclerVisibilityAccordingly()
                    requireActivity().unlockOrientation()

                }

                }
                }
            }


            CSDViewModel.fetchedStocksCount = pagingAdapter.itemCount
            updateCountView(CSDViewModel.fetchedStocksCount)


        //PAGING RECYCLER VIEW
        binding.pagingRecyclerView.adapter = pagingAdapter
        binding.pagingRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        return pagingAdapter
    }

    fun setupSearchingRecyclerView(): StockRecyclerAdapterFragment {
        //SEARCHING RECYCLER VIEW (For efficiency purpose we use a different recycler view)
        binding.searchingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val searchingAdapter =
            StockRecyclerAdapterFragment(
                emptyList(),
                callBack = object : StockRecyclerAdapterFragment.SearchedStockClickListener {
                    override fun onSearchedStockClicked(index: Int) {
                        viewModelAllStocks.filteredStocksList.value?.get(index)
                            ?.let { selectedStock ->
                                viewModelDetailStock.clickedStock.value = selectedStock


                                    findNavController().navigate(R.id.action_stockSelection3_to_detailsItemFragment)

                            }
                    }

                    override fun onSearchedStockLongClicked(index: Int) { //TODO(CHANGE THE WAY OF DELETION)
                        val clickedStock = viewModelAllStocks.followedStocks.value?.get(index)

                        val viewHolder =
                            binding.pagingRecyclerView.findViewHolderForAdapterPosition(index)
                        viewHolder?.itemView?.findViewById<View>(R.id.stock_card_view)



                        //TODO(put in viewlmodel)
                        showConfirmationDialog(
                            context = requireContext(),
                            title = getString(R.string.deletion_stock_title),
                            message = getString(R.string.delete_stock_message, clickedStock?.name),
                            onYes = {

                                viewModelAllStocks.setUserFollowsStockData(clickedStock!!, false)
                                (binding.pagingRecyclerView.adapter as? StockRecyclerAdapterFragment)?.notifyItemRemoved(
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
                glide = glide,
                false
            )

        binding.searchingRecyclerView.adapter = searchingAdapter

        return searchingAdapter

    }

        fun updateCountView(count: Int) {
            if (!this.isAdded) return // Ensure the Fragment is attached to an activity
            var countText: String
            if (count == 0) {
                countText = "No stocks available"
            } else if (count == 1) {
                countText = "One Stock available"
            }
           else {countText = "$count Stocks Available"}

            (requireActivity() as AppCompatActivity).supportActionBar?.title =countText
        }

        override fun onResume() {
            super.onResume()

            resetSearchView()
        }

        override fun onDestroyView() {
            super.onDestroyView()

        }
    fun resetSearchView() {
        binding.searchView.setQuery("", true)
        viewModelAllStocks.isOnQuerySearch = false
        updateRecyclerVisibilityAccordingly()
    }

    fun updateRecyclerVisibilityAccordingly(){
        if (viewModelAllStocks.isOnQuerySearch)
        {binding.pagingRecyclerView.visibility= View.GONE
            binding.searchingRecyclerView.visibility = View.VISIBLE
            Log.d("StocksSelectionFragment", "isOnQuerySearch is true, hiding paging recycler view")
        }
        else{

            binding.pagingRecyclerView.visibility= View.VISIBLE
            binding.searchingRecyclerView.visibility = View.GONE
        }
    }


    }
