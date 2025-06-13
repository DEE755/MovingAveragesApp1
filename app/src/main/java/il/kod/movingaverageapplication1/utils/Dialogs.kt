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