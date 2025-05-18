package il.kod.movingaverageapplication1.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.local_db.StockDao
import il.kod.movingaverageapplication1.data.local_db.StocksDatabase

class LocalStocksRepository(application: Application)
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
       stockDao.updateStock(stock)
    }

     fun getStocksByIds(vararg ids: Int): List<Stock> {
        return stockDao.getStocksByIds(ids.toList())
    }

}