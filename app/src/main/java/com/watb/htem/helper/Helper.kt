package com.watb.htem.helper

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.watb.htem.data.CartItem
import com.watb.htem.data.Dish
import com.watb.htem.main.FULL_NAME
import com.watb.htem.main.IS_LOGGED_IN
import com.watb.htem.main.ITEM_NAME
import com.watb.htem.main.ITEM_PRICE
import com.watb.htem.main.ITEM_QUANTITY
import com.watb.htem.main.ITEM_SERVED
import com.watb.htem.main.ITEM_STATUS
import com.watb.htem.main.ITEM_TYPE
import com.watb.htem.data.LoginResponse
import com.watb.htem.main.ORDER_ID
import com.watb.htem.main.USER_ID
import com.watb.htem.main.USER_POINTS
import com.watb.htem.data.ServedDish
import com.watb.htem.main.IS_PAID
import com.watb.htem.main.TABLE_CODE
import com.watb.htem.main.dataStore
import com.watb.htem.main.userDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

object Helper {
    fun isSetChoose(context: Context): Boolean {
        return runBlocking {
            val preferences = context.dataStore.data.first()
            val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()

            for (type in itemTypes) {
                if (type.lowercase() == "set") {
                    return@runBlocking true
                }
            }
            return@runBlocking false
        }
    }

//suspend fun getSetName(context: Context): String {
//    val preferences = context.dataStore.data.first()
//    val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
//    val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
//
//    for (index in itemNames.indices) {
//        val type = itemTypes.getOrNull(index) ?: ""
//        if (type.lowercase() == "set") {
//            val name = itemNames.getOrNull(index) ?: ""
//            return name
//        }
//    }
//    return ""
//}

    fun getSetName(context: Context): String {
        return runBlocking {
            val preferences = context.dataStore.data.first()
            val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
            val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()

            for (index in itemNames.indices) {
                val type = itemTypes.getOrNull(index) ?: ""
                if (type.lowercase() == "set") {
                    val name = itemNames.getOrNull(index) ?: ""
                    return@runBlocking name
                }
            }
            return@runBlocking ""
        }
    }

    @SuppressLint("MemberExtensionConflict")
    fun formatCurrency(amount: Int): String {
        val amountString = amount.toString()
        val result = StringBuilder()

        var count = 0
        for (i in amountString.length - 1 downTo 0) {
            result.append(amountString[i])
            count++

            if (count % 3 == 0 && i != 0) {
                result.append('.')
            }
        }

        result.reverse()

        return "${result}đ"
    }

    suspend fun getUserPoints(context: Context): Int {
        val preferences = context.userDataStore.data.first()
        val points = preferences[USER_POINTS] ?: 0
        return points
    }

    fun countDataFood(context: Context, itemStatus: String, quantityType: String = "ordered"): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            var count = 0

            if (itemStatus != "ordered") {
                val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
                val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
                val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
                val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

                for (index in itemNames.indices) {
                    val status = itemStatuses.getOrNull(index) ?: ""
                    val type = itemTypes.getOrNull(index) ?: ""

                    if (status == itemStatus && type.lowercase() != "drink" && type.lowercase() != "set") {
                        val quantity = itemQuantities.getOrNull(index) ?: ""
                        val intQuantity = quantity.toIntOrNull() ?: 0
                        count += 1 * intQuantity
                    }
                }
            } else {
                when (quantityType) {
                    "ordered" -> {
                        val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
                        val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
                        val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
                        val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

                        for (index in itemNames.indices) {
                            val status = itemStatuses.getOrNull(index) ?: ""
                            val type = itemTypes.getOrNull(index) ?: ""

                            if (status == "ordered" && type.lowercase() != "drink" && type.lowercase() != "set") {
                                val quantity = itemQuantities.getOrNull(index) ?: ""
                                val intQuantity = quantity.toIntOrNull() ?: 0
                                count += 1 * intQuantity
                            }
                        }
                    }
                    "served" -> {
                        val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
                        val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
                        val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
                        val itemServeds = preferences[ITEM_SERVED]?.split(",") ?: emptyList()

                        for (index in itemNames.indices) {
                            val status = itemStatuses.getOrNull(index) ?: ""
                            val type = itemTypes.getOrNull(index) ?: ""

                            if (status == "ordered" && type.lowercase() != "drink" && type.lowercase() != "set") {
                                val servedQuantity = itemServeds.getOrNull(index) ?: ""
                                val intServedQuantity = servedQuantity.toIntOrNull() ?: 0
                                count += 1 * intServedQuantity
                            }
                        }
                    }
                }
            }

