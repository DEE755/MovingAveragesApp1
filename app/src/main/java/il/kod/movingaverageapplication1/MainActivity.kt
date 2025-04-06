package il.kod.movingaverageapplication1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val stock1: Stock = Stock(
        symbol = "AAPL",
        name = "Apple Inc.",
        price = 150.0,
        marketCap = 2_500_000_000_000,
        peRatio = 28.0,
        dividend = true
    )

    private val stock2: Stock = Stock(
        symbol = "GOOGL",
        name = "Alphabet Inc.",
        price = 2800.0,
        marketCap = 1_800_000_000_000,
        peRatio = 30.0,
        dividend = false
    )

    private val stock3: Stock = Stock(
        symbol = "AMZN",
        name = "Amazon.com Inc.",
        price = 3400.0,
        marketCap = 1_700_000_000_000,
        peRatio = 60.0,
        dividend = false
    )

    private val stock4: Stock = Stock(
        symbol = "MSFT",
        name = "Microsoft Corporation",
        price = 290.0,
        marketCap = 2_200_000_000_000,
        peRatio = 35.0,
        dividend = true
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}