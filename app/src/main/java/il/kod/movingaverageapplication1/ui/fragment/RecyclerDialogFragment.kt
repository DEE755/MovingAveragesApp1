package il.kod.movingaverageapplication1.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.databinding.FragmentRecyclerPopUpBinding

class RecyclerDialogFragment(
    private val items: List<String>,
    private val onItemClick: (String, RecyclerDialogFragment) -> Unit
) : DialogFragment() {

    private var _binding: FragmentRecyclerPopUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Properly initialize _binding
        _binding = FragmentRecyclerPopUpBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = SimpleAdapter(items){selectedItem ->onItemClick(selectedItem, this)}

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.75).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.returnButton.setOnClickListener { dialog.dismiss() }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class SimpleAdapter(
        private val items: List<String>,
        private val onClick: (String) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
            val text: android.widget.TextView = itemView.findViewById(R.id.action_title)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.actions_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = items[position]
            holder.itemView.setOnClickListener { onClick(items[position]) }
        }

        override fun getItemCount(): Int = items.size
    }
}