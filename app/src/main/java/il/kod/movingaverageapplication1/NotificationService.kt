package il.kod.movingaverageapplication1

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.data.repository.LocalFollowSetRepository
import javax.inject.Inject
import il.kod.movingaverageapplication1.data.repository.LocalStocksRepository
import il.kod.movingaverageapplication1.ui.NotificationHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationService : Service()
{
    @Inject
    lateinit var baseRepositoryAllStocks: LocalStocksRepository

    @Inject
    lateinit var followSetRepository: LocalFollowSetRepository

    @Inject
    lateinit var notificationsFragment: NotificationHandler

    private val binder = LocalBinder()

    var notifiableFollowSets: MutableList<FollowSet>? = null // This will be the list of follow sets that we want to notify the user about


    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)


    var isReady = false
        private set

    interface OnReadyListener {
        fun onServiceReady(service: NotificationService)
    }

    var onReadyListener: OnReadyListener? = null
        set(value) {
            field = value
            if (isReady) {
                value?.onServiceReady(this)
            }
        }




    inner class LocalBinder : Binder() {
        fun getService(): NotificationService = this@NotificationService
    }

    // This service will be responsible for watching and detecting when a stock/followset reaches a certain threshold --TODO (supposed to work if instancied)--> can relaunch the onstartCommand if added new notifiableFollowSet or individual detect and notify func launch


    override fun onCreate() {
        super.onCreate()

        Log.d("NotificationService", "Service Created and is ready ==true ")
        isReady = true

        onReadyListener?.onServiceReady(this)

    }




    override fun onBind(p0: Intent?): IBinder?
    {
        Log.d("NotificationService", "onBind called, returning binder")

        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        serviceScope.launch {

            val tempImmutableFetch = followSetRepository.getFollowSetWithNotifications()
            tempImmutableFetch.let{notifiableFollowSets = it.toMutableList() }// Convert to MutableList if not null
            notifiableFollowSets?.let{
                Log.d("NotificationService", "Detected Notifiable Follow Sets: ${it.size}")
                it.forEach { followSet->
                    Log.d("NotificationService", "Monitoring Follow Set: ${followSet.name} with threshold ${followSet.notificationsPriceThreeshold}")
                    detectAndNotifyUser(followSet)

                }
            }
        }
        return START_STICKY
    }



    fun detectAndNotifyUser(followSet: FollowSet)
    {
        Log.d("NotificationService", "Starting to monitor Follow Set: ${followSet.name} of size ${followSet.set_ids.size} with threshold ${followSet.notificationsPriceThreeshold}")

                 followSet.set_ids.forEach { id ->
                     CoroutineScope(Dispatchers.IO).launch {
                         try {
                             Log.d("NotificationService", "Monitoring stock ID: $id")
                             baseRepositoryAllStocks.observePrice(id)
                                 .filter { stockPrice -> stockPrice < followSet.notificationsPriceThreeshold && stockPrice > 0.0 }
                                 .distinctUntilChanged()
                                 .collect {
                                     notificationsFragment.showNotification(
                                         "Price Alert!",
                                         "${followSet.name} has reached the threshold price of ${followSet.notificationsPriceThreeshold} for stock: ${
                                             baseRepositoryAllStocks.getStockById(id).name
                                         } with current price: $it"
                                     )
                                 }

                         } catch (e: Exception) {
                             Log.e("NotificationService", "Error monitoring stock: $id", e)
                         }
                     }
                 }
    }





}