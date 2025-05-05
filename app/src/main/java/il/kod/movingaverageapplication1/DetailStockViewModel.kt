package il.kod.movingaverageapplication1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import il.kod.movingaverageapplication1.data.Stock

class DetailStockViewModel : ViewModel() {

    private val _clickedStock=MutableLiveData<Stock>()
    val clickedStock get()=_clickedStock

    fun setStock(stock: Stock){
        _clickedStock.value=stock
    }


}