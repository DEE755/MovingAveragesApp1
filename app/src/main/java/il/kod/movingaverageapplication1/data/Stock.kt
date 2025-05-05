package il.kod.movingaverageapplication1.data

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stocks")
data class Stock(
    @ColumnInfo(name="symbol")
    val symbol: String,
    @ColumnInfo(name="name")
    val name: String,
    @ColumnInfo(name="price")
    val price: Double,
    @ColumnInfo(name="marketCap")
    val marketCap: Long,
    @ColumnInfo(name="peRatio")
    val peRatio: Double,
    @ColumnInfo(name="dividend")
    val dividend: Boolean,
    @ColumnInfo(name="dividendYield")
    val movingAverage: Double = 0.0,
    @ColumnInfo(name="imageUri")
    
    internal var imageUri: String? =null,
    @ColumnInfo(name="isSelected")
    var isSelected: Boolean = false

): Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id : Int =0

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



