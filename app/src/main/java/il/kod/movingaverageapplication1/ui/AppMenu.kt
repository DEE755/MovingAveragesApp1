package il.kod.movingaverageapplication1.ui

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment

import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint

import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.ui.viewmodel.DialogViewModel
import il.kod.movingaverageapplication1.utils.showConfirmationDialog
import il.kod.movingaverageapplication1.utils.showGenericDialogNoInput2choices
import javax.inject.Inject

@AndroidEntryPoint
class AppMenu @Inject constructor(private val sessionManager: SessionManager) : Fragment() {


    fun sharedMenuProvider(
        isListEmpty: Boolean = false, navController: NavController,
        context: Context,
    ): MenuProvider {
        return object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main_activity, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_follow_list -> {
                        if (isListEmpty) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.select_stocks_first),
                                Toast.LENGTH_SHORT
                            ).show()
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
                            title = "Logout",
                            message = "Currently logged as ${sessionManager.getUsername()}.\n\nDo you want to log out?",
                            onYes = {Toast.makeText(context, "Logged Out! Goodbye ${sessionManager.getUsername()}", Toast.LENGTH_SHORT).show()
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