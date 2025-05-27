package il.kod.movingaverageapplication1.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stocks")

data class Stock(
    @ColumnInfo(name="currency")
    val currency: String,
    @ColumnInfo(name="name")
    val name: String,

    @ColumnInfo(name="symbol")
    val symbol: String,

    @ColumnInfo(name="current_price")
    val current_price: Double,
  //moving averages
    @ColumnInfo(name="ma_50")
    val ma_50: Double,
    @ColumnInfo(name="ma_25")
    val ma_25: Double,
    @ColumnInfo(name="ma_150")
    val ma_150: Double,
    @ColumnInfo(name="ma_200")
    val ma_200: Double,

    @ColumnInfo(name="logo_url")
    internal var logo_url: String? =null,
    @ColumnInfo(name="isSelected")
    var isSelected: Boolean = false


): Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    companion object {
        val stockList: MutableList<Stock> = mutableListOf()
    }

    override fun toString(): String {
        return "Stock(symbol='$symbol', name='$name', price=$current_price)"
    }

    init {
        stockList.add(this)
    }
}



