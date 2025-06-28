package il.kod.movingaverageapplication1.ui.fragment

import android.content.Intent
import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.NotificationsService
import il.kod.movingaverageapplication1.ui.viewmodel.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.databinding.FragmentSelectedStocksBinding
import il.kod.movingaverageapplication1.ui.AppMenu
import il.kod.movingaverageapplication1.ui.viewmodel.CustomServerDatabaseViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.DialogViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.SyncManagementViewModel
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success


import il.kod.movingaverageapplication1.utils.showConfirmationDialog
import kotlinx.coroutines.Job
import javax.inject.Inject


@AndroidEntryPoint
class FollowedStocksFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var appMenu: AppMenu

    @Inject
    lateinit var sessionManager: SessionManager


    private var _binding: FragmentSelectedStocksBinding? = null

    private val binding get() = _binding!! //to avoid writing ? after every _binding

    // Shared ViewModels

    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()

    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()

    private val CSDViewModel: CustomServerDatabaseViewModel by activityViewModels()

    private val syncManagementViewModel: SyncManagementViewModel by activityViewModels()

    private val dialogViewModel: DialogViewModel by activityViewModels()


    private var priceUpdateJob: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            //START THE NOTIFICATION SERVICE (WILL BIND IT LATER)
        if (sessionManager.isNotificationsServiceStarted==false)
        { val intent = Intent(context, NotificationsService::class.java)
        context?.startService(intent)
        sessionManager.isNotificationsServiceStarted = true
        }

        if (sessionManager.allStocksPackHaveBeenFetch()){syncManagementViewModel.pullAndInjectFollowedStocksFromRemote()}


    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val menuHost: MenuHost = requireActivity()
        var currentMenu: MenuProvider? = null




        viewModelAllStocks.followedStocks.observe(viewLifecycleOwner) {
            val isEmpty = it.isEmpty()

            val oldMenu = currentMenu
            if (oldMenu != null) {
                menuHost.removeMenuProvider(oldMenu)
            }

            val newMenu = appMenu.sharedMenuProvider(
                context = requireContext(),
                isListEmpty = isEmpty,
                navController = findNavController()
            )
            currentMenu = newMenu
            menuHost.addMenuProvider(newMenu, viewLifecycleOwner)

        }


            _binding = FragmentSelectedStocksBinding.inflate(inflater, container, false)

            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


            binding.recyclerView.adapter = StockRecyclerAdapterFragment(
                emptyList(),
                callBack = object : StockRecyclerAdapterFragment.SearchedStockClickListener {
                    override fun onSearchedStockClicked(index: Int) {

                        viewModelAllStocks.followedStocks.value?.get(index)
                            ?.let { selectedStock ->
                                viewModelDetailStock.clickedStock.value = selectedStock
                                findNavController().navigate(R.id.action_selectedStocks_to_detailsItemFragment)
                            }
                    }

                    override fun onSearchedStockLongClicked(index: Int) {
                        val clickedStock = viewModelAllStocks.followedStocks.value?.get(index)

                        //TODO(make use of dialogViewModel instead)
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



            viewModelAllStocks.followedStocks.observe(viewLifecycleOwner) { selectedStocks ->
                (binding.recyclerView.adapter as? StockRecyclerAdapterFragment)?.updateData(selectedStocks)
                if (viewModelAllStocks.followedStocks.value?.isEmpty() == false) {
                    if (!sessionManager.restoredFollowedStocksDialogHasBeenShown() && sessionManager.userHasFollowedStocksinRemoteDB)
                    {
                    dialogViewModel.showRestoredPreviouslyFollowedStocksDialog(requireContext(), "stock", -1 )
                    }
                    binding.addStockButtonBig.visibility = View.GONE
                    binding.isEmptytextView.visibility = View.GONE
                    binding.addStockButtonSmall.visibility = View.VISIBLE
                } else {
                    binding.addStockButtonBig.visibility = View.VISIBLE
                    binding.isEmptytextView.visibility = View.VISIBLE
                    binding.addStockButtonSmall.visibility = View.GONE
                }
            }


CSDViewModel.updatedStockPrice.observe(viewLifecycleOwner)
{

    when (it.status) {
        is Success -> {
            it?.status?.data.let { listOfStockPriceOnly ->
                if (listOfStockPriceOnly != null) {
                    for (stock in listOfStockPriceOnly) {
                        viewModelAllStocks.updateStockPrice(stock.symbol, stock.current_price)
                    }
                }
            }
        }

        is Error -> {
            Log.d("FollowedStocksFragment", "Error fetching stock prices: ${it.status.message}")

            Toast.makeText(requireContext(), "Problem Fetching Prices", Toast.LENGTH_SHORT).show()
        }


        is Loading-> {
            //TODO()/*FIND THE FOLLOWED STOCKS BINDING AND WRITE "LOADING"*/
            //Toast.makeText(requireContext(), "Updating stock prices", Toast.LENGTH_SHORT).show()

            }
        }

}

        CSDViewModel.getFollowedMovingAverages().observe(viewLifecycleOwner)
        {
            when (it.status) {
                is Error -> {
                    Log.d(
                        "FollowedStocksFragment",
                        "Error fetching moving averages: ${it.status.message}"
                    )
                    Toast.makeText(
                        requireContext(),
                        "Problem Fetching Moving Averages",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Loading -> {}

                is Success -> {
                    it.status.data?.let { listOfStockMovingAveragesOnly ->
                        for (stock in listOfStockMovingAveragesOnly) {
                            viewModelAllStocks.updateMovingAverages(
                                stock.symbol,
                                stock.ma_50,
                                stock.ma_25,
                                stock.ma_150,
                                stock.ma_200
                            )
                        }
                    }
                }
            }
        }



            binding.addStockButtonBig.setOnClickListener {

                findNavController().navigate(R.id.action_selectedStocks_to_stockSelection3)
            }

            binding.addStockButtonSmall.setOnClickListener {

                findNavController().navigate(R.id.action_selectedStocks_to_stockSelection3)
            }


            return binding.root

    }


    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.followed_stocks_title)
        CSDViewModel.getFollowedStockPrice() // trigger the request
    }



    override fun onDestroyView() {
        super.onDestroyView()
        priceUpdateJob?.cancel()
        _binding = null
    }
}


