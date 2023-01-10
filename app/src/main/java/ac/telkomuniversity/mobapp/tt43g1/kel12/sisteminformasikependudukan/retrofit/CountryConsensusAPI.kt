package ac.telkomuniversity.mobapp.tt43g1.kel12.sisteminformasikependudukan.retrofit

import ac.telkomuniversity.mobapp.tt43g1.kel12.sisteminformasikependudukan.response_data.ResponseDataValue
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface CountryConsensusAPI {
    @GET
    suspend fun getCountryConsensus(@Url url: String): Response<List<ResponseDataValue>>
}