        package il.kod.movingaverageapplication1.data.repository

        import android.util.Log
        import android.widget.Toast
        import androidx.core.content.ContentProviderCompat.requireContext
        import il.kod.movingaverageapplication1.data.BaseDataSource
        import il.kod.movingaverageapplication1.data.objectclass.Stock
        import il.kod.movingaverageapplication1.data.models.AuthResponse
        import il.kod.movingaverageapplication1.data.repository.retrofit.CustomServerDatabaseServiceNoToken
        import il.kod.movingaverageapplication1.data.repository.retrofit.CustomServerDatabaseServiceWithToken
        import il.kod.movingaverageapplication1.utils.HttpMethod
        import il.kod.movingaverageapplication1.utils.Resource
        import il.kod.movingaverageapplication1.utils.formatText
        import retrofit2.HttpException
        import retrofit2.Response
        import javax.inject.Inject



        //used to call the cloud database service and envelop the data inside a pattern with success/failure responses
        class CustomServerDatabaseRemoteDataSource @Inject constructor(
            private val CSDPublicService: CustomServerDatabaseServiceNoToken,
            private val CSDPrivateService: CustomServerDatabaseServiceWithToken,

        ) : BaseDataSource() {
            suspend fun login(username: String, password: String):Resource<AuthResponse> =
                getResult({ CSDPublicService.login(username, password) })


            suspend fun signUp(username: String, password: String): Resource<AuthResponse> =
                getResult({
                    try {
                        Log.d("CustomServerDatabaseRemoteDataSource", "signUp started")
                        val response = CSDPublicService.sign_up(username, password)
                        Log.d("CustomServerDatabaseRemoteDataSource", "sign_up response: $response")
                        if (response.body() == null) {
                            Log.e("CustomServerDatabaseRemoteDataSource", "Response body is null")
                        } else {
                            Log.d(
                                "CustomServerDatabaseRemoteDataSource",
                                "sign_up response body: ${response.body()}"
                            )
                        }
                        response
                    } catch (e: Exception) {
                        Log.e(
                            "CustomServerDatabaseRemoteDataSource",
                            "Exception during signUp: ${e.message}",
                            e
                        )
                        throw e
                    }
                }, HttpMethod.POST)

            suspend fun getAllStocks(): Resource<List<Stock>> =
                getResult({
                    CSDPublicService.getAllStocks()
                })

            suspend fun getStocksStartingFromSymbol(symbol: String): Resource<List<Stock>> {
                Log.d(
                    "CustomServerDatabaseRemoteDataSource",
                    "getStocksStartingFromSymbol called with symbol: $symbol"
                )
                return getResult({

                    CSDPublicService.getStocksStartingFromSymbol(symbol)
                })
            }


            suspend fun getNbOfStocksInRemoteDB(): Int =
               CSDPublicService.getNbOfStocksInRemoteDB().body()?.count?: 0





        /*    suspend fun getStockPrice(stock: Stock): Resource<String> =
                {return }*/
            suspend fun userFollowsStock(stockSymbol: String, follow: Boolean, clientId: Int) =
                try {
                    val response = CSDPrivateService.setUserFollowsStock(stockSymbol)
                    Log.d("CustomServerDatabaseRemoteDataSource", "setUserFollowsStock response: $response")
                }
                catch (e: HttpException) {
                    Log.e("CustomServerDatabaseRemoteDataSource", "HTTP error: ${e.code()} - ${e.message()}")

                } catch (e: Exception) {
                    Log.e("CustomServerDatabaseViewModel", "Unexpected error: ${e.message}")
                }



        suspend fun getFollowedStockPrice(): Resource<List<Stock>> =
           getResult({CSDPrivateService.getFollowedStockPrice()}, HttpMethod.GET)


            suspend fun getFollowedMovingAverages(): Resource<List<Stock>> =
                getResult({CSDPrivateService.getFollowedMovingAverages()}, HttpMethod.GET)

            suspend fun askAI(stock: Stock): Resource<String> = getResult({
                val question = "What can you tell me about the stock ${stock.name}?"
                val response = CSDPublicService.ask_ai(question)

                val reply = formatText(response.body()?.reply ?: "No reply found")
                Response.success(reply)
            }, HttpMethod.POST)

        }



