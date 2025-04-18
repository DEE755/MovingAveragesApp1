import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import il.kod.movingaverageapplication1.Stock

@Composable
fun StockItem(stock: Stock) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = stock.symbol, fontWeight = FontWeight.Bold)
        Text(text = stock.name)
        Text(text = "Price: \$${stock.price}")
    }
}