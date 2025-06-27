package il.kod.movingaverageapplication1.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.utils.showGenericDialogInput
import il.kod.movingaverageapplication1.utils.showGenericDialogNoInput
import il.kod.movingaverageapplication1.utils.showLargeGenericDialogInput
import il.kod.movingaverageapplication1.utils.showNotificationDialog
import il.kod.movingaverageapplication1.utils.showThresholdInputDialog
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(private val sessionManager: SessionManager): ViewModel()
{
     val username= sessionManager.getUsername()


    fun showDescriptionInputDialog(
        context: Context,
        onDescriptionEntered: (String) -> Unit,
        object1 : Any? = null
    ) {
        val objectName: String= when (object1) {
            is FollowSet -> "${object1.name} FollowSet"
            is Stock ->"${object1.name} stock"
            else -> "this object"
        }

        showLargeGenericDialogInput(
            context = context,
            title = "Enter Description for $objectName",
            message = "Enter your comments for $objectName :",
            onInputEntered = { description ->
                if (description.isNotBlank()) {
                    onDescriptionEntered(description)
                } else {
                    Toast.makeText(context, "Description cannot be empty", Toast.LENGTH_SHORT).show()
                }
            },
            invalidInputMessage = "Please enter a valid description",

            )
    }

    fun showFollowSetInputNameDialog(
        context: Context,
        onNameEntered: (String) -> Unit,
    ) {
        showGenericDialogInput(
            context = context,
            title = "FollowSet Name",
            message = "Please enter a name for your FollowSet:",
            onInputEntered = { name ->
                if (name.isNotBlank()) {
                    onNameEntered(name)
                } else {
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            },
            invalidInputMessage = "Please enter a valid name",

            )
    }

    fun showTutorialFollowsetDialog(context: Context )
    {
        showNotificationDialog(
            context = context,
            title = "About FollowSets Functionality",
            message = "Hi, ${username}! You entered the FollowSet functionality.\n " +
                    "You can create sets from stocks you already follow, and then manage them together.\n" +
                    "You'll be able track your favorite stocks performance together or ask our AI adviser about investing those stocks together, etc..\n\n" +
                    "Start by clicking the main button to create your first FollowSet.",
            onYes = {}
        )
    }

    fun showThresholdInputDialogVM(context: Context, followSetName: String, function: (Double)-> Unit) {
        showThresholdInputDialog(
            context = context,
            onNumberEntered = {thresholdValue ->
                function(thresholdValue)

            },
            followSetName= followSetName,

        )
    }


    fun showNotificationExplanationDialog(context: Context, onYes: () -> Unit) =
        showNotificationDialog(
            context = context,
            title = "Notifications Permission",
            message = "Dear ${username}, we kindly ask you to allow notifications for our app, so you can receive important updates about your stocks and FollowSets.\n" +
                    "You can always change this in the app settings later.\n\n" +
                    "Click 'Allow' to enable notifications now.",
            {onYes()}
        )



    fun showRestoredPreviouslyFollowedStocksDialog(context: Context, object1: String, size:Int) =
    showNotificationDialog(
    context, "Previously Followed Stocks",
    "${username}, you were previously following " +
            "${if (size==-1) "some" else size} ${object1}${if (size == 1) "" else "s"}., "+
            "we retrieved them for you and you can now see them in the list."+
        "If you add any followSet they will as well be added to the FollowSets list.",
        {}
    )

    fun showDescriptionDialog(context: Context, followSet: FollowSet) {
        showGenericDialogNoInput(
            context = context,
            title = "${followSet.name}${username}'s comments",
            message = followSet.userComments!!,
        )
    }



}