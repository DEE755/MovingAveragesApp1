package il.kod.movingaverageapplication1.data.objectclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stocks", indices = [Index(value = ["symbol"], unique = true)])
//PAY ATTENTION THE NAMES FITS THE DATABASE TABLE NAMES
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
    var isSelected: Boolean = false,

    @ColumnInfo(name="isDirty")
    var isDirty: Boolean = false

): Parcelable {
    @PrimaryKey(autoGenerate = true)// TODO(FOR NOW IT MAKES TROUBLES, ONLY 1 STOCK IS IN THE RECYCLER IF PUTTING =FALSE)
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