package com.example.desiregallery.network.serializers

import com.example.desiregallery.models.User
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

import java.lang.reflect.Type


class UserSerializer : JsonSerializer<User> {
    override fun serialize(src: User, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val result = JsonObject()
        val fields = JsonObject()
        val password = JsonObject()
        val email = JsonObject()
        val gender = JsonObject()
        val birthday = JsonObject()
        val photo = JsonObject()

        password.addProperty("stringValue", src.password)
        email.addProperty("stringValue", src.email)
        gender.addProperty("stringValue", src.gender)
        birthday.addProperty("stringValue", src.birthday)
        photo.addProperty("stringValue", src.photo)

        with (fields) {
            add("password", password)
            add("email", email)
            add("gender", gender)
            add("birthday", birthday)
            add("photo", photo)
        }
        result.add("fields", fields)
        return result
    }
}
