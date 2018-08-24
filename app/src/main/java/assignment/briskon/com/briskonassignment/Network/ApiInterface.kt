package assignment.briskon.com.briskonassignment.Network

import assignment.briskon.com.briskonassignment.model.ApiResponse
import assignment.briskon.com.briskonassignment.model.RestResponse
import assignment.briskon.com.briskonassignment.model.StateApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

    @GET("/country/get/all")
    fun getAllCountry(): Call<ApiResponse>


    @GET("/state/get/{Id}//all")
    fun getAllState(@Path("Id") countryCode: String ): Call<StateApiResponse>

}
