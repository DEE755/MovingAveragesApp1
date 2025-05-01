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
    fun addStock(stock: Stock)

    @Delete
    fun deleteStock(vararg stock: Stock)

    @Update
    fun updateStock(stock: Stock)

    @Query("SELECT * FROM stocks ORDER BY name ASC")
    fun getStocks() : LiveData<List<Stock>>

    @Query("SELECT * FROM stocks WHERE id LIKE :id")
    fun getItem(id :Int) : Stock





}

