package il.kod.movingaverageapplication1.data
import il.kod.movingaverageapplication1.data.models.StockService
import javax.inject.Inject

class StockRemoteDataSource @Inject constructor(
    private val stockService: StockService
) : BaseDataSource()




{}