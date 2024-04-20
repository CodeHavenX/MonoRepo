package com.codehavenx.alpaca.server.model

/**
* * Values: Owner,Staff,Customer
*/
enum class BusinessRelation(val value: kotlin.String) {

    Owner("Owner"),

    Staff("Staff"),

    Customer("Customer");
}
