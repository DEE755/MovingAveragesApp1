package il.kod.movingaverageapplication1.ui.fragment.followset

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.NotificationsService
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.databinding.FragmentFollowSetBinding
import il.kod.movingaverageapplication1.ui.AppMenu
import il.kod.movingaverageapplication1.ui.NotificationHandler
import il.kod.movingaverageapplication1.ui.fragment.adapters.FollowSetAdapterFragment
import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.DetailStockViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.DialogViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.FollowSetViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.SyncManagementViewModel
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Resource
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.showConfirmationDialog
import kod.il.movingaverageapplication1.utils.sessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ExistingFollowSetFragment : Fragment() {


    @Inject
    lateinit var notificationHandler: NotificationHandler

    @Inject
    lateinit var appMenu: AppMenu


    private var _binding: FragmentFollowSetBinding? = null

    private val binding get() = _binding!! //to avoid writing ? after every _binding

    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()

    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()

    private val viewModelFollowSet: FollowSetViewModel by activityViewModels()

    private val dialogViewModel: DialogViewModel by activityViewModels()

    private val syncManagementViewModel: SyncManagementViewModel by activityViewModels()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startNotificationService()
            } else {
                Log.d("ExistingFollowSetFragment", "Notification permission denied")
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_LONG).show()
            }
        }

        if (!sessionManager.tutorialFollowSetHasBeenShown()) {
            dialogViewModel.showNotificationExplanationDialog(requireContext()) {
                // Launch permission request only after the dialog is displayed
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            dialogViewModel.showTutorialFollowsetDialog(requireContext())
        }
    }



    fun startNotificationService() {
    {
            Log.d("ExistingFollowSetFragment", "Notification permission granted")
            try {
                val intent = Intent(requireContext(), NotificationsService::class.java)
                requireContext().startService(intent)
                Log.d("ExistingFollowSetFragment", "NotificationService started successfully")
            } catch (e: Exception) {
                Log.d(
                    "ExistingFollowSetFragment",
                    "NotificationService error starting: ${e.message}"
                )
            }
    }

    askNotificationPermission()
}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        if (!sessionManager.userFollowSetsHaveBeenRetrievedOrNone())
        { Log.d("ExistingFollowSetFragment", "Pulling user follow sets from remote DB")
                syncManagementViewModel.pullUserFollowSetsFromToRemoteDB()
            val observer = object : Observer<Resource<List<FollowSet>>> {
                override fun onChanged(it: Resource<List<FollowSet>>) {
                    when (it.status) {
                        is Success -> {
                            syncManagementViewModel.followsetsFromRemote.removeObserver(this);
                            it.status.data?.let { data ->
                                if (data.isNotEmpty()) {
                                    sessionManager.setUserhasFollowedFollowSetsInRemoteDB()
                                    data.forEach { followSet ->
                                        followSet.userComments = ""
                                        followSet.imageUri=""
                                        viewModelFollowSet.addFollowSet(followSet)
                                    }


                                    dialogViewModel.showRestoredPreviouslyFollowedStocksDialog(
                                        requireContext(),
                                        "followset",
                                        data.size
                                    )
                                }

                            }
                            sessionManager.setUserFollowSetsHaveBeenRetrievedOrNone(true)
                            syncManagementViewModel.followsetsFromRemote.removeObserver(this)
                            Log.d("ExistingFollowSetFragment", "Successfully pulled user follow sets: ${it.status.data}")
                        }
                        is Error -> {
                            Log.d("ExistingFollowSetFragment", "Error pulling user follow sets: ${it.status.message}")
                            syncManagementViewModel.followsetsFromRemote.removeObserver(this)
                        }
                        is Loading -> {
                            Log.d("ExistingFollowSetFragment", "Loading user follow sets...")
                        }
                    }
                }
            }
                syncManagementViewModel.followsetsFromRemote.observe(viewLifecycleOwner, observer)
            }

        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(
            (appMenu.sharedMenuProvider(
                context = requireContext(),
                isListEmpty = false,
                navController = findNavController())
                    ),
            viewLifecycleOwner
        )
        _binding = FragmentFollowSetBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.adapter = FollowSetAdapterFragment(
            emptyList(),
            callBack = object : FollowSetAdapterFragment.ItemListener {
                override fun onItemClicked(index: Int) {

                    viewModelFollowSet.onItemClicked(index)?.let { followSet ->
                        viewModelFollowSet.clickedFollowSet.value = followSet
                        Log.d("ExistingFollowSetFragment", "Clicked FollowSet: ${followSet.name}")

                        findNavController().navigate(
                            R.id.action_followSetFragment_to_insideFollowSetFragment,
                            //TODO(SEE IF CAN BE REMOVED)bundleOf("clickedFollowSet" to followSet, "position" to index)
                        )
                    }

                }


                override fun onItemLongClicked(index: Int) {

                    val clickedFollowSet = viewModelFollowSet.getFollowSetAt(index)

                    Log.d("ExistingFollowSetFragment", "Long clicked FollowSet: ${clickedFollowSet}")

                    showConfirmationDialog(
                        context = requireContext(),
                        title = getString(R.string.deletion_follow_set_title),
                        message = getString(
                            R.string.delete_follow_set_message,
                            clickedFollowSet?.name
                        ),
                        onYes = {

                            syncManagementViewModel.setUserUnfollowsFollowSet(
                                clickedFollowSet!!
                            )
                            (binding.recyclerView.adapter as? FollowSetAdapterFragment)?.notifyItemRemoved(
                                index
                            )

                            Toast.makeText(
                                requireContext(),
                                getString(R.string.success_remove, clickedFollowSet.name),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onNo = {}
                    )
                }
            }
            ,viewModelAllStocks, viewModelFollowSet
        )

        if (viewModelFollowSet.newCreatedFollowSet!=null){
            Log.d("ExistingFollowSetFragment", "Adding new follow set to remote DB: ${viewModelFollowSet.newCreatedFollowSet}")
            syncManagementViewModel.saveNewFollowSet(viewModelFollowSet.newCreatedFollowSet!!)//add to both local and remote db

            //Observe response and update local DB insert id answered for linking
            syncManagementViewModel.answerFromRemoteFollowSetInsertId.observe(viewLifecycleOwner)

            { result ->
                when (result.status) {
                    is Success -> {
                        binding.progressWheel.visibility=View.GONE
                        Log.d(
                            "ExistingFollowSetFragment",
                            "ADDING to follow in local DB: ${result.status.data}"
                        )
                        viewModelFollowSet.setUserFollowsStockAnswer = result.status.message


                        viewModelFollowSet.newCreatedFollowSet=null

                    }

                    is Error -> {
                        viewModelFollowSet.setUserFollowsStockAnswer=
                            "Error adding follow set: ${result.status.message}"

                        Log.e("SyncManager", result.status.message)

                       viewModelFollowSet.dummyFollowSet.let{
                            viewModelFollowSet.removeFollowSet(it!!)
                        }

                    }

                    is Loading -> {

                        viewModelFollowSet.setUserFollowsStockAnswer =
                            "Adding follow set... please wait"

                        binding.progressWheel.visibility=View.VISIBLE


                    }
                }
                Log.d("SyncManager", viewModelFollowSet.setUserFollowsStockAnswer)

            }

        }

        else {
            Log.d("ExistingFollowSetFragment", "No new follow set to add")
        }



        viewModelFollowSet.getAllFollowSet()
        viewModelFollowSet.existingFollowSet.observe(viewLifecycleOwner) {
            (binding.recyclerView.adapter as? FollowSetAdapterFragment)?.updateData(it)

             if(sessionManager.fetchedStocksFromRemoteDB) {
                 binding.isEmptytextView.text =
                     getString(R.string.you_don_t_follow_any_stocks_yet_n_click_the_button_below_to_add_stocks)
             }

            if (!it.isEmpty()) {
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

    fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            when {
                ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                }

                shouldShowRequestPermissionRationale(permission) -> {
                    Toast.makeText(requireContext(), "Notification permission required", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(permission)
                }

                else -> {
                    requestPermissionLauncher.launch(permission)
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Notifications are active by default",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Following Sets"
    }

    override fun onStop() {

        super.onStop()



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}