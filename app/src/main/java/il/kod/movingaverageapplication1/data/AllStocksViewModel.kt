import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.repository.LocalStocksRepository
import kotlinx.coroutines.launch

class AllStocksViewModel(application: Application) : AndroidViewModel(application) {



    private val repository = LocalStocksRepository(application)

    val unselectedStock : LiveData<List<Stock>> = repository.getUnselectedStocks()
    val followedStocks : LiveData<List<Stock>> = repository.getSelectedStocks()


        //for the future API, for now the initial stock data is hardcoded into the database
    fun addStock(stock: Stock){viewModelScope.launch {  repository.addStock(stock)}}
    fun removeStock(stock: Stock) {viewModelScope.launch{repository.removeStock(stock)}}


    fun followStock(stock: Stock) {
        viewModelScope.launch{stock.let{stock.isSelected=true
            repository.updateStock(stock)}}
    }

    fun unfollowStock(stock: Stock) {
        viewModelScope.launch{
        stock.let{stock.isSelected=false
            repository.updateStock(stock)}}
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

    fun getStocksByIds(vararg ids: Int): List<Stock>{
    var stocks= emptyList<Stock>()
    viewModelScope.launch {
         stocks=repository.getStocksByIds(*ids)
    }
        return stocks
    }



}