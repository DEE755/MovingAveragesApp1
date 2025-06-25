package il.kod.movingaverageapplication1.utils

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import il.kod.movingaverageapplication1.R
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


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

fun showNotificationDialog(
    context: Context,
    title: String,
    message: String,
    onYes: () -> Unit,
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton("I Understand") { dialog, _ ->
        onYes()
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
    onNumberEntered: (Double) -> Unit,
    followSetName: String,
) {
    val editText = EditText(context).apply {
        inputType = android.text.InputType.TYPE_CLASS_NUMBER
    }
    editText.hint = context.getString(R.string.set_threshold_alert, followSetName)
    AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.set_threshold_alert, followSetName))
        .setView(editText)
        .setMessage(context.getString(R.string.alert_threshold_message))
        .setPositiveButton(context.getString(R.string.ok)) { dialog, _ ->
            val number = editText.text.toString()
            if (number.isNotBlank() && number.isDigitsOnly()) {
                onNumberEntered(number.toDouble())
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



suspend fun showCustomQuestionInputDialog(
    context: Context,
    title: String,
    message: String
): String? = suspendCancellableCoroutine { continuation ->
    val editText = EditText(context).apply {
        hint = message
    }

    android.app.AlertDialog.Builder(context)
        .setTitle(title)
        .setView(editText)
        .setPositiveButton("Ask") { dialog, _ ->
            val input = editText.text.toString()
            if (input.isNotBlank()) {
                continuation.resume(input)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.valid_number_prompt),
                    Toast.LENGTH_SHORT
                ).show()
                continuation.resume(null)
            }
            dialog.dismiss()
        }
        .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
            continuation.resume(null)
            dialog.dismiss()
        }
        .setOnCancelListener {
            continuation.resume(null)
        }
        .show()
}

fun showGenericDialogInput(
    context: Context,
    title: String,
    positiveButtonText: String = "Ok",
    negativeButtonText: String = "Cancel",
    invalidInputMessage: String = "Enter a valid input",
    message: String = "Please enter your input here",
    onInputEntered: (String) -> Unit
) {
    val editText = EditText(context)
    editText.hint = message

    AlertDialog.Builder(context)
        .setTitle(title)
        .setView(editText)
        .setPositiveButton(positiveButtonText) { dialog, _ ->
            val name = editText.text.toString()
            if (name.isNotBlank()) {
                onInputEntered(name)
            } else {
                Toast.makeText(context, invalidInputMessage, Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        .setNegativeButton(negativeButtonText) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
    }

fun showLargeGenericDialogInput(
    context: Context,
    title: String,
    positiveButtonText: String = "Ok",
    negativeButtonText: String = "Cancel",
    invalidInputMessage: String = "Enter a valid input",
    message: String = "Please enter your input here",
    onInputEntered: (String) -> Unit
) {
    val editText = EditText(context)
    editText.hint = message

    val dialog=AlertDialog.Builder(context)
        .setTitle(title)
        .setView(editText)
        .setPositiveButton(positiveButtonText) { dialog, _ ->
            val name = editText.text.toString()
            if (name.isNotBlank()) {
                onInputEntered(name)
            } else {
                Toast.makeText(context, invalidInputMessage, Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        .setNegativeButton(negativeButtonText) { dialog, _ ->
            dialog.dismiss()
        }.create()

        dialog.show()

        dialog.window?.setLayout(1000, 1200)
}


    fun showGenericDialogNoInput(
        context: Context,
        title: String,
        positiveButtonText: String = "Close",
        message: String = "Please enter your input here"
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
}