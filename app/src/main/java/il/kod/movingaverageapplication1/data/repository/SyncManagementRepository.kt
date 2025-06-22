package il.kod.movingaverageapplication1.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.Error
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

    lateinit var setUserFollowsStockAnswer: String

    fun setUserFollowsStockData(stock: Stock, follow: Boolean)
        {
        //local Update:
       CoroutineScope(Dispatchers.IO).launch {
           localStockRepository.setUserFollowsStock(stock, follow)
           //remote Update:
          CSDRepository.setUserFollowsStock(stock.symbol, follow, sessionManager.clientId)
       }
    }

     fun pushFollowSetToRemoteDB(createdFollowSet: FollowSet, lifecycleOwner: LifecycleOwner)
    {
        CoroutineScope(Dispatchers.IO).launch {
            //add followset to local DB
            localFollowSetRepository.addFollowSet(createdFollowSet)
        }
            //add followset to remote DB
             CSDRepository.pushFollowSetToRemoteDB(createdFollowSet).observe(lifecycleOwner)
             { result ->
                 when (result.status) {
                     is Success -> {
                         setUserFollowsStockAnswer = result.status.message


                     }
                     is Error -> {
                         setUserFollowsStockAnswer = "Error adding follow set: ${result.status.message}"
                         Log.e("SyncManager", result.status.message)

                     }
                     is Loading -> {
                         setUserFollowsStockAnswer = "Adding follow set..."

                     }
                 }
                 Log.d("SyncManager", setUserFollowsStockAnswer)


             }

    }


    suspend fun pullUserFollowSetsFromToRemoteDB()=
        CSDRepository.pullUserFollowSetsFromToRemoteDB()
}