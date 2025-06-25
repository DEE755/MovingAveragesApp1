package il.kod.movingaverageapplication1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import il.kod.movingaverageapplication1.data.repository.SyncManagementRepository
import javax.inject.Inject

@HiltViewModel
class SyncManagementViewModel @Inject constructor(private val dataSyncRepository: SyncManagementRepository) :ViewModel() {

        var areFollowedInjectedFlag = false
        /*val mediatorFollowedStocks = MediatorLiveData<List<Stock>>().apply {
                addSource(pullAndConvertFollowedStocksFromRemote()) { value = it }
        }*/

//fun pullAndConvertFollowedStocksFromRemote() : LiveData<List<Stock>> =
  //      dataSyncRepository.pullAndConvertFollowedStocksFromRemote(this@SyncManagementViewModel.viewModelScope.coroutineContext)

        fun pullAndInjectFollowedStocksFromRemote()  =
               dataSyncRepository.pullAndConvertFollowedStocksFromRemote(viewModelScope)


    fun pullUserFollowSetsFromToRemoteDB() = //function to be observed by the UI to get the user follow sets at first launch only
        dataSyncRepository.pullUserFollowSetsFromToRemoteDB()


}