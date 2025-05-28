package il.kod.movingaverageapplication1.ui

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.utils.showConfirmationDialog
import javax.inject.Inject

@AndroidEntryPoint
class AppMenu @Inject constructor(private val sessionManager: SessionManager, @ApplicationContext context: Context) : Fragment() {

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
                        showConfirmationDialog(
                            context = context,
                            title = "Logout",
                            message = "Are you sure you want to log out?",
                            onYes = {
                                sessionManager.logOutClient()
                                navController.navigate(R.id.signUpFragment)
                                Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
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