package il.kod.movingaverageapplication1.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import il.kod.movingaverageapplication1.data.Stock
import java.util.concurrent.Executors

@Database(entities = [Stock::class], version = 1, exportSchema = false)
abstract class StocksDatabase : RoomDatabase() {

    abstract fun stocksDao(): StockDao

    companion object {
        @Volatile
        private var instance: StocksDatabase? = null

        fun getDatabase(context: Context): StocksDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    StocksDatabase::class.java,
                    "stocks_database"
                )
                    .allowMainThreadQueries() // dangerous, but for seeding
                    .addCallback(SeedDatabaseCallback())
                    .build()
                    .also { instance = it }
            }
        }
    }


    //TODO(INSTEAD OF HARCODED, FETCH FROM API)
    private class SeedDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Executors.newSingleThreadExecutor().execute {
                instance?.stocksDao()?.apply {
                    addStock(
                        Stock(
                            symbol = "AAPL",
                            name = "Apple Inc.",
                            price = 150.0,
                            marketCap = 2_500_000_000_000,
                            peRatio = 28.0,
                            dividend = true,
                            imageUri = "https://pngimg.com/uploads/apple_logo/apple_logo_PNG19678.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "GOOGL",
                            name = "Alphabet Inc.",
                            price = 2800.0,
                            marketCap = 1_800_000_000_000,
                            peRatio = 30.0,
                            dividend = false,
                            imageUri = "https://www.freepnglogos.com/uploads/google-logo-png/google-logo-png-suite-everything-you-need-know-about-google-newest-0.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "AMZN",
                            name = "Amazon.com Inc.",
                            price = 3400.0,
                            marketCap = 1_700_000_000_000,
                            peRatio = 60.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/amazon/amazon_PNG11.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "MSFT",
                            name = "Microsoft Corporation",
                            price = 290.0,
                            marketCap = 2_200_000_000_000,
                            peRatio = 35.0,
                            dividend = true,
                            imageUri = "https://pngimg.com/uploads/microsoft/microsoft_PNG13.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "TSLA",
                            name = "Tesla Inc.",
                            price = 800.0,
                            marketCap = 800_000_000_000,
                            peRatio = 100.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/tesla_logo/tesla_logo_PNG19.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "NFLX",
                            name = "Netflix Inc.",
                            price = 500.0,
                            marketCap = 250_000_000_000,
                            peRatio = 50.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/netflix/netflix_PNG15.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "NVDA",
                            name = "NVIDIA Corporation",
                            price = 600.0,
                            marketCap = 1_000_000_000_000,
                            peRatio = 80.0,
                            dividend = true,
                            imageUri = "https://www.stickpng.com/assets/images/58482ee4cef1014c0b5e4a75.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "FB",
                            name = "Meta Platforms Inc.",
                            price = 350.0,
                            marketCap = 900_000_000_000,
                            peRatio = 25.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/meta/meta_PNG5.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "BABA",
                            name = "Alibaba Group",
                            price = 200.0,
                            marketCap = 500_000_000_000,
                            peRatio = 20.0,
                            dividend = false,
                            imageUri = "https://upload.wikimedia.org/wikipedia/commons/5/5b/Alibaba_Group_logo.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "V",
                            name = "Visa Inc.",
                            price = 230.0,
                            marketCap = 500_000_000_000,
                            peRatio = 30.0,
                            dividend = true,
                            imageUri = "https://pngimg.com/uploads/visa/visa_PNG30.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "JPM",
                            name = "JPMorgan Chase & Co.",
                            price = 160.0,
                            marketCap = 470_000_000_000,
                            peRatio = 12.0,
                            dividend = true,
                            imageUri = "https://www.stickpng.com/assets/images/58482f67cef1014c0b5e4a81.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "DIS",
                            name = "The Walt Disney Company",
                            price = 180.0,
                            marketCap = 320_000_000_000,
                            peRatio = 25.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/disney/disney_PNG11.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "ADBE",
                            name = "Adobe Inc.",
                            price = 650.0,
                            marketCap = 300_000_000_000,
                            peRatio = 40.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/adobe/adobe_PNG1.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "ORCL",
                            name = "Oracle Corporation",
                            price = 90.0,
                            marketCap = 250_000_000_000,
                            peRatio = 15.0,
                            dividend = true,
                            imageUri = "https://pngimg.com/uploads/oracle/oracle_PNG3.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "INTC",
                            name = "Intel Corporation",
                            price = 55.0,
                            marketCap = 220_000_000_000,
                            peRatio = 10.0,
                            dividend = true,
                            imageUri = "https://pngimg.com/uploads/intel/intel_PNG13.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "BA",
                            name = "Boeing Co.",
                            price = 210.0,
                            marketCap = 120_000_000_000,
                            peRatio = 18.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/boeing/boeing_PNG6.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "CSCO",
                            name = "Cisco Systems Inc.",
                            price = 50.0,
                            marketCap = 200_000_000_000,
                            peRatio = 15.0,
                            dividend = true,
                            imageUri = "https://pngimg.com/uploads/cisco/cisco_PNG7.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "PYPL",
                            name = "PayPal Holdings Inc.",
                            price = 75.0,
                            marketCap = 90_000_000_000,
                            peRatio = 20.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/paypal/paypal_PNG22.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "CRM",
                            name = "Salesforce Inc.",
                            price = 220.0,
                            marketCap = 210_000_000_000,
                            peRatio = 35.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/salesforce/salesforce_PNG27.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "UBER",
                            name = "Uber Technologies Inc.",
                            price = 45.0,
                            marketCap = 90_000_000_000,
                            peRatio = -10.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/uber/uber_PNG7.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "SQ",
                            name = "Block Inc.",
                            price = 60.0,
                            marketCap = 40_000_000_000,
                            peRatio = -5.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/square/square_PNG9.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "SHOP",
                            name = "Shopify Inc.",
                            price = 65.0,
                            marketCap = 80_000_000_000,
                            peRatio = 100.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/shopify/shopify_PNG10.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "TWTR",
                            name = "Twitter Inc.",
                            price = 55.0,
                            marketCap = 45_000_000_000,
                            peRatio = 25.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/twitter/twitter_PNG3.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "SPOT",
                            name = "Spotify Technology S.A.",
                            price = 150.0,
                            marketCap = 30_000_000_000,
                            peRatio = -20.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/spotify/spotify_PNG15.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "SNAP",
                            name = "Snap Inc.",
                            price = 10.0,
                            marketCap = 15_000_000_000,
                            peRatio = -30.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/snapchat/snapchat_PNG33.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "ZM",
                            name = "Zoom Video Communications Inc.",
                            price = 70.0,
                            marketCap = 20_000_000_000,
                            peRatio = 50.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/zoom/zoom_PNG10.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "LYFT",
                            name = "Lyft Inc.",
                            price = 12.0,
                            marketCap = 4_000_000_000,
                            peRatio = -15.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/lyft/lyft_PNG9.png"
                        )
                    )
                    addStock(
                        Stock(
                            symbol = "DOCU",
                            name = "DocuSign Inc.",
                            price = 50.0,
                            marketCap = 10_000_000_000,
                            peRatio = -10.0,
                            dividend = false,
                            imageUri = "https://pngimg.com/uploads/docusign/docusign_PNG5.png"
                        )
                    )
                }
            }
        }
    }
}