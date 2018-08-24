package assignment.briskon.com.briskonassignment.Network

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var retrofit: Retrofit? = null
    private val BaseUrl: String = "http://services.groupkt.com/"

    val apiClient: Retrofit?
    get() {
        if (retrofit == null) {
            Log.i("ApiClient", "BaseUrl: ")
            retrofit = Retrofit.Builder()
                    .baseUrl(BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit
    }
}