package il.kod.movingaverageapplication1.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import il.kod.movingaverageapplication1.data.repository.LocalFollowSetRepository
import kotlinx.coroutines.launch

class FollowSetViewModel(application: Application) : AndroidViewModel(application) {

    private val _clickedFollowSet: MutableLiveData<FollowSet> = MutableLiveData()
    val clickedFollowSet get()=_clickedFollowSet

    var repository = LocalFollowSetRepository(application)

    var existingFollowSet: LiveData<List<FollowSet>> = repository.getAllFollowSet()

    fun getAllFollowSet() = existingFollowSet

    fun onItemClicked(index: Int): FollowSet? {
        return getAllFollowSet().value?.get(index)
    }

    fun addFollowSet(followSet: FollowSet) {
        viewModelScope.launch {
            repository.addFollowSet(followSet)
        }

    }

    fun removeFollowSet(followSet: FollowSet) {
        viewModelScope.launch {
            repository.deleteFollowSet(followSet)
        }
    }

    fun getFollowSetAt(index: Int): FollowSet? {
        return getAllFollowSet().value?.get(index)
    }




}