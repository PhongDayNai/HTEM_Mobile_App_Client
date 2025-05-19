package com.watb.htem.api

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.watb.htem.data.Dish
import com.watb.htem.data.DishesOrderRequest
import com.watb.htem.data.DishesOrderResponse
import com.watb.htem.data.ServedDish
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

object SocketManager {
    private lateinit var socket: Socket
    private val gson = Gson()

    @SuppressLint("MemberExtensionConflict")
    fun initialize() {
        try {
            socket = IO.socket(Constant.Main_Url)
            socket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MemberExtensionConflict")
    fun dishesOrder(orderId: Int, dishes: List<Dish>, callback: (DishesOrderResponse?) -> Unit) {
        val request = DishesOrderRequest(orderId, dishes)
        val json = gson.toJson(request)
        socket.emit("dishesOrder", JSONObject(json))
        socket.once("dishesOrderResponse") { args ->
            val responseJson = args[0].toString()
            val response = gson.fromJson(responseJson, DishesOrderResponse::class.java)
            callback(response)
        }
    }

    fun getServedDishes(orderId: Int, callback: (List<ServedDish>) -> Unit) {
        val data = JSONObject().apply {
            put("orderId", orderId)
        }
        socket.emit("getServedDishes", data)
        socket.on("getServedDishesResponse") { args ->
            val response = args[0] as JSONObject
            if (response.getBoolean("success")) {
                val servedDishes = mutableListOf<ServedDish>()
                val dataArray = response.getJSONArray("servedDishes")
                for (i in 0 until dataArray.length()) {
                    val dish = dataArray.getJSONObject(i)
                    servedDishes.add(
                        ServedDish(
                            DishName = dish.getString("DishName"),
                            ServedQuantity = dish.getInt("ServedQuantity")
                        )
                    )
                }
                callback(servedDishes)
            }
        }
    }

    fun disconnect() {
        socket.disconnect()
    }
}
