package ac.telkomuniversity.mobapp.tt43g1.kel12.sisteminformasikependudukan.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val baseUrl = "https://api.api-ninjas.com/"
    fun getInstance(): Retrofit =
        Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}