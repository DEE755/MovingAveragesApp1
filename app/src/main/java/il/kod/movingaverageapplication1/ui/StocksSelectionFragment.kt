package il.kod.movingaverageapplication1.ui

import AllStocksViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.databinding.FragmentAllStockSelectionBinding

class StocksSelectionFragment : Fragment() {

    private var _binding: FragmentAllStockSelectionBinding? = null
    private val binding get() = _binding!!



        //shared viewmodel
    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()




    var bundle :Bundle = Bundle()


    override fun onCreateView(



        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        _binding = FragmentAllStockSelectionBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())



//if a new stock is added to the allstocklist, it will be added to the recycler view

        viewModelAllStocks.unselectedStockList?.observe(viewLifecycleOwner)
        {

            binding.recyclerView.adapter = StockAdapterFragment(
                viewModelAllStocks.unselectedStockList?.value ?: emptyList(),
                callBack = object : StockAdapterFragment.ItemListener {

                    override fun onItemClicked(index: Int) {
                        val clickedStock = viewModelAllStocks.unselectedStockList?.value?.get(index)
                        Log.d("Cliked", "clicked:$clickedStock, index: $index")
                        clickedStock?.let {
                            findNavController().navigate(
                                R.id.action_stockSelection3_to_detailsItemFragment,
                                bundleOf(
                                    "stock" to clickedStock))
                        }
                    }

                    override fun onItemLongClicked(index: Int) {
                            return
                        //val clickedStock = viewModelAllStocks.onItemClicked(index)

                        //Toast.makeText(
                            //requireContext(),
                            //"Successfully added: ${clickedStock?.name}",
                           // Toast.LENGTH_SHORT
                        //).show()
                        //viewModelSelectedStock.addStock(clickedStock)
                        //viewModelAllStocks.removeStock(clickedStock)
                        //bundle.putParcelable("stock", clickedStock)
                    }




                })

        }




        /*ItemTouchHelper(object: ItemTouchHelper.Callback() {
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
        }).attachToRecyclerView(binding.recyclerView)*/




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