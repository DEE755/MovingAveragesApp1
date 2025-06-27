package il.kod.movingaverageapplication1.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import il.kod.movingaverageapplication1.NotificationsService
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.data.repository.LocalFollowSetRepository
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowSetViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    var newCreatedFollowSet : FollowSet? = null
    var followSetHasBeenCreated: Boolean = false

    //To update UI after setting an alert
    var setAlertLastValue: Double? = null
    val setAlertFollowSetID: Int? = null



    var setUserFollowsStockAnswer ="not inserted yet"

    var dummyFollowSet : FollowSet? = null

    private var notificationService:
            NotificationsService? = null

    private val _clickedFollowSet: MutableLiveData<FollowSet> = MutableLiveData()
    val clickedFollowSet get() = _clickedFollowSet

    var repository = LocalFollowSetRepository(application)

    lateinit var existingFollowSet: LiveData<List<FollowSet>>

    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("FollowSetViewModel", "onServiceConnected() CALLED")
            val binder = service as NotificationsService.LocalBinder
            notificationService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            notificationService = null
            isBound = false
        }
    }

    fun bindService() {// This function binds the service to the ViewModel but service will start before the ViewModel is created
        val context = getApplication<Application>()
        Log.d("FollowSetViewModel", "bindService() CALLED")
        val intent = Intent(context, NotificationsService::class.java)

        // link to ViewModel and service and calls onBind
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (isBound) {
            getApplication<Application>().unbindService(connection)
            isBound = false
        }
    }



    fun onItemClicked(index: Int): FollowSet? {
        return existingFollowSet.value?.get(index)
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
        return existingFollowSet.value?.get(index)
    }

    fun getAllFollowSet() {
        existingFollowSet=repository.getAllUserFollowSet()
    }

    fun updateFollowSet(followSet: FollowSet) =
        viewModelScope.launch {
            repository.updateFollowSet(followSet)
        }


    fun addNotification(followSet: FollowSet, priceThreshold: Double) {
Log.d("FollowSetViewModel", "addNotification() CALLED with priceThreshold: $priceThreshold, followSet: ${followSet.name}")
            followSet.notificationsPriceThreeshold = priceThreshold
              this.updateFollowSet(followSet)


        notificationService?.onReadyListener = object : NotificationsService.OnReadyListener {
            override fun onServiceReady(service: NotificationsService) {
                service.detectAndNotifyUser(followSet)
            }
        }

        notificationService?.let { service ->
            Log.d("FollowSetViewModel", "NotificationService is not null, checking if ready")
                    if (service.isReady) {
                        Log.d("FollowSetViewModel", "Service is ready, detecting and notifying user")
                        service.detectAndNotifyUser(followSet)
                        service.notifiableFollowSets?.add(followSet)
                    } else {
                        Log.d("FollowSetViewModel", "Service is not ready, setting onReadyListener")
                        service.onReadyListener = object : NotificationsService.OnReadyListener {
                            override fun onServiceReady(service: NotificationsService) {
                            Log.d("FollowSetViewModel", "Service is ready and it took a while to be ready")
                                service.detectAndNotifyUser(followSet)
                                service.notifiableFollowSets?.add(followSet)
                            }
                        }
                    }
                }
            }
        }

