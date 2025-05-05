package il.kod.movingaverageapplication1.ui

import AllStocksViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import il.kod.movingaverageapplication1.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.FollowSet
import il.kod.movingaverageapplication1.data.FollowSetViewModel
import il.kod.movingaverageapplication1.databinding.FragmentFollowSetCreationBinding
import il.kod.movingaverageapplication1.databinding.ItemCheckLayoutBinding
import il.kod.movingaverageapplication1.showNameInputDialog
import kotlin.getValue

class FollowSetCreationFragment : Fragment()
{


        private var _binding: FragmentFollowSetCreationBinding? = null
        private val binding get() = _binding!!

        private var _binding2: ItemCheckLayoutBinding? = null
        private val binding2 get() = _binding2!!


        //shared viewmodels
        private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
        private val viewModelDetailStock: DetailStockViewModel by activityViewModels()
        private val viewModelFollowStock: FollowSetViewModel by activityViewModels()



        var bundle :Bundle = Bundle()


        override fun onCreateView(



            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {


            _binding = FragmentFollowSetCreationBinding.inflate(inflater, container, false)

            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

            _binding2 = ItemCheckLayoutBinding.inflate(inflater, container, false)


//if a new stock is added to the allstocklist, it will be added to the recycler view

            viewModelAllStocks.unselectedStockList.observe(viewLifecycleOwner)
            {

                binding.recyclerView.adapter = MultipleStockAdapterFragment(
                    viewModelAllStocks.selectedStockList.value ?: emptyList(),
                    callBack = object : MultipleStockAdapterFragment.ItemListener {

                        override fun onItemClicked(index: Int) {
                            val clickedStock =
                                viewModelAllStocks.selectedStockList.value?.get(index)
                            clickedStock?.let {
                                binding2.checkBox.isChecked = !binding2.checkBox.isChecked

                            }
                        }

                        override fun onItemLongClicked(index: Int) {
                            val clickedStock =
                                viewModelAllStocks.selectedStockList.value?.get(index)
                            viewModelDetailStock.setStock(clickedStock!!)
                            findNavController().navigate(
                                R.id.action_stockSelection3_to_detailsItemFragment
                            )

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




            binding.doneButton.setOnClickListener {
                //val bundle = bundleOf("element1" to binding.listView.tag.toString(), "description" to binding.textView.toString()) //pass another data to the next fragment (here for exemple position in the list
                //findNavController().navigate(R.id.action_stockSelection3_to_selectedStocks, bundle)

                val selectedIds = mutableListOf<Int>()
                var numberOfItems = binding.recyclerView.adapter?.itemCount ?: 0
                //iterate on all the items in the recycler view to see the state of their checkbox
                for (i in 0 until numberOfItems) {
                    val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(i)
                    val checkBox = viewHolder?.itemView?.findViewById<CheckBox>(R.id.checkBox)
                    if (checkBox?.isChecked == true) {
                        selectedIds.add(viewModelAllStocks.selectedStockList.value?.get(i)?.id!!)
                    }
                }

                showNameInputDialog(
                    context = requireContext(),
                    title = "Follow Set Name",
                    message = "Please enter a name for your new Follow Set",
                    onNameEntered = { name ->
                        val createdFollowSet = FollowSet(
                            name = name,
                            imageUri = "TODO()",
                            userComments = "TODO()",
                            set_ids = selectedIds
                        )
                        viewModelFollowStock.addFollowSet(createdFollowSet)
                        findNavController().popBackStack()
                    }

                )

            }

            binding.cancelButton.setOnClickListener {
                findNavController().popBackStack()

            }
            return binding.root
        }



        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }


    }


