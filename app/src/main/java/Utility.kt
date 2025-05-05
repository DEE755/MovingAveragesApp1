import android.content.Context
import androidx.appcompat.app.AlertDialog

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