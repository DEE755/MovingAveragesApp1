package il.kod.movingaverageapplication1.data.repository

import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.data.objectclass.Stock
import javax.inject.Inject


class SyncManagementRepository @Inject constructor(
    val CSDRepository: CustomServerDatabaseRepository, val localStockRepository: LocalStocksRepository)
{

    suspend fun setUserFollowsStock(stock: Stock, follow: Boolean) {
        //local Update:
        localStockRepository.setUserFollowsStock(stock, follow)
        //remote Update:
        CSDRepository.setUserFollowsStock(stock, follow)
    }



}