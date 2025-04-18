package il.kod.movingaverageapplication1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity



class MainActivity : AppCompatActivity() {
    private val stockList = listOf(
        Stock(symbol = "AAPL", name = "Apple Inc.", price = 150.0, marketCap = 2_500_000_000_000, peRatio = 28.0, dividend = true),
        Stock(symbol = "GOOGL", name = "Alphabet Inc.", price = 2800.0, marketCap = 1_800_000_000_000, peRatio = 30.0, dividend = false),
        Stock(symbol = "AMZN", name = "Amazon.com Inc.", price = 3400.0, marketCap = 1_700_000_000_000, peRatio = 60.0, dividend = false),
        Stock(symbol = "MSFT", name = "Microsoft Corporation", price = 290.0, marketCap = 2_200_000_000_000, peRatio = 35.0, dividend = true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView: ListView = findViewById(R.id.listView)
        val adapter = StockAdapter(this, stockList)
        listView.adapter = adapter
    }
}

class StockAdapter(private val context: Context, private val stocks: List<Stock>) : BaseAdapter() {
    override fun getCount(): Int = stocks.size
    override fun getItem(position: Int): Any = stocks[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)
        val stock = getItem(position) as Stock

        val text1: TextView = view.findViewById(android.R.id.text1)
        val text2: TextView = view.findViewById(android.R.id.text2)

        text1.text = stock.name
        text2.text = "Price: \$${stock.price}, PE Ratio: ${stock.peRatio}"

        return view
    }
}