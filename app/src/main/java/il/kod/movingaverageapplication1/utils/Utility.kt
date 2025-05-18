package il.kod.movingaverageapplication1.utils

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import il.kod.movingaverageapplication1.R


fun showConfirmationDialog(
    context: Context,
    title: String,
    message: String,
    onYes: () -> Unit,
    onNo: () -> Unit
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
        onYes()
        dialog.dismiss()
    }
    builder.setNegativeButton(context.getString(R.string.no)) { dialog, _ ->
        onNo()
        dialog.dismiss()
    }
    builder.create().show()
}

fun showNameInputDialog(
    context: Context,
    onNameEntered: (String) -> Unit,
    title: String,
    message: String
) {
    val editText = EditText(context)
    editText.hint = message

    AlertDialog.Builder(context)
        .setTitle(title)
        .setView(editText)
        .setPositiveButton(context.getString(R.string.ok)) { dialog, _ ->
            val name = editText.text.toString()
            if (name.isNotBlank()) {
                onNameEntered(name)
            } else {
                Toast.makeText(context, context.getString(R.string.valid_name_prompt), Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

fun showThresholdInputDialog(
    context: Context,
    onNumberEntered: (String) -> Unit,
    title: String,
    message: String
) {
    val editText = EditText(context)
    editText.hint = message

    AlertDialog.Builder(context)
        .setTitle(title)
        .setView(editText)
        .setPositiveButton(context.getString(R.string.ok)) { dialog, _ ->
            val number = editText.text.toString()
            if (number.isNotBlank() && number.isDigitsOnly()) {
                onNumberEntered(number)
            } else {
                Toast.makeText(context, context.getString(R.string.valid_number_prompt), Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}




    fun sharedMenuProvider( isListEmpty: Boolean=false, navController: NavController,
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
                            Toast.makeText(context, context.getString(R.string.select_stocks_first), Toast.LENGTH_SHORT).show()
                        } else {
                            navController.navigate(R.id.followSetFragment)
                            Toast.makeText(context, context.getString(R.string.add_following_sets), Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    R.id.action_stocks_list -> {
                        navController.navigate(R.id.followedStocks)
                        Toast.makeText(context, context.getString(R.string.view_followed_stocks), Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }
    }


