import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.repository.StocksRepository

class AllStocksViewModel(application: Application) : AndroidViewModel(application) {



    private val repository = StocksRepository(application)

    val unselectedStockList : LiveData<List<Stock>> = repository.getUnselectedStocks()

    fun addStock(stock: Stock?)=stock.let{stock?.isSelected=false}
    fun removeStock(stock: Stock?) =stock.let{stock?.isSelected=true}



/*
    fun addStock(stock: Stock?) {
        stock?.let {
            stock.isSelected=false
            stockList.value?.add(it)
            stockList.value = stockList.value // Trigger LiveData update
        }
    }

    fun removeStock(stock: Stock?) {
        stock?.let {
            stockList.value?.remove(it)
            stockList.value =stockList.value // Trigger LiveData update
        }
    }

            }*/
}