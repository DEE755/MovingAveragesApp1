package il.kod.movingaverageapplication1.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import il.kod.movingaverageapplication1.data.repository.FollowSetRepository

class FollowSetViewModel(application: Application) : AndroidViewModel(application)
{
    var followSet = mutableListOf<Stock>()

    var clickedFollowSet : FollowSet? = null

    var repository= FollowSetRepository(application)

    fun getAllFollowSet() = repository.getAllFollowSet()

    fun onItemClicked(index: Int): FollowSet? {
        return getAllFollowSet().value?.get(index)
    }
}