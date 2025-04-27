package il.kod.movingaverageapplication1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import il.kod.movingaverageapplication1.databinding.FragmentAllStockSelectionBinding

class AllStockSelection : Fragment() {

    private var _binding: FragmentAllStockSelectionBinding? = null
    private val binding get() = _binding!!

    var bundle :Bundle = Bundle()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllStockSelectionBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.adapter = StockAdapter(ExampleStocks.stockList,
            callBack = object : StockAdapter.ItemListener {


            override fun onItemClicked(index: Int) {
            val clickedStock = ExampleStocks.stockList[index]

            Toast.makeText(requireContext(), "Successfully added: ${clickedStock.name}", Toast.LENGTH_SHORT).show()
            SelectedStocks.selectedStList.add(clickedStock)

        }
            override fun onItemLongClicked(index: Int) {}
        })



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
                Toast.makeText(requireContext(), "You cannot remove an object from that list", Toast.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(binding.recyclerView)







binding.returntoselected.setOnClickListener {
    //val bundle = bundleOf("element1" to binding.listView.tag.toString(), "description" to binding.textView.toString()) //pass another data to the next fragment (here for exemple position in the list
    findNavController().navigate(R.id.action_stockSelection3_to_selectedStocks, bundle)


}
        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}