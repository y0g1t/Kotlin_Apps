package assignment.briskon.com.briskonassignment.model

import com.google.gson.annotations.SerializedName

data class CountryFeed (@SerializedName("name") var name: String,
                        var alpha2_code: String,
                        var alpha3_code: String)
