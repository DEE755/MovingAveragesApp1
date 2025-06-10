package il.kod.movingaverageapplication1.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class SyncManagementRepository @Inject constructor(
    val CSDRepository: CustomServerDatabaseRepository,
    val localStockRepository: LocalStocksRepository,
    val sessionManager: SessionManager
)
{
    lateinit var setUserAnswer: String

    fun setUserFollowsStock(stock: Stock, follow: Boolean) {
        //local Update:
       CoroutineScope(Dispatchers.IO).launch {
           localStockRepository.setUserFollowsStock(stock, follow)
           //remote Update:
          CSDRepository.setUserFollowsStock(stock.symbol, follow, sessionManager.clientId)
       }
    }

}