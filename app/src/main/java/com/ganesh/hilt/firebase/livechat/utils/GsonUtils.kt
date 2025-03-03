package com.ganesh.hilt.firebase.livechat.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GsonUtils {

    private val gson = Gson()

    // Convert a model object to JSON string (with user-provided model type)
    fun <T> modelToJson(model: T): String {
        return gson.toJson(model)
    }

    // Convert a JSON string to a model object (with user-provided model type)
    fun <T> jsonToModel(json: String, modelClass: Class<T>): T {
        return gson.fromJson(json, modelClass)
    }

    // Convert a list of models to JSON string (with user-provided model type)
    fun <T> modelListToJson(modelList: List<T>): String {
        return gson.toJson(modelList)
    }

    // Convert a JSON string to a list of models (with user-provided model type)
    fun <T> jsonToModelList(json: String, modelClass: Class<T>): List<T> {
        val listType = TypeToken.getParameterized(List::class.java, modelClass).type
        return gson.fromJson(json, listType)
    }
}
