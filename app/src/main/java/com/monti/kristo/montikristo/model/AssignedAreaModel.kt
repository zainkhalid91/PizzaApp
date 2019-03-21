package com.monti.kristo.montikristo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AssignedAreaModel {
    @SerializedName("status")
    @Expose
    private var status: String? = null
    @SerializedName("area")
    @Expose
    lateinit var area: ArrayList<AreaModel>
}

class AreaModel {
    var id: Int? = 0
    var Name: String? = null
    var assigned: String? = null
}