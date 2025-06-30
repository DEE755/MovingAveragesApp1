        package il.kod.movingaverageapplication1.data.repository

        import android.util.Log
        import il.kod.movingaverageapplication1.SessionManager
        import il.kod.movingaverageapplication1.data.BaseDataSource
        import il.kod.movingaverageapplication1.data.models.AdapterBackIDForGson
        import il.kod.movingaverageapplication1.data.models.AdapterStockIdGson
        import il.kod.movingaverageapplication1.data.objectclass.Stock
        import il.kod.movingaverageapplication1.data.models.AuthResponse
        import il.kod.movingaverageapplication1.data.objectclass.FollowSet
        import il.kod.movingaverageapplication1.data.repository.retrofit.CustomServerDatabaseServiceNoToken
        import il.kod.movingaverageapplication1.data.repository.retrofit.CustomServerDatabaseServiceWithToken
        import il.kod.movingaverageapplication1.utils.Constants
        import il.kod.movingaverageapplication1.utils.HttpMethod
        import il.kod.movingaverageapplication1.utils.Resource
        import il.kod.movingaverageapplication1.utils.formatText
        import retrofit2.HttpException
        import retrofit2.Response
        import javax.inject.Inject



        //used to call the cloud database service and envelop the data inside a pattern with success/failure responses
        class CustomServerDatabaseRemoteDataSource @Inject constructor(
            private val CSDPublicService: CustomServerDatabaseServiceNoToken,
            private val CSDPrivateService: CustomServerDatabaseServiceWithToken

        ) : BaseDataSource() {

            private val promptPrecision : String ="Please provide a detailed answer with relevant data and insights. If nothing in this prompt is not at all related to the stock(s), don't answer about it, just kindly answer something like 'This question is not related to this stock or company, I can only answer questions about stocks or companies'."

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
                    CSDPublicService.getAllStocks(Constants.DATABASE_LIMIT)
                })




            suspend fun getNbOfStocksInRemoteDB(): Int =
               CSDPublicService.getNbOfStocksInRemoteDB().body()?.count?: 0





        /*    suspend fun getStockPrice(stock: Stock): Resource<String> =
                {return }*/
            suspend fun userFollowsStock(stockSymbol: String, follow: Boolean) =
                try {
                    val response : Any

                    if (follow) {
                         response = CSDPrivateService.setUserFollowsStock(stockSymbol)

                    }
                    else{ response = CSDPrivateService.setUserUnfollowsStock(stockSymbol)

                    }
                    Log.d(
                        "CustomServerDatabaseRemoteDataSource",
                        "setUserFollowsStock response: $response"
                    )
                }
                catch (e: HttpException) {
                    Log.e("CustomServerDatabaseRemoteDataSource", "HTTP error: ${e.code()} - ${e.message()}")

                } catch (e: Exception) {
                    Log.e("CustomServerDatabaseViewModel", "Unexpected error: ${e.message}")
                }



                suspend fun setUserUnfollowsFollowSet(followSet: FollowSet) {
                    val response = CSDPrivateService.setUserUnfollowsFollowSet(followSet.back_id)
                    Log.d("CustomServerDatabaseRemoteDataSource", "setUserUnfollowsFollowSet response: $response")
                }

            suspend fun pushFollowSetToRemoteDB(createdFollowSet: FollowSet) : Resource<AdapterBackIDForGson> =
                getResult({CSDPrivateService.pushFollowSetToRemoteDB(createdFollowSet)}, HttpMethod.POST)

            suspend fun pullUserFollowSetsFromToRemoteDB() : Resource<List<FollowSet>> =
                getResult({CSDPrivateService.pullUserFollowSetFromRemoteDB()})

            suspend fun pullUserFollowedStockFromRemoteDB() : Resource<List<AdapterStockIdGson>> =
                getResult({CSDPrivateService.pullUserFollowedStockFromRemoteDB()})

        suspend fun getFollowedStockPrice(): Resource<List<Stock>> =
           getResult({CSDPrivateService.getFollowedStockPrice()}, HttpMethod.GET)


            suspend fun getFollowedMovingAverages(): Resource<List<Stock>> =
                getResult({CSDPrivateService.getFollowedMovingAverages()}, HttpMethod.GET)

            suspend fun askAI(stock: Stock, question: String): Resource<String> = getResult({

                val completeQuestion = "$question about the stock ${stock.name}?"
                val response = CSDPublicService.ask_ai(completeQuestion+"\n" +promptPrecision)

                val reply = formatText(response.body()?.reply ?: "No reply found")
                Response.success(reply)
            }, HttpMethod.POST)


            suspend fun askAIFollowSet(vararg allStockNames: String, question: String): Resource<String> = getResult({


                val completeQuestion = "$question concerning those following stocks ${allStockNames.joinToString(",")}."

                Log.d("CustomServerDatabaseRemoteDataSource", "askAIFollowSet called with question: $completeQuestion")
                val response = CSDPublicService.ask_ai(completeQuestion+"\n" +promptPrecision)

                val reply = formatText(response.body()?.reply ?: "No reply found")
                Response.success(reply)
            }, HttpMethod.POST)




        }



