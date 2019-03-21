package com.monti.kristo.montikristo.model


data class PreviousOrderModel(
        val msg: String,
        val ods: List<Od>,
        val status: Boolean
)

data class Od(
        val details: List<Detail>,
        val head: Head
)

data class Detail(
        val ProductId: Int,
        val ProductName: String,
        val ProductQuantity: Int,
        val ProductSubtotal: Int
)

data class Head(
        val GrossTotal: Int,
        val OrderDate: String,
        val OrderStatus: String,
        val orderID: Int
)