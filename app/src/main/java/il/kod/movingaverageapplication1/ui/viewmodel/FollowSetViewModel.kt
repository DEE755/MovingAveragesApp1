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
import il.kod.movingaverageapplication1.NotificationService
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.data.repository.LocalFollowSetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowSetViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private var notificationService: NotificationService? = null

    private val _clickedFollowSet: MutableLiveData<FollowSet> = MutableLiveData()
    val clickedFollowSet get() = _clickedFollowSet

    var repository = LocalFollowSetRepository(application)

    var existingFollowSet: LiveData<List<FollowSet>> = repository.getAllFollowSet()

    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("FollowSetViewModel", "onServiceConnected() CALLED")
            val binder = service as NotificationService.LocalBinder
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
        val intent = Intent(context, NotificationService::class.java)

        // link to ViewModel and service and calls onBind
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (isBound) {
            getApplication<Application>().unbindService(connection)
            isBound = false
        }
    }

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

    fun addNotification(followSet: FollowSet, priceThreshold: Double) {

            followSet.notificationsPriceThreeshold = priceThreshold


            viewModelScope.launch {
                repository.updateFollowSet(followSet)
            }

        notificationService?.onReadyListener = object : NotificationService.OnReadyListener {
            override fun onServiceReady(service: NotificationService) {
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
                        service.onReadyListener = object : NotificationService.OnReadyListener {
                            override fun onServiceReady(service: NotificationService) {
                            Log.d("FollowSetViewModel", "Service is ready and it took a while to be ready")
                                service.detectAndNotifyUser(followSet)
                                service.notifiableFollowSets?.add(followSet)
                            }
                        }
                    }
                }
            }
        }

