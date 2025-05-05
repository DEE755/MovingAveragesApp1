import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.repository.StocksRepository

class AllStocksViewModel(application: Application) : AndroidViewModel(application) {



    private val repository = StocksRepository(application)

    val unselectedStockList : LiveData<List<Stock>> = repository.getUnselectedStocks()
    val selectedStList : LiveData<List<Stock>> = repository.getSelectedStocks()


    fun addStock(stock: Stock)=repository.addStock(stock)
    fun removeStock(stock: Stock) =repository.removeStock(stock)


    fun followStock(stock: Stock) {
        stock.let{stock.isSelected=true
            repository.updateStock(stock)}
    }

    fun unfollowStock(stock: Stock) {
        stock.let{stock.isSelected=false
            repository.updateStock(stock)}
    }

    fun onItemClicked(index: Int): Stock? {
        return selectedStList.value?.get(index)
    }



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