package il.kod.movingaverageapplication1.ui.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.NotificationService
import il.kod.movingaverageapplication1.ui.viewmodel.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.ui.viewmodel.FollowSetViewModel
import il.kod.movingaverageapplication1.databinding.FragmentFollowSetBinding
import il.kod.movingaverageapplication1.ui.AppMenu
import il.kod.movingaverageapplication1.ui.NotificationHandler
import il.kod.movingaverageapplication1.utils.showConfirmationDialog
import android.Manifest
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import javax.inject.Inject
import kotlin.getValue
import kotlin.jvm.java
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import kod.il.movingaverageapplication1.utils.sessionManager
import kotlinx.coroutines.launch


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

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // Start the NotificationService when the fragment is created (Because first after login)
        try {
            val intent = Intent(requireContext(), NotificationService::class.java)
            requireContext().startService(intent)
            Log.d("ExistingFollowSetFragment", "NotificationService started successfully")
        } catch (e: Exception) {
            Log.d("ExistingFollowSetFragment", "NotificationService error starting: ${e.message}")
        }



        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d("ExistingFollowSetFragment", "Notification permission granted")
                //notificationHandler.showNotification("Title", "Message")
            } else {
                Log.d("ExistingFollowSetFragment", "Notification permission denied")
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_LONG).show()
            }
        }

       askNotificationPermission()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

                            findNavController().navigate(R.id.action_followSetFragment_to_insideFollowSetFragment,
                                bundleOf("clickedFollowSet" to followSet, "position" to index)
                            )
                        }

                    }


                override fun onItemLongClicked(index: Int) {

                    val clickedFollowSet = viewModelFollowSet.getFollowSetAt(index)

                    showConfirmationDialog(
                        context = requireContext(),
                        title = getString(R.string.deletion_follow_set_title),
                        message = getString(R.string.delete_follow_set_message, clickedFollowSet?.name),
                        onYes = {

                            viewModelFollowSet.removeFollowSet(clickedFollowSet!!)
                            (binding.recyclerView.adapter as? FollowSetAdapterFragment)?.notifyItemRemoved(
                                index
                            )

                            Toast.makeText(
                                requireContext(),
                                getString(R.string.success_remove,clickedFollowSet.name),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onNo = {}

                    )


                }
            }
        )

        viewModelFollowSet.getAllFollowSet().observe(viewLifecycleOwner) {
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
                    Toast.makeText(
                        requireContext(),
                        "Notifications are active, permission already granted",
                        Toast.LENGTH_LONG
                    ).show()
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}