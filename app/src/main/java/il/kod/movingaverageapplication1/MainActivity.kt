package il.kod.movingaverageapplication1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val stock1 = Stock(
        name = "AAPL",
        price = 150.0,
        movingAverage = 145.0
    )

    private val stock2 = Stock(
        name = "GOOGL",
        price = 2800.0,
        movingAverage = 2750.0
    )

    private val stock3 = Stock(
        name = "AMZN",
        price = 3400.0,
        movingAverage = 3350.0
    )

    private val stock4 = Stock(
        name = "MSFT",
        price = 299.0,
        movingAverage = 295.0
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