package il.kod.movingaverageapplication1.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.local_db.StockDao
import il.kod.movingaverageapplication1.data.local_db.StocksDatabase
import javax.inject.Inject



class LocalStocksRepository @Inject constructor(application: Application)
{


    private var stockDao : StockDao


    init {

        val db = StocksDatabase.getDatabase(application.applicationContext)
        stockDao = db.stocksDao()

    }


     fun getAllStocks(): LiveData<List<Stock>> {
        return stockDao.getAllStocks()
    }

     fun getSelectedStocks(): LiveData<List<Stock>> {
        return stockDao.getSelectedStocks()
    }

     fun getUnselectedStocks(): LiveData<List<Stock>> {
        return stockDao.getUnselectedStocks()
    }

    suspend fun addStock(stock: Stock){stockDao.addStock(stock)}

    suspend fun removeStock(stock: Stock){
        stockDao.deleteStock(stock)
    }


    suspend fun updateStock(stock: Stock) {
        Log.d("updateStock", "updatedStock: $stock")
       stockDao.updateStock(stock)
    }

     fun getStocksByIds(vararg ids: Int): List<Stock> {
        return stockDao.getStocksByIds(ids.toList())
    }

    suspend fun saveAllStocks(allStocks: List<Stock>) {
        for (stock in allStocks) {
            stockDao.addStock(stock)
        }
    }

    suspend fun filterStocksByName(name_part: String): List<Stock>? {
        Log.d("LocalStocksRepository", "calling filterStockByname with name_part: $name_part")
        return stockDao.filterStockByName(name_part)
    }

   suspend fun setUserFollowsStock(stock: Stock, follow: Boolean)
    {
     stock.isSelected=follow
        updateStock(stock)
    }



}