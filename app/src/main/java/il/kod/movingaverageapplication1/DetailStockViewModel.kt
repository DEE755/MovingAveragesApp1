package il.kod.movingaverageapplication1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import il.kod.movingaverageapplication1.data.Stock

class DetailStockViewModel : ViewModel() {

    private val _chosenStock=MutableLiveData<Stock>()
    val chosenStock get()=_chosenStock

    fun setStock(stock: Stock){
        _chosenStock.value=stock
    }


}