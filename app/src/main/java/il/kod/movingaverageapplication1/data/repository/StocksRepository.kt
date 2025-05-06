package il.kod.movingaverageapplication1.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.local_db.StockDao
import il.kod.movingaverageapplication1.data.local_db.StocksDatabase

class StocksRepository(application: Application)
{
    //TODO(FETCH DATA FROM API instead OF HARDCODED)

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

    fun addStock(stock: Stock)=stockDao.addStock(stock)

    fun removeStock(stock: Stock){
        stockDao.deleteStock(stock)
    }
    fun updateStock(stock: Stock) {
        stockDao.updateStock(stock)
    }

    fun getStocksByIds(vararg ids: Int): List<Stock> {
        return stockDao.getStocksByIds(ids.toList())
    }

}