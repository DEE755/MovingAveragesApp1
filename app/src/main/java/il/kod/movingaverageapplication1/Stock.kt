package il.kod.movingaverageapplication1

import StockItem
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Stock(
    val symbol: String,
    val name: String,
    val price: Double,
    val marketCap: Long,
    val peRatio: Double,
    val dividend: Boolean,
    val movingAverage: Double = 0.0,
) {
    companion object {
        val stockList: MutableList<Stock> = mutableListOf()
    }

    override fun toString(): String {
        return "Stock(symbol='$symbol', name='$name', price=$price, marketCap=$marketCap)"
    }

    init {
        stockList.add(this)
    }
}

@Composable
fun StockItem(stock: Stock) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = stock.symbol, fontWeight = FontWeight.Bold)
        Text(text = stock.name)
        Text(text = "Price: \$${stock.price}")
    }
}

@Composable
fun StockList(stockList: List<Stock>) {
    LazyColumn {
        items(stockList) { stock ->
            StockItem(stock = stock)
        }
    }
}

