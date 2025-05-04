package il.kod.movingaverageapplication1.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.repository.StocksRepository

class StocksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository= StocksRepository(application)
    //val stocks : LiveData<List<Stock>>? = repository.getStocks()

    fun addStock(stock: Stock)= repository.addStock(stock)


    fun deleteStock(stock: Stock)=repository.removeStock(stock)

}