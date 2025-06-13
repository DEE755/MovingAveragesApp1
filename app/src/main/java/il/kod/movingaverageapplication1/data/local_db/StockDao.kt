package il.kod.movingaverageapplication1.data.local_db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import il.kod.movingaverageapplication1.data.objectclass.Stock

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStock(stock: Stock)

    @Delete
    suspend fun deleteStock(vararg stock: Stock)

    @Update
    suspend fun updateStock(stock: Stock)

    @Query("SELECT DISTINCT * FROM stocks ORDER BY name ASC LIMIT :limit")
     fun getAllStocks(limit: Int) : PagingSource<Int, Stock>


    @Query("SELECT * FROM stocks WHERE id LIKE :id")
     fun getStock(id :Int) : Stock

    @Query("SELECT * FROM stocks WHERE isSelected = 1")
     fun getSelectedStocks(): LiveData<List<Stock>>


    @Query("SELECT * FROM stocks WHERE isSelected = 0")
     fun getUnselectedStocks(): LiveData<List<Stock>>

    @Query("SELECT * FROM stocks WHERE id IN (:ids) ")
    fun getStocksByIds(ids: List<Int>): List<Stock>


    @Query("SELECT * FROM stocks WHERE name LIKE '%' || :name_part || '%' " +
            "OR symbol LIKE '%' || :name_part || '%' ORDER BY name LIMIT 10000")
    suspend fun filterStockByName(name_part: String): List<Stock>?

    @Query("SELECT COUNT(*) FROM stocks")
    fun getAvailableStockCount(): LiveData<Int>

    @Query("SELECT symbol FROM stocks ORDER BY symbol DESC LIMIT 1")
    fun getLastSymbol(): String?

    @Query("UPDATE stocks SET current_price=:currentPrice WHERE symbol=:symbol")
    fun updateStockPrice(symbol: String, currentPrice: Double): Unit

    @Query("UPDATE stocks SET ma_50=:ma50, ma_25=:ma25, ma_150=:ma150, ma_200=:ma200 WHERE symbol=:symbol")
    fun updateMovingAverages(symbol: String, ma50: Double, ma25: Double, ma150: Double, ma200: Double): Unit
}

