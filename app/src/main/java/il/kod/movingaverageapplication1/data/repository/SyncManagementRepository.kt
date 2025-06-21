package il.kod.movingaverageapplication1.data.repository

import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.data.objectclass.Stock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class SyncManagementRepository @Inject constructor(
    val CSDRepository: CustomServerDatabaseRepository,
    val localStockRepository: LocalStocksRepository,
    val sessionManager: SessionManager,
    val localFollowSetRepository: LocalFollowSetRepository
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

     fun pushFollowSetToRemoteDB(createdFollowSet: FollowSet)
    {
        CoroutineScope(Dispatchers.IO).launch {
            //add followset to local DB
            localFollowSetRepository.addFollowSet(createdFollowSet)

            //add followset to remote DB
            CSDRepository.pushFollowSetToRemoteDB(createdFollowSet)

        }
    }


    suspend fun pullUserFollowSetsFromToRemoteDB()=
        CSDRepository.pullUserFollowSetsFromToRemoteDB()
}