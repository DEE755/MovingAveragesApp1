package il.kod.movingaverageapplication1

data class Stock(
    val symbol: String,
    val name: String,
    val price: Double,
    val marketCap: Long,
    val peRatio: Double,
    val dividend: Boolean,
) {
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


