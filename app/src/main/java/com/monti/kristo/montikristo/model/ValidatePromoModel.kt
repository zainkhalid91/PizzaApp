package com.monti.kristo.montikristo.model

data class ValidatePromoModel(
        val status: Boolean,
        val msg : String,
        val data: Data
)
data class Data(
        val discount_type : String?,
        val discount_value : Int?
)