            count
        }
    }

    fun getAllServed(context: Context, itemType: String): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            var count = 0
            val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
            val existingServed = preferences[ITEM_SERVED]?.split(",") ?: emptyList()
            Log.d("getAllServed", "existing: $existingServed")
            when (itemType) {
                "notDrink" -> {
                    for (i in 0..(existingServed.size - 1)) {
                        if (itemTypes[i].lowercase() != "drink" && itemTypes[i].lowercase() != "set") {
                            count += existingServed[i].toIntOrNull() ?: 0
                        }
                    }
                }
                "drink" -> {
                    for (i in 0..(existingServed.size - 1)) {
                        if (itemTypes[i].lowercase() == "drink") {
                            count += existingServed[i].toIntOrNull() ?: 0
                        }
                    }
                }
            }

            count
        }
    }

    fun countDataFood(context: Context, itemName: String, itemStatus: String, quantityType: String = "ordered"): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            var count = 0

            if (itemStatus != "ordered") {
                val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
                val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
                val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
                val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

                for (index in itemNames.indices) {
                    val name = itemNames.getOrNull(index) ?: ""
                    val status = itemStatuses.getOrNull(index) ?: ""
                    val type = itemTypes.getOrNull(index) ?: ""
                    val quantity = itemQuantities.getOrNull(index) ?: ""
                    val intQuantity = quantity.toIntOrNull() ?: 0

                    if (status == itemStatus && type.lowercase() != "drink" && name == itemName) {
                        count += 1 * intQuantity
                        break
                    }
                }
            } else {
                when (quantityType) {
                    "ordered" -> {
                        val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
                        val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
                        val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

                        for (index in itemNames.indices) {
                            val name = itemNames.getOrNull(index) ?: ""
                            val status = itemStatuses.getOrNull(index) ?: ""
                            val quantity = itemQuantities.getOrNull(index) ?: ""
                            val intQuantity = quantity.toIntOrNull() ?: 0

                            if (status == itemStatus && name == itemName) {
                                count += 1 * intQuantity
                                break
                            }
                        }
                    }
                    "served" -> {
                        val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
                        val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
                        val itemServeds = preferences[ITEM_SERVED]?.split(",") ?: emptyList()

                        for (index in itemNames.indices) {
                            val name = itemNames.getOrNull(index) ?: ""
                            val status = itemStatuses.getOrNull(index) ?: ""
                            val servedQuantity = itemServeds.getOrNull(index) ?: ""
                            val intServedQuantity = servedQuantity.toIntOrNull() ?: 0

                            if (status == itemStatus && name == itemName) {
                                count += intServedQuantity
                            }
                        }
                    }
                }
            }

            count
        }
    }

    fun countDataDrink(context: Context, itemStatus: String, quantityType: String = "ordered"): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            var count = 0

            if (itemStatus != "ordered") {
                val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
                val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
                val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
                val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

                for (index in itemNames.indices) {
                    val status = itemStatuses.getOrNull(index) ?: ""
                    val type = itemTypes.getOrNull(index) ?: ""

                    if (status == itemStatus && type.lowercase() == "drink") {
                        val quantity = itemQuantities.getOrNull(index) ?: ""
                        val intQuantity = quantity.toIntOrNull() ?: 0
                        count += 1 * intQuantity
                    }
                }
            } else {
                when (quantityType) {
                    "ordered" -> {
                        val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
                        val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
                        val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
                        val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

                        for (index in itemNames.indices) {
                            val status = itemStatuses.getOrNull(index) ?: ""
                            val type = itemTypes.getOrNull(index) ?: ""

                            if (status == itemStatus && type.lowercase() == "drink") {
                                val quantity = itemQuantities.getOrNull(index) ?: ""
                                val intQuantity = quantity.toIntOrNull() ?: 0
                                count += 1 * intQuantity
                            }
                        }
                    }
                    "served" -> {
                        val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
                        val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
                        val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
                        val itemServeds = preferences[ITEM_SERVED]?.split(",") ?: emptyList()

                        for (index in itemNames.indices) {
                            val status = itemStatuses.getOrNull(index) ?: ""
                            val type = itemTypes.getOrNull(index) ?: ""

                            if (status == itemStatus && type.lowercase() == "drink") {
                                val servedQuantity = itemServeds.getOrNull(index) ?: ""
                                val intServedQuantity = servedQuantity.toIntOrNull() ?: 0
                                count += 1 * intServedQuantity
                            }
                        }
                    }
                }
            }

            count
        }
    }

    fun countDataDrink(context: Context, itemName: String, itemStatus: String, quantityType: String = "ordered"): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            var count = 0

            if (itemStatus != "ordered") {
                val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
                val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
                val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
                val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

                for (index in itemNames.indices) {
                    val name = itemNames.getOrNull(index) ?: ""
                    val status = itemStatuses.getOrNull(index) ?: ""
                    val type = itemTypes.getOrNull(index) ?: ""

                    if (status == itemStatus && type.lowercase() == "drink" && name == itemName) {
                        val quantity = itemQuantities.getOrNull(index) ?: ""
                        val intQuantity = quantity.toIntOrNull() ?: 0
                        count += 1 * intQuantity
                        break
                    }
                }
            } else {
                when (quantityType) {
                    "ordered" -> {
                        val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
                        val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
                        val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
                        val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

                        for (index in itemNames.indices) {
                            val name = itemNames.getOrNull(index) ?: ""
                            val status = itemStatuses.getOrNull(index) ?: ""
                            val type = itemTypes.getOrNull(index) ?: ""

                            if (status == itemStatus && type.lowercase() == "drink" && name == itemName) {
                                val quantity = itemQuantities.getOrNull(index) ?: ""
                                val intQuantity = quantity.toIntOrNull() ?: 0
                                count += 1 * intQuantity
                                break
                            }
                        }
                    }
                    "served" -> {
                        val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
                        val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
                        val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
                        val itemServeds = preferences[ITEM_SERVED]?.split(",") ?: emptyList()

                        for (index in itemNames.indices) {
                            val name = itemNames.getOrNull(index) ?: ""
                            val status = itemStatuses.getOrNull(index) ?: ""
                            val type = itemTypes.getOrNull(index) ?: ""
                            val servedQuantity = itemServeds.getOrNull(index) ?: ""
                            val intServedQuantity = servedQuantity.toIntOrNull() ?: 0

                            if (status == itemStatus && type.lowercase() == "drink" && name == itemName) {
                                count += intServedQuantity
                            }
                        }
                    }
                }
            }

            count
        }
    }

    suspend fun decreaseNumber(dataStore: DataStore<Preferences>, item: CartItem) {
        dataStore.edit { preferences ->
            val existingItems = (preferences[ITEM_NAME]?.let { itemNames ->
                itemNames.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val existingQuantities = (preferences[ITEM_QUANTITY]?.let { itemQuantities ->
                itemQuantities.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingPrices = (preferences[ITEM_PRICE]?.let { itemPrices ->
                itemPrices.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingStatuses = (preferences[ITEM_STATUS]?.let { itemStatuses ->
                itemStatuses.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val existingTypes = (preferences[ITEM_TYPE]?.let { itemTypes ->
                itemTypes.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

//        val index = existingItems.indexOfFirst {
//            existingItems.indexOf(it) != -1 && it == item.name && existingStatuses[existingItems.indexOf(it)] == item.status
//        }

            val index = existingItems.indices.indexOfFirst { i ->
                existingItems[i] == item.name && existingStatuses[i] == item.status
            }

            if (index != -1) {
                existingQuantities[index] -= 1

                if (existingQuantities[index] <= 0) {
//            removeItemAtIndex(preferences, index)
                    existingQuantities[index] = 0
                }

                // Lưu lại vào DataStore
                preferences[ITEM_NAME] = existingItems.joinToString(",")
                preferences[ITEM_QUANTITY] = existingQuantities.joinToString(",")
                preferences[ITEM_PRICE] = existingPrices.joinToString(",")
                preferences[ITEM_STATUS] = existingStatuses.joinToString(",")
                preferences[ITEM_TYPE] = existingTypes.joinToString(",")
            }
        }
    }

    suspend fun addItemMenuToDataStore(dataStore: DataStore<Preferences>, menuList: List<CartItem>) {
        dataStore.edit { preferences ->
            val existingItems = (preferences[ITEM_NAME]?.let { itemNames ->
                itemNames.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val existingQuantities = (preferences[ITEM_QUANTITY]?.let { itemQuantities ->
                itemQuantities.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingPrices = (preferences[ITEM_PRICE]?.let { itemPrices ->
                itemPrices.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingStatuses = (preferences[ITEM_STATUS]?.let { itemStatuses ->
                itemStatuses.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val existingServed = (preferences[ITEM_SERVED]?.let { itemServed ->
                itemServed.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingTypes = (preferences[ITEM_TYPE]?.let { itemTypes ->
                itemTypes.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            for (item in menuList) {
                existingItems.add(item.name)
                existingQuantities.add(0)
                existingPrices.add(item.price)
                existingStatuses.add(item.status)
                existingServed.add(0)
                existingTypes.add(item.type)
            }

            preferences[ITEM_NAME] = existingItems.joinToString(",")
            preferences[ITEM_QUANTITY] = existingQuantities.joinToString(",")
            preferences[ITEM_PRICE] = existingPrices.joinToString(",")
            preferences[ITEM_STATUS] = existingStatuses.joinToString(",")
            preferences[ITEM_SERVED] = existingServed.joinToString(",")
            preferences[ITEM_TYPE] = existingTypes.joinToString(",")
        }
    }

    suspend fun addToCart(dataStore: DataStore<Preferences>, newItem: CartItem) {
        dataStore.edit { preferences ->
            val existingItems = (preferences[ITEM_NAME]?.let { itemNames ->
                itemNames.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val existingQuantities = (preferences[ITEM_QUANTITY]?.let { itemQuantities ->
                itemQuantities.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingPrices = (preferences[ITEM_PRICE]?.let { itemPrices ->
                itemPrices.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingStatuses = (preferences[ITEM_STATUS]?.let { itemStatuses ->
                itemStatuses.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val existingServed = (preferences[ITEM_SERVED]?.let { itemServed ->
                itemServed.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingTypes = (preferences[ITEM_TYPE]?.let { itemTypes ->
                itemTypes.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val index = existingItems.indices.indexOfFirst { i ->
                existingItems[i] == newItem.name && existingStatuses[i] == newItem.status
            }

            if (index != -1) {
                // Nếu mặt hàng đã tồn tại, nhưng status khác nhau thì không gộp
//            if (existingStatuses[index] == "notOrderYet" && newItem.status == "notOrderYet") {
                if (existingStatuses[index] == newItem.status) {
                    existingQuantities[index] += newItem.quantity
                } else {
                    // Nếu status khác nhau, giữ nguyên
                    existingItems.add(newItem.name)
                    existingQuantities.add(newItem.quantity)
                    existingPrices.add(newItem.price)
                    existingStatuses.add(newItem.status)
                    existingServed.add(0)
                    existingTypes.add(newItem.type)
                }
            } else {
                // Nếu mặt hàng chưa tồn tại, thêm mới với status là "notOrderYet"
                existingItems.add(newItem.name)
                existingQuantities.add(newItem.quantity)
                existingPrices.add(newItem.price)
                existingStatuses.add(newItem.status)
                existingServed.add(0)
                existingTypes.add(newItem.type)
            }

            // Lưu lại vào DataStore
            preferences[ITEM_NAME] = existingItems.joinToString(",")
            preferences[ITEM_QUANTITY] = existingQuantities.joinToString(",")
            preferences[ITEM_PRICE] = existingPrices.joinToString(",")
            preferences[ITEM_STATUS] = existingStatuses.joinToString(",")
            preferences[ITEM_SERVED] = existingServed.joinToString(",")
            preferences[ITEM_TYPE] = existingTypes.joinToString(",")
        }
    }

    suspend fun printDataStore(dataStore: DataStore<Preferences>) {
        dataStore.data.first().let { preferences ->
            val orderId = preferences[ORDER_ID]
            val names = preferences[ITEM_NAME]
            val quantities = preferences[ITEM_QUANTITY]
            val prices = preferences[ITEM_PRICE]
            val statuses = preferences[ITEM_STATUS]
            val served = preferences[ITEM_SERVED]
            val types = preferences[ITEM_TYPE]
            val isPaid = preferences[IS_PAID]
            Log.d("DataStore","OrderID: $orderId,\nNames: $names,\nQuantities: $quantities,\nPrices: $prices,\nStatuses: $statuses,\nServed: $served,\nTypes: $types,\nIsPaid: $isPaid")
        }
    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun orderItems(dataStore: DataStore<Preferences>) {
        dataStore.edit { preferences ->
            val existingItems = (preferences[ITEM_NAME]?.let { itemNames ->
                itemNames.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val existingQuantities = (preferences[ITEM_QUANTITY]?.let { itemQuantities ->
                itemQuantities.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingPrices = (preferences[ITEM_PRICE]?.let { itemPrices ->
                itemPrices.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingServed = (preferences[ITEM_SERVED]?.let { itemServed ->
                itemServed.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingTypes = (preferences[ITEM_TYPE]?.let { itemTypes ->
                itemTypes.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val itemMap = mutableMapOf<String, Quad<Int, Int, String, Int>>()

            for (i in existingItems.indices) {
                val itemName = existingItems[i]
                val currentQuantity = existingQuantities.getOrElse(i) { 0 }
                val currentPrice = existingPrices.getOrElse(i) { 0 }
                val currentType = existingTypes.getOrElse(i) { "" }
                val currentServed = existingServed.getOrElse(i) { 0 }

                if (itemMap.containsKey(itemName)) {
                    val (quantity, price, type, served) = itemMap[itemName]!!
                    itemMap[itemName] = Quad(
                        quantity + currentQuantity,
                        price,
                        type,
                        served + currentServed
                    )
                } else {
                    itemMap[itemName] = Quad(currentQuantity, currentPrice, currentType, currentServed)
                }
            }

            val updatedItems = mutableListOf<String>()
            val updatedQuantities = mutableListOf<Int>()
            val updatedPrices = mutableListOf<Int>()
            val updatedStatuses = mutableListOf<String>()
            val updatedServed = mutableListOf<Int>()
            val updatedTypes = mutableListOf<String>()

            itemMap.forEach { (itemName, quad) ->
                updatedItems.add(itemName)
                updatedQuantities.add(quad.first)
                updatedPrices.add(quad.second)
                updatedStatuses.add("ordered")
                updatedServed.add(quad.fourth)
                updatedTypes.add(quad.third)
            }

            preferences[ITEM_NAME] = updatedItems.joinToString(",")
            preferences[ITEM_QUANTITY] = updatedQuantities.joinToString(",")
            preferences[ITEM_PRICE] = updatedPrices.joinToString(",")
            preferences[ITEM_STATUS] = updatedStatuses.joinToString(",")
            preferences[ITEM_SERVED] = updatedServed.joinToString(",")
            preferences[ITEM_TYPE] = updatedTypes.joinToString(",")
        }
    }

    data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    suspend fun getCartItems(dataStore: DataStore<Preferences>): List<CartItem> {
        return dataStore.data.map { preferences ->
            val itemNames = preferences[ITEM_NAME]?.split(",")?.map { it.trim() } ?: emptyList()
            val itemQuantities = preferences[ITEM_QUANTITY]?.split(",")?.map { it.trim().toInt() } ?: emptyList()
            val itemPrices = preferences[ITEM_PRICE]?.split(",")?.map { it.trim().toInt() } ?: emptyList()
            val itemStatuses = preferences[ITEM_STATUS]?.split(",")?.map { it.trim() } ?: emptyList()
            val itemTypes = preferences[ITEM_TYPE]?.split(",")?.map { it.trim() } ?: emptyList()

            val cartItems = itemNames.mapIndexed { index, name ->
                CartItem(
                    name = name,
                    quantity = itemQuantities.getOrElse(index) { 0 },
                    price = itemPrices.getOrElse(index) { 0 },
                    status = itemStatuses.getOrElse(index) { "notOrderYet" },
                    type = itemTypes.getOrElse(index) { "unknown" }
                )
            }

            cartItems.groupBy { it.name to it.status }
                .map { (key, items) ->
                    val totalQuantity = items.sumOf { it.quantity }
                    val price = items.first().price
                    val type = items.first().type
                    CartItem(
                        name = key.first,
                        quantity = totalQuantity,
                        price = price,
                        status = key.second,
                        type = type
                    )
                }
        }.first()
    }

    suspend fun clearDataStore(dataStore: DataStore<Preferences>) {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveLoginState(context: Context, isLoggedIn: Boolean, response: LoginResponse) {
        context.userDataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            preferences[USER_ID] = response.userId ?: 0
            preferences[FULL_NAME] = response.fullName ?: ""
            preferences[USER_POINTS] = response.points ?: 0
        }
    }

    suspend fun saveUserPoints(context: Context, points: Int) {
        context.userDataStore.edit { preferences ->
            preferences[USER_POINTS] = points
        }
    }

    suspend fun isLoggedIn(context: Context): Boolean {
        val preferences = context.userDataStore.data.first()
        return preferences[IS_LOGGED_IN] ?: false
    }

    suspend fun saveOrderID(context: Context, orderId: Int) {
        context.dataStore.edit { preferences ->
            preferences[ORDER_ID] = orderId
        }
    }

    suspend fun getOrderID(context: Context): Int {
        val preferences = context.dataStore.data.first()
        return preferences[ORDER_ID] ?: 0
    }

    suspend fun isPaid(context: Context): Boolean {
        val preferences = context.dataStore.data.first()
        return preferences[IS_PAID] ?: false
    }

    suspend fun setPaidState(context: Context, isPaid: Boolean = false) {
        context.dataStore.edit { preferences ->
            preferences[IS_PAID] = isPaid
        }
    }

    suspend fun saveTableCode(context: Context, tableCode: String) {
        context.dataStore.edit { preferences ->
            preferences[TABLE_CODE] = tableCode
        }
    }

    suspend fun getTableCode(context: Context): String {
        val preferences = context.dataStore.data.first()
        return preferences[TABLE_CODE] ?: ""
    }

    suspend fun printDataStoreUser(dataStore: DataStore<Preferences>) {
        dataStore.data.first().let { preferences ->
            val isLoggedIn = preferences[IS_LOGGED_IN] ?: false
            val userId = preferences[USER_ID] ?: 0
            val fullName = preferences[FULL_NAME] ?: ""
            val points = preferences[USER_POINTS] ?: 0
            Log.d(
                "DataStore",
                "isLoggedIn: $isLoggedIn, userId: $userId, fullName: $fullName, points: $points"
            )
        }
    }

    suspend fun getDishes(context: Context): List<Dish> {
        val preferences = context.dataStore.data.first()
        val dishes = mutableListOf<Dish>()

        val existingItems = (preferences[ITEM_NAME]?.let { itemNames ->
            itemNames.split(",").map { it.trim() }
        } ?: emptyList()).toMutableList()

        val existingQuantities = (preferences[ITEM_QUANTITY]?.let { itemQuantities ->
            itemQuantities.split(",").map { it.trim().toInt() }
        } ?: emptyList()).toMutableList()

        val existingStatuses = (preferences[ITEM_STATUS]?.let { itemStatuses ->
            itemStatuses.split(",").map { it.trim() }
        } ?: emptyList()).toMutableList()

        for (index in existingItems.indices) {
            if (existingStatuses[index] == "notOrderYet") {
                dishes.add(Dish(existingItems[index], existingQuantities[index]))
                existingStatuses[index] = "ordered"
            }
        }

        Log.d("Dishes", "Quantity: $existingQuantities")
        Log.d("Dishes", "Status: $existingStatuses")

        return dishes
    }

    suspend fun updateServedDishes(context: Context, servedDishes: List<ServedDish>) {
        context.dataStore.edit { preferences ->
            val existingItems = (preferences[ITEM_NAME]?.let { itemNames ->
                itemNames.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val existingQuantities = (preferences[ITEM_QUANTITY]?.let { itemQuantities ->
                itemQuantities.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingPrices = (preferences[ITEM_PRICE]?.let { itemPrices ->
                itemPrices.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingStatuses = (preferences[ITEM_STATUS]?.let { itemStatuses ->
                itemStatuses.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val existingServed = (preferences[ITEM_SERVED]?.let { itemServed ->
                itemServed.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingTypes = (preferences[ITEM_TYPE]?.let { itemTypes ->
                itemTypes.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            for (i in 0..<(existingItems.size - 1)) {
                for (j in 0..(servedDishes.size - 1)) {
                    if (existingItems[i] == servedDishes[j].DishName) {
                        existingServed[i] = servedDishes[j].ServedQuantity
//                        if (existingQuantities[i] == existingServed[i]) {
//                            existingStatuses[i] = "served"
//                        }
                    }
                }
            }

            preferences[ITEM_NAME] = existingItems.joinToString(",")
            preferences[ITEM_QUANTITY] = existingQuantities.joinToString(",")
            preferences[ITEM_PRICE] = existingPrices.joinToString(",")
            preferences[ITEM_STATUS] = existingStatuses.joinToString(",")
            preferences[ITEM_SERVED] = existingServed.joinToString(",")
            preferences[ITEM_TYPE] = existingTypes.joinToString(",")

//            printDataStore(context.dataStore)
        }
    }
}