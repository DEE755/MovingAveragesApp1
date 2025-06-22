package il.kod.movingaverageapplication1.ui.fragment

import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.ui.viewmodel.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.data.repository.SyncManagementRepository
import il.kod.movingaverageapplication1.ui.viewmodel.FollowSetViewModel
import il.kod.movingaverageapplication1.databinding.FragmentFollowSetCreationBinding
import il.kod.movingaverageapplication1.utils.showNameInputDialog
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.getValue

@AndroidEntryPoint
class FollowSetCreationFragment : Fragment()
{

        @Inject
        lateinit var syncManager: SyncManagementRepository

        private var _binding: FragmentFollowSetCreationBinding? = null
        private val binding get() = _binding!!


        //shared viewmodels
        private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
        private val viewModelDetailStock: DetailStockViewModel by activityViewModels()
        private val viewModelFollowSet: FollowSetViewModel by activityViewModels()




        override fun onCreateView(



            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {


            _binding = FragmentFollowSetCreationBinding.inflate(inflater, container, false)

            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


            viewModelAllStocks.followedStocks.observe(viewLifecycleOwner)
            {
                binding.recyclerView.adapter = MultipleStockAdapterFragment(
                    it,
                    callBack = object : MultipleStockAdapterFragment.ItemListener {

                        override fun onItemClicked(index: Int) {
                            val checkBox : CheckBox? =binding.recyclerView.findViewHolderForAdapterPosition(index)?.itemView?.findViewById<CheckBox>(R.id.checkBox)
                            val clickedStock =
                                it[index]
                            clickedStock.let {
                                checkBox?.isChecked = checkBox!!.isChecked

                            }
                        }

                        override fun onItemLongClicked(index: Int) {
                            val clickedStock =
                                viewModelAllStocks.followedStocks.value?.get(index)
                            viewModelDetailStock.setStock(clickedStock!!)
                            findNavController().navigate(
                                R.id.action_stockSelection3_to_detailsItemFragment
                            )

                        }


                    })

            }


            binding.doneButton.setOnClickListener {

                val selectedIds = mutableListOf<Int>()
                var numberOfItems = binding.recyclerView.adapter?.itemCount ?: 0
                //iterate on all the items in the recycler view to see the state of their checkbox
                for (i in 0 until numberOfItems) {
                    val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(i)
                    val checkBox = viewHolder?.itemView?.findViewById<CheckBox>(R.id.checkBox)
                    if (checkBox?.isChecked == true) {
                        selectedIds.add(viewModelAllStocks.followedStocks.value?.get(i)?.id!!)
                    }
                }

                showNameInputDialog(
                    context = requireContext(),
                            title = getString(R.string.follow_set_name),
                            message = getString(R.string.enter_follow_set_name),
                            onNameEntered = { name -> //actions to perform after confirming the dialog
                                val createdFollowSet = FollowSet(
                                    name = name,
                                    imageUri = "TODO()",
                                    userComments = "TODO()",
                                    set_ids = selectedIds,
                                    -1.00
                                )
                                syncManager.pushFollowSetToRemoteDB(createdFollowSet, viewLifecycleOwner)//add to both local and remote db

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


