package il.kod.movingaverageapplication1.ui

import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
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
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.databinding.FragmentSelectedStocksBinding
import il.kod.movingaverageapplication1.utils.sharedMenuProvider

import il.kod.movingaverageapplication1.utils.showConfirmationDialog
import javax.inject.Inject

@AndroidEntryPoint
class FollowedStocksFragment : Fragment() {
@Inject
    lateinit var glide: RequestManager

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
        var currentMenu: MenuProvider? = null




        viewModelAllStocks.followedStocks.observe(viewLifecycleOwner) {
            val isEmpty = it.isEmpty()


            val oldMenu = currentMenu
            if (oldMenu != null) {
                menuHost.removeMenuProvider(oldMenu)
            }

            val newMenu = sharedMenuProvider(
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
                callBack = object : StockRecyclerAdapterFragment.ItemListener {
                    override fun onItemClicked(index: Int) {

                        viewModelAllStocks.followedStocks.value?.get(index)
                            ?.let { selectedStock ->
                                viewModelDetailStock.clickedStock.value = selectedStock
                                findNavController().navigate(R.id.action_selectedStocks_to_detailsItemFragment)
                            }
                    }

                    override fun onItemLongClicked(index: Int) {
                        val clickedStock = viewModelAllStocks.followedStocks.value?.get(index)

                        showConfirmationDialog(
                            context = requireContext(),
                            title = getString(R.string.deletion_stock_title),
                            message = getString(R.string.delete_stock_message, clickedStock?.name),
                            onYes = {
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



            viewModelAllStocks.followedStocks.observe(viewLifecycleOwner) { selectedStocks ->
                (binding.recyclerView.adapter as? StockRecyclerAdapterFragment)?.updateData(selectedStocks)
                if (viewModelAllStocks.followedStocks.value?.isEmpty() == false) {
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
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.followed_stocks_title)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}












