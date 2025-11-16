package com.example.MAD_23012011126_Practical_7

import org.json.JSONObject
import java.io.Serializable

class Person(
    var id: String,
    var name: String,
    var email: String,
    var phone: String,
    var address: String
) : Serializable {

    // FIXED constructor → correctly maps your JSON structure
    constructor(jsonObject: JSONObject) : this(
        jsonObject.optString("id"),
        jsonObject.optJSONObject("profile")?.optString("name") ?: "",
        jsonObject.optString("email"),
        jsonObject.optString("phone"),
        jsonObject.optJSONObject("profile")?.optString("address") ?: ""
    )

    override fun toString(): String {
        return "$name\n$email\n$phone\n$address"
    }
}