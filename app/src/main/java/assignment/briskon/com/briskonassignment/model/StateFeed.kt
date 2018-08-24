package assignment.briskon.com.briskonassignment.model

import com.google.gson.annotations.SerializedName

data class StateFeed (@SerializedName("id") var stateId: Int =0,
                      @SerializedName("country") var countryCode: String,
                      @SerializedName("name") var stateName: String,
                      @SerializedName("abbr") var stateCode: String,
                      @SerializedName("area") var area: String,
                      @SerializedName("largest_city") var largest_city: String,
                      @SerializedName("capital")var capitalOfState: String)
