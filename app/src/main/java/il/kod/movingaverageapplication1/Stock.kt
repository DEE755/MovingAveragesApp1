package il.kod.movingaverageapplication1

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stock(
    val symbol: String,
    val name: String,
    val price: Double,
    val marketCap: Long,
    val peRatio: Double,
    val dividend: Boolean,
    val movingAverage: Double = 0.0,
    internal var imageUri: Uri? =null,
    internal var imagedraw: Int? =null

): Parcelable {
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



