package il.kod.movingaverageapplication1

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider



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
    builder.setPositiveButton("Yes") { dialog, _ ->
        onYes()
        dialog.dismiss()
    }
    builder.setNegativeButton("No") { dialog, _ ->
        onNo()
        dialog.dismiss()
    }
    builder.create().show()
}

fun showNameInputDialog(
    context: Context,
    onNameEntered: (String) -> Unit,
    title: String,
    message: String) {
    val editText = EditText(context)
    editText.hint = message

    android.app.AlertDialog.Builder(context)
        .setTitle(title)
        .setView(editText)
        .setPositiveButton("OK") { dialog, _ ->
            val name = editText.text.toString()
            if (name.isNotBlank()) {
                onNameEntered(name)
            }
            dialog.dismiss()
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}





    fun sharedMenuProvider( isListEmpty: Boolean=false, navController: androidx.navigation.NavController,
        context: Context,
    ): MenuProvider {
        return object : MenuProvider {
            override fun onCreateMenu(menu: android.view.Menu, menuInflater: android.view.MenuInflater) {
                menuInflater.inflate(R.menu.menu_main_activity, menu)
            }

            override fun onMenuItemSelected(menuItem: android.view.MenuItem): Boolean {

                return when (menuItem.itemId) {
                    R.id.action_follow_list -> {
                        if  (isListEmpty) {
                            Toast.makeText(context, "Please select stocks first", Toast.LENGTH_SHORT).show()
                        } else
                            navController.navigate(R.id.followSetFragment)

                        true
                    }
                    R.id.action_stocks_list -> {
                        navController.navigate(R.id.followedStocks)
                        Toast.makeText(context, "stock_list", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }
    }


