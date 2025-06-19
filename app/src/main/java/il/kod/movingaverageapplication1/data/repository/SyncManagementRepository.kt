package il.kod.movingaverageapplication1.data.repository

import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.data.objectclass.Stock
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

    fun setUserFollowsStockData(stock: Stock, follow: Boolean)
        {
        //local Update:
       CoroutineScope(Dispatchers.IO).launch {
           localStockRepository.setUserFollowsStock(stock, follow)
           //remote Update:
          CSDRepository.setUserFollowsStock(stock.symbol, follow, sessionManager.clientId)
       }
    }

}