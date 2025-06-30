package il.kod.movingaverageapplication1.ui

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels

import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint

import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.data.repository.LocalStocksRepository
import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.DialogViewModel
import il.kod.movingaverageapplication1.utils.showGenericDialogNoInput
import il.kod.movingaverageapplication1.utils.showGenericDialogNoInput2choices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppMenu @Inject constructor(private val sessionManager: SessionManager, val localStocksRepository: LocalStocksRepository) : Fragment() {


    fun sharedMenuProvider(
        isListEmpty: Boolean = false, navController: NavController,
        context: Context,
    ): MenuProvider {
        return object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main_activity, menu)

                CoroutineScope(Dispatchers.IO).launch {
                    if (localStocksRepository.getSelectedStockCount() == 0) {
                        val item = menu.findItem(R.id.action_follow_list)
                        item.icon?.alpha = 50
                    }
                    }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_follow_list -> {
                        if (isListEmpty) {
                            showGenericDialogNoInput(
                                context = context,
                                title = context.getString(R.string.no_stocks_selected),
                                    positiveButtonText = context.getString(R.string.close),
                                message = context.getString((R.string.select_stocks_first)))


                        } else {
                            navController.navigate(R.id.followSetFragment)
                            Toast.makeText(
                                context,
                                context.getString(R.string.add_following_sets),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        true
                    }

                    R.id.action_stocks_list -> {
                        navController.navigate(R.id.followedStocks)
                        Toast.makeText(
                            context,
                            context.getString(R.string.view_followed_stocks),
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }

                    R.id.action_logout -> {
                        showGenericDialogNoInput2choices(
                            context = context,
                            title = context.getString(R.string.logout),
                            message = context.getString(R.string.logout_message, sessionManager.getUsername()),
                            onYes = {Toast.makeText(context, context.getString(R.string.logged_out_message, sessionManager.getUsername()), Toast.LENGTH_SHORT).show()
                                sessionManager.logOutClient()
                                navController.navigate(R.id.signUpFragment)
                            },
                            onNo = {}
                        )




                        true
                    }

                    else -> false
                }
            }
        }
    }
}