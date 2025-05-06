package il.kod.movingaverageapplication1.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import il.kod.movingaverageapplication1.data.repository.FollowSetRepository

class FollowSetViewModel(application: Application) : AndroidViewModel(application) {

    private val _clickedFollowSet: MutableLiveData<FollowSet> = MutableLiveData()
    val clickedFollowSet get()=_clickedFollowSet

    var repository = FollowSetRepository(application)

    var existingFollowSet: LiveData<List<FollowSet>> = repository.getAllFollowSet()

    fun getAllFollowSet() = existingFollowSet

    fun onItemClicked(index: Int): FollowSet? {
        return getAllFollowSet().value?.get(index)
    }

    fun addFollowSet(followSet: FollowSet) {
        repository.addFollowSet(followSet)

    }

    fun removeFollowSet(followSet: FollowSet) {
        repository.removeStock(followSet)
    }

    fun getFollowSetAt(index: Int): FollowSet? {
        return getAllFollowSet().value?.get(index)
    }




}