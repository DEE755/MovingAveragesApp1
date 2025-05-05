package il.kod.movingaverageapplication1.ui

import AllStocksViewModel
import android.content.Context
import android.os.Bundle
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
import il.kod.movingaverageapplication1.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.databinding.FragmentSelectedStocksBinding
import il.kod.movingaverageapplication1.sharedMenuProvider

import il.kod.movingaverageapplication1.showConfirmationDialog



class FollowedStocksFragment : Fragment() {

    private var _binding: FragmentSelectedStocksBinding? = null

    private val binding get() = _binding!! //to avoid writing ? after every _binding

    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()

    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val menuHost: MenuHost = requireActivity()
        var currentMenuProvider: MenuProvider? = null
        viewModelAllStocks.selectedStockList.observe(viewLifecycleOwner) {

            val isEmpty = it.isEmpty()

            currentMenuProvider?.let { menuHost.removeMenuProvider(it) }

            currentMenuProvider = sharedMenuProvider(
                context = requireContext(),
                isListEmpty = isEmpty,
                navController = findNavController()
            )
            menuHost.addMenuProvider(currentMenuProvider!!, viewLifecycleOwner)
        }

            _binding = FragmentSelectedStocksBinding.inflate(inflater, container, false)

            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


            binding.recyclerView.adapter = StockAdapterFragment(
                emptyList(),
                callBack = object : StockAdapterFragment.ItemListener {
                    override fun onItemClicked(index: Int) {

                        viewModelAllStocks.selectedStockList.value?.get(index)
                            ?.let { selectedStock ->
                                viewModelDetailStock.clickedStock.value = selectedStock
                                findNavController().navigate(R.id.action_selectedStocks_to_detailsItemFragment)
                            }
                    }

                    override fun onItemLongClicked(index: Int) {

                        val clickedStock = viewModelAllStocks.selectedStockList.value?.get(index)

                        showConfirmationDialog(
                            context = requireContext(),
                            title = "Deletion of Stock",
                            message = "Are you sure you want to delete this stock : ${clickedStock?.name} ?",
                            onYes = {

                                viewModelAllStocks.unfollowStock(clickedStock!!)
                                (binding.recyclerView.adapter as? StockAdapterFragment)?.notifyItemRemoved(
                                    index
                                )

                                Toast.makeText(
                                    requireContext(),
                                    "Successfully removed: ${clickedStock.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onNo = {}

                        )


                    }
                }
            )

            viewModelAllStocks.selectedStockList.observe(viewLifecycleOwner) { selectedStocks ->
                (binding.recyclerView.adapter as? StockAdapterFragment)?.updateData(selectedStocks)
            }


            viewModelAllStocks.selectedStockList.observe(viewLifecycleOwner) { selectedStocks ->
                if (viewModelAllStocks.selectedStockList.value?.isEmpty() == false) {
                    binding.addStockButtonBig.visibility = View.GONE
                    binding.isEmptytextView.visibility = View.GONE
                    binding.addStockButtonSmall.visibility = View.VISIBLE
                } else {
                    binding.addStockButtonBig.visibility = View.VISIBLE
                    binding.isEmptytextView.visibility = View.VISIBLE
                    binding.addStockButtonSmall.visibility = View.GONE
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

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Followed Stocks"
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}












