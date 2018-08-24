package assignment.briskon.com.briskonassignment.model

import com.google.gson.annotations.SerializedName

data class StateListResponse (var messages:ArrayList<String>,
                         @SerializedName("result") var stateList:ArrayList<StateFeed>)