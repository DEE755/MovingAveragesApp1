package il.kod.movingaverageapplication1.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import il.kod.movingaverageapplication1.R
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
            is FollowSet -> context.getString(R.string.followset_display_name, object1.name)
            is Stock -> context.getString(R.string.stock_display_name, object1.name)
            else -> context.getString(R.string.this_object)
        }

        showLargeGenericDialogInput(
            context = context,
            title = context.getString(R.string.enter_description_title, objectName),
            message = context.getString(R.string.enter_description_message, objectName),
            onInputEntered = { description ->
                if (description.isNotBlank()) {
                    onDescriptionEntered(description)
                } else {
                    Toast.makeText(context, context.getString(R.string.description_cannot_be_empty), Toast.LENGTH_SHORT).show()
                }
            },
            invalidInputMessage = context.getString(R.string.enter_valid_description),

            )
    }

    fun showFollowSetInputNameDialog(
        context: Context,
        onNameEntered: (String) -> Unit,
    ) {
        showGenericDialogInput(
            context = context,
            title = context.getString(R.string.followset_name_title),
            message = context.getString(R.string.enter_followset_name_message),
            onInputEntered = { name ->
                if (name.isNotBlank()) {
                    onNameEntered(name)
                } else {
                    Toast.makeText(context, context.getString(R.string.name_cannot_be_empty), Toast.LENGTH_SHORT).show()
                }
            },
            invalidInputMessage = context.getString(R.string.valid_name_prompt),

            )
    }

    fun showTutorialFollowsetDialog(context: Context )
    {
        showNotificationDialog(
            context = context,
            title = context.getString(R.string.about_followsets_title),
            message = context.getString(R.string.about_followsets_message, username),
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
            title = context.getString(R.string.notifications_permission_title),
            message = context.getString(R.string.notifications_permission_message, username),
            {onYes()}
        )



    fun showRestoredPreviouslyFollowedStocksDialog(context: Context, object1: String, size:Int) =
    showNotificationDialog(
    context, context.getString(R.string.previously_followed_stocks),
    context.getString(R.string.retrieved_stocks_message, 
        username,
        if (size==-1) context.getString(R.string.some) else size.toString(),
        object1,
        if (size == 1) context.getString(R.string.empty_string) else context.getString(R.string.stocks_plural_suffix)),
        {}
    )

    fun showDescriptionDialog(context: Context, followSet: FollowSet) {
        showGenericDialogNoInput(
            context = context,
            title = context.getString(R.string.followset_comments_title, followSet.name, username),
            message = followSet.userComments!!,
        )
    }



}