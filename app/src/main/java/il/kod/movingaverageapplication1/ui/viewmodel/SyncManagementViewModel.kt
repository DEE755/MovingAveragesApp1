package il.kod.movingaverageapplication1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import il.kod.movingaverageapplication1.data.models.AdapterBackIDForGson
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.data.repository.SyncManagementRepository
import il.kod.movingaverageapplication1.utils.Resource
import javax.inject.Inject

@HiltViewModel
class SyncManagementViewModel @Inject constructor(private val dataSyncRepository: SyncManagementRepository) :ViewModel() {


    lateinit var followsetsFromRemote: LiveData<Resource<List<FollowSet>>>

    lateinit var answerFromRemoteFollowSetInsertId: LiveData<Resource<AdapterBackIDForGson>>


    fun pullAndInjectFollowedStocksFromRemote() =
        dataSyncRepository.pullAndConvertFollowedStocksFromRemote(viewModelScope)


    fun pullUserFollowSetsFromToRemoteDB() { //function to be observed by the UI to get the user follow sets at first launch only
        followsetsFromRemote = dataSyncRepository.pullUserFollowSetsFromToRemoteDB()

    }


    fun setUserUnfollowsFollowSet(followSet: FollowSet) {
        dataSyncRepository.setUserUnfollowsFollowSet(followSet)
    }

    fun saveNewFollowSet(createdFollowSet: FollowSet) {
        answerFromRemoteFollowSetInsertId =
            dataSyncRepository.pushFollowSetToRemoteDB(createdFollowSet)
    }

}