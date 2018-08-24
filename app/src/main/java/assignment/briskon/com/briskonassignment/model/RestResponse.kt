package assignment.briskon.com.briskonassignment.model

import com.google.gson.annotations.SerializedName

data class RestResponse(var messages:ArrayList<String>,
                        @SerializedName("result") var countryList:ArrayList<CountryFeed>)
