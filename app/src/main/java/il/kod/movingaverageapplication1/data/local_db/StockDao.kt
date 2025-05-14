package il.kod.movingaverageapplication1.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import il.kod.movingaverageapplication1.data.Stock

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStock(stock: Stock)

    @Delete
    suspend fun deleteStock(vararg stock: Stock)

    @Update
    suspend fun updateStock(stock: Stock)

    @Query("SELECT * FROM stocks ORDER BY name ASC")
     fun getAllStocks() : LiveData<List<Stock>>


    @Query("SELECT * FROM stocks WHERE id LIKE :id")
     fun getStock(id :Int) : Stock

    @Query("SELECT * FROM stocks WHERE isSelected = 1")
     fun getSelectedStocks(): LiveData<List<Stock>>


    @Query("SELECT * FROM stocks WHERE isSelected = 0")
     fun getUnselectedStocks(): LiveData<List<Stock>>

    @Query("SELECT * FROM stocks WHERE id IN (:ids)")
    fun getStocksByIds(ids: List<Int>): List<Stock>

}

