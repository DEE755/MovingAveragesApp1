package il.kod.movingaverageapplication1.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.data.local_db.StockDao
import il.kod.movingaverageapplication1.data.local_db.StocksDatabase
import il.kod.movingaverageapplication1.utils.Constants
import il.kod.movingaverageapplication1.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



class LocalStocksRepository @Inject constructor(private val application: Application) {


    private var stockDao: StockDao



    init {

        val db = StocksDatabase.getDatabase(application.applicationContext)
        stockDao = db.stocksDao()

    }



    /*fun getAllStocks(scope: CoroutineScope): LiveData<PagingData<Stock>> {
        Log.d("LocalStocksRepository", "getAllStocks called")
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { stockDao.getAllStocks()}
        ).flow
            .cachedIn(scope) // cache in given scope
            .asLiveData()
    }*/



    fun getAllStocks() =
        stockDao.getAllStocks(Constants.DATABASE_LIMIT)


    fun getSelectedStocks(): LiveData<List<Stock>> {
        return stockDao.getSelectedStocks()
    }

    fun getUnselectedStocks(): LiveData<List<Stock>> {
        return stockDao.getUnselectedStocks()
    }

    suspend fun addStock(stock: Stock) {
        stockDao.addStock(stock)
    }

    suspend fun removeStock(stock: Stock) {
        stockDao.deleteStock(stock)
    }


    suspend fun updateStock(stock: Stock) {
        Log.d("updateStock", "updatedStock: $stock")
        stockDao.updateStock(stock)
    }

    fun getStocksByIds(vararg ids: Int): List<Stock> {
        return stockDao.getStocksByIds(ids.toList())
    }

    fun getStockById(id: Int): Stock {
        return stockDao.getStockById(id)
    }

    suspend fun saveAllStocks(allStocks: Resource<List<Stock>>) {
        for (stock in allStocks.status.data ?: emptyList()) {
            stockDao.addStock(stock)
        }
    }

    suspend fun filterStocksByName(name_part: String): List<Stock>? {
        Log.d("LocalStocksRepository", "calling filterStockByname with name_part: $name_part")
        return stockDao.filterStockByName(name_part)
    }

    suspend fun setUserFollowsStock(stock: Stock, follow: Boolean) {
        Log.d("LocalStocksRepository", "setUserFollowsStock called with stock: $stock, follow: $follow")
        stock.isSelected = follow
        updateStock(stock)
    }


    fun getAvailableStockCount():LiveData<Int> = stockDao.getAvailableStockCount()

    fun getLastSymbol(): String? {
        return stockDao.getLastSymbol()
    }

    fun updateStockPrice(symbol: String, currentPrice: Double) : Unit =
        stockDao.updateStockPrice(symbol,currentPrice)


    fun updateMovingAverages(symbol: String, ma50: Double, ma25: Double, ma150: Double, ma200: Double) =
        stockDao.updateMovingAverages(symbol, ma50, ma25, ma150, ma200)


    fun observePrice(id: Int):  Flow<Double> = stockDao.observePrice(id)

}