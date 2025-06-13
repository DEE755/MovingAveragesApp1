package il.kod.movingaverageapplication1.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import il.kod.movingaverageapplication1.data.objectclass.AiQuestion
import il.kod.movingaverageapplication1.databinding.ItemLayoutBinding

import il.kod.movingaverageapplication1.databinding.QuestionLayoutBinding
import il.kod.movingaverageapplication1.ui.fragment.QuestionRecyclerAdapterFragment.QuestionViewHolder


class QuestionRecyclerAdapterFragment (private var questions: List<AiQuestion>, private val callBack: QuestionListener, private val glide: RequestManager) : RecyclerView.Adapter<QuestionViewHolder>()
{


    override fun getItemCount() = questions.size


    fun updateData(newQuestions: List<AiQuestion>) {
        questions = newQuestions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int
    ) = QuestionViewHolder(
        QuestionLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )


    override fun onBindViewHolder(
        holder: QuestionViewHolder,
        position: Int
    ) {
        holder.bind(questions[position])

    }




    interface QuestionListener {
            fun onItemClicked(index: Int)
            fun onItemLongClicked(index: Int)
        }

    inner class QuestionViewHolder(private val binding: QuestionLayoutBinding) :
            RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {
            init {

                binding.root.setOnClickListener(this)
                binding.root.setOnLongClickListener(this)

            }

            override fun onClick(p0: View?) {
                callBack.onItemClicked(adapterPosition)


            }

            override fun onLongClick(p0: View?): Boolean {
                callBack.onItemLongClicked(adapterPosition)
                return false
            }


            fun bind(question: AiQuestion) {

                binding.questionTitle.text = question.name
                binding.questionText.text = question.question

                }


            }

        }





