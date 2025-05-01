package il.kod.movingaverageapplication1.UI

import AllStocksViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.SelectedStocksViewModel
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.databinding.FragmentSelectedStocksBinding
import showConfirmationDialog


class SelectedStocksFragment : Fragment() {

    private var _binding: FragmentSelectedStocksBinding? = null

    private val binding get() = _binding!! //to avoid writing ? after every _binding

    private val viewModel: SelectedStocksViewModel by activityViewModels()

    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()






    companion object{
         val selectedStList: MutableList<Stock> = mutableListOf()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //viewModel = ViewModelProvider(this)[SelectedStocksViewModel::class.java]



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
                    val clickedStock = viewModelAllStocks.onItemClicked(index)//returns the object clicked
                    clickedStock?.let {
                        findNavController().navigate(
                            R.id.action_stockSelection3_to_detailsItemFragment,
                            bundleOf("item" to index)
                        )
                    }
                }

                override fun onItemLongClicked(index: Int) {

                    val clickedStock = viewModel.selectedStList.value?.get(index)

            showConfirmationDialog(
                context=requireContext(),
                title="Deletion of Stock",
                message = "Are you sure you want to delete this stock : ${clickedStock?.name} ?",
                onYes= {

                    viewModel.removeStock(clickedStock)
                    (binding.recyclerView.adapter as? StockAdapterFragment)?.notifyItemRemoved(index)

                    viewModelAllStocks.addStock(clickedStock)
                    Toast.makeText(
                        requireContext(),
                        "Successfully removed: ${clickedStock?.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onNo = {}

                    )


                }
            }
        )

        viewModel.selectedStList.observe(viewLifecycleOwner) { selectedStocks ->
            (binding.recyclerView.adapter as? StockAdapterFragment)?.updateData(selectedStocks)
        }






        binding.addStockButton.setOnClickListener {

            findNavController().navigate(R.id.action_selectedStocks_to_stockSelection3)
        }

        arguments?.getString("position")?.let {
            Toast.makeText(requireActivity(), "Added: $it ", Toast.LENGTH_LONG).show()
        }


        ItemTouchHelper(object: ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            )=makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                /*val to_remove = viewModel.selectedStList.value?.get(viewHolder.adapterPosition)
                viewModel.removeStock(to_remove)
                binding.recyclerView.adapter!!.notifyItemRemoved(viewHolder.adapterPosition)
                Toast.makeText(requireContext(), "Item deleted: ${to_remove?.name}", Toast.LENGTH_SHORT).show()*/
            }
        }).attachToRecyclerView(binding.recyclerView)


        return binding.root


    }






    fun onItemClicked(clickedStock: Stock) {


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

