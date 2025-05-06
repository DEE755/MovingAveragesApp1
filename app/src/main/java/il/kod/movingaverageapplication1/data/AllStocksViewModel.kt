import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.repository.StocksRepository

class AllStocksViewModel(application: Application) : AndroidViewModel(application) {



    private val repository = StocksRepository(application)

    val unselectedStock : LiveData<List<Stock>> = repository.getUnselectedStocks()
    val followedStocks : LiveData<List<Stock>> = repository.getSelectedStocks()


        //for the future API, for now the initial stock data is hardcoded into the database
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
        return followedStocks.value?.get(index)
    }

    fun getStocksAt(vararg index: Int): List<Stock> {
        val stocks = mutableListOf<Stock>()
        index.forEach{
            followedStocks.value?.get(it)?.let {
                stocks.add(it)
            }
        }
        return stocks
    }

    fun getStocksByIds(vararg ids: Int): List<Stock> {
        return repository.getStocksByIds(*ids)
    }


}