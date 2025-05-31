package il.kod.movingaverageapplication1.data.repository

import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.utils.Resource

interface LocalStockRepository
{
    fun getAllStocks(): LiveData<List<Stock>>

    fun getSelectedStocks(): LiveData<List<Stock>>


    fun getUnselectedStocks(): LiveData<List<Stock>>


    suspend fun addStock(stock: Stock): Resource<Void>


    suspend fun removeStock(stock: Stock):Resource<Void>

    suspend fun updateStock(stock: Stock)

    fun getStocksByIds(vararg ids: Int): List<Stock>
    fun userFollowsStock(stock: Stock):Resource<Void>
}