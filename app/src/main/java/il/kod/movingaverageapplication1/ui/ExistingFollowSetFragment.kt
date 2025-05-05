package il.kod.movingaverageapplication1.ui

import AllStocksViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import il.kod.movingaverageapplication1.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.FollowSetViewModel
import il.kod.movingaverageapplication1.databinding.FragmentFollowSetBinding
import il.kod.movingaverageapplication1.sharedMenuProvider
import il.kod.movingaverageapplication1.showConfirmationDialog
import kotlin.getValue

class ExistingFollowSetFragment : Fragment() {
    private var _binding: FragmentFollowSetBinding? = null

    private val binding get() = _binding!! //to avoid writing ? after every _binding

    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()

    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()

    private val viewModelFollowSet: FollowSetViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(
            (sharedMenuProvider(
                context = requireContext(),
                isListEmpty = false,
                navController = findNavController())
                    ),
            viewLifecycleOwner
        )
        _binding = FragmentFollowSetBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.adapter = StockAdapterFragment(
            emptyList(),
            callBack = object : StockAdapterFragment.ItemListener {
                override fun onItemClicked(index: Int) {

                        viewModelFollowSet.onItemClicked(index)?.let { followSet ->
                            viewModelFollowSet.clickedFollowSet = followSet
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

        viewModelFollowSet.getAllFollowSet().observe(viewLifecycleOwner) { allFollowSets ->
            (binding.recyclerView.adapter as? FollowSetAdapterFragment)?.updateData(allFollowSets)
        }


        viewModelFollowSet.getAllFollowSet().observe(viewLifecycleOwner) { selectedStocks ->
            if (viewModelFollowSet.getAllFollowSet().value?.size == 0) {
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

            findNavController().navigate(R.id.action_followSetFragment_to_followSetCreationFragment)
        }

        binding.addStockButtonSmall.setOnClickListener {

            binding.addStockButtonBig.callOnClick()
        }



        return binding.root
    }

    override fun onResume() {
        super.onResume()

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Following Sets"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}