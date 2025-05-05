package il.kod.movingaverageapplication1.ui
import AllStocksViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.databinding.FragmentSelectedStocksBinding
import showConfirmationDialog
import kotlin.getValue


class FollowedStocksFragment : Fragment() {

    private var _binding: FragmentSelectedStocksBinding? = null

    private val binding get() = _binding!! //to avoid writing ? after every _binding

    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectedStocksBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.adapter = StockAdapterFragment(
            emptyList(),
            callBack = object : StockAdapterFragment.ItemListener {
                override fun onItemClicked(index: Int) {

                    val clickedStock =
                        viewModelAllStocks.onItemClicked(index)//returns the object clicked
                    Log.d("SelectedStocksFragment", "onItemClicked: ${clickedStock}")
                    clickedStock?.let {
                        findNavController().navigate(
                            R.id.action_selectedStocks_to_detailsItemFragment,
                            bundleOf(
                                "stock" to clickedStock))
                    }
                }

                override fun onItemLongClicked(index: Int) {

                    val clickedStock = viewModelAllStocks.selectedStList.value?.get(index)

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

        viewModelAllStocks.selectedStList.observe(viewLifecycleOwner) { selectedStocks ->
            (binding.recyclerView.adapter as? StockAdapterFragment)?.updateData(selectedStocks)
        }


        viewModelAllStocks.selectedStList.observe(viewLifecycleOwner) { selectedStocks ->
            if (viewModelAllStocks.selectedStList.value?.isEmpty() == false) {
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

        arguments?.getString("position")?.let {
            Toast.makeText(requireActivity(), "Added: $it ", Toast.LENGTH_LONG).show()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}