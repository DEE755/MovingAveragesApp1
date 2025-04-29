package il.kod.movingaverageapplication1

class StockList {
    private val stockList = listOf(
        Stock(symbol = "AAPL", name = "Apple Inc.", price = 150.0, marketCap = 2_500_000_000_000, peRatio = 28.0, dividend = true),
        Stock(symbol = "GOOGL", name = "Alphabet Inc.", price = 2800.0, marketCap = 1_800_000_000_000, peRatio = 30.0, dividend = false),
        Stock(symbol = "AMZN", name = "Amazon.com Inc.", price = 3400.0, marketCap = 1_700_000_000_000, peRatio = 60.0, dividend = false),
        Stock(symbol = "MSFT", name = "Microsoft Corporation", price = 290.0, marketCap = 2_200_000_000_000, peRatio = 35.0, dividend = true)
    )
}