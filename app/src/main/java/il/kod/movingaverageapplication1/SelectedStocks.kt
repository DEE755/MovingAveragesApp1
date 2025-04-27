package il.kod.movingaverageapplication1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeFlag
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import il.kod.movingaverageapplication1.databinding.FragmentSelectedStocksBinding



class SelectedStocks : Fragment() {

    private var _binding: FragmentSelectedStocksBinding? = null

    private val binding get() = _binding!! //to avoid writing ? after every _binding





    companion object{
         val selectedStList: MutableList<Stock> = mutableListOf()
    }



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

        binding.recyclerView.adapter = StockAdapter(selectedStList,
            callBack = object : StockAdapter.ItemListener {


                override fun onItemClicked(index: Int) {
                    Toast.makeText(requireContext(), "Clicked: ${selectedStList[index].imageUri}}", Toast.LENGTH_SHORT).show()

                }
                override fun onItemLongClicked(index: Int) { val clickedStock = selectedStList[index]

                    Toast.makeText(requireContext(), "Successfully removed: ${clickedStock.name}", Toast.LENGTH_SHORT).show()
                    selectedStList.remove(clickedStock)}
            })


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
                val to_remove = selectedStList[viewHolder.adapterPosition]
                selectedStList.remove(to_remove)
                binding.recyclerView.adapter!!.notifyItemRemoved(viewHolder.adapterPosition)
                Toast.makeText(requireContext(), "Item deleted: ${to_remove.name}", Toast.LENGTH_SHORT).show()
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

