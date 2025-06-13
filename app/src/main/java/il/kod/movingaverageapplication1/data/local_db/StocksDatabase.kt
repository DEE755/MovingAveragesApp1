package il.kod.movingaverageapplication1.data.local_db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import il.kod.movingaverageapplication1.utils.IntListConverter
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.data.objectclass.Stock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext


@Database(entities = [Stock::class, FollowSet::class], version = 1, exportSchema = true)
@TypeConverters(IntListConverter::class)
abstract class StocksDatabase : RoomDatabase(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO



    abstract fun stocksDao(): StockDao

    abstract fun followSetDao(): FollowSetDao

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

                    .build()
                    .also { instance = it }
            }
        }
    }



}