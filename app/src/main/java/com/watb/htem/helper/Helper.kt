package com.watb.htem.helper

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.watb.htem.CartItem
import com.watb.htem.Dish
import com.watb.htem.FULL_NAME
import com.watb.htem.IS_LOGGED_IN
import com.watb.htem.ITEM_NAME
import com.watb.htem.ITEM_PRICE
import com.watb.htem.ITEM_QUANTITY
import com.watb.htem.ITEM_SERVED
import com.watb.htem.ITEM_STATUS
import com.watb.htem.ITEM_TYPE
import com.watb.htem.LoginResponse
import com.watb.htem.ORDER_ID
import com.watb.htem.USER_ID
import com.watb.htem.USER_POINTS
import com.watb.htem.UserPointsResponse
import com.watb.htem.dataStore
import com.watb.htem.userDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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
        // Sử dụng runBlocking để thực thi coroutine trong hàm đồng bộ
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

    fun formatCurrency(amount: Int): String {
        // Chuyển đổi thành chuỗi
        val amountString = amount.toString()
        val result = StringBuilder()

        // Duyệt từ phải sang trái
        var count = 0
        for (i in amountString.length - 1 downTo 0) {
            result.append(amountString[i])
            count++

            // Thêm dấu phân cách sau mỗi 3 chữ số
            if (count % 3 == 0 && i != 0) {
                result.append('.')
            }
        }

        // Đảo ngược chuỗi kết quả
        result.reverse()

        // Thêm ký hiệu tiền tệ
        return "${result}đ"
    }

    suspend fun getUserPoints(context: Context): Int {
        val preferences = context.userDataStore.data.first()
        val points = preferences[USER_POINTS] ?: 0
        return points
    }

    suspend fun countDataStoreEntries(context: Context): Int {
        val preferences = context.dataStore.data.first() // Lấy dữ liệu từ DataStore
        return preferences.asMap().size // Đếm số lượng key-value pairs
    }

    fun countDataFood(context: Context, itemStatus: String): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            var count = 0

            // Lấy danh sách các giá trị từ DataStore
            val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
            val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
            val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
            val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

            // Duyệt qua danh sách và kiểm tra điều kiện
            for (index in itemNames.indices) {
                val status = itemStatuses.getOrNull(index) ?: ""
                val type = itemTypes.getOrNull(index) ?: ""
                val quantity = itemQuantities.getOrNull(index) ?: ""
                val intQuantity = quantity.toIntOrNull() ?: 0

                // Kiểm tra trạng thái "notOrderYet" và loại khác "drink"
                if (status == itemStatus && type.lowercase() != "drink" && type.lowercase() != "set") {
                    count += 1 * intQuantity
                }
            }
            count
        }
    }

    fun countDataFood(context: Context, itemName: String, itemStatus: String): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            var count = 0

            // Lấy danh sách các giá trị từ DataStore
            val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
            val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
            val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
            val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

            // Duyệt qua danh sách và kiểm tra điều kiện
            for (index in itemNames.indices) {
                val name = itemNames.getOrNull(index) ?: ""
                val status = itemStatuses.getOrNull(index) ?: ""
                val type = itemTypes.getOrNull(index) ?: ""
                val quantity = itemQuantities.getOrNull(index) ?: ""
                val intQuantity = quantity.toIntOrNull() ?: 0

                // Kiểm tra trạng thái "notOrderYet" và loại khác "drink"
                if (status == itemStatus && type.lowercase() != "drink" && name == itemName) {
                    count += 1 * intQuantity
                    break
                }
            }
            count
        }
    }

    fun countDataDrink(context: Context, itemName: String, itemStatus: String): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            var count = 0

            // Lấy danh sách các giá trị từ DataStore
            val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
            val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
            val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
            val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

            // Duyệt qua danh sách và kiểm tra điều kiện
            for (index in itemNames.indices) {
                val name = itemNames.getOrNull(index) ?: ""
                val status = itemStatuses.getOrNull(index) ?: ""
                val type = itemTypes.getOrNull(index) ?: ""
                val quantity = itemQuantities.getOrNull(index) ?: ""
                val intQuantity = quantity.toIntOrNull() ?: 0

                // Kiểm tra trạng thái "notOrderYet" và loại khác "drink"
                if (status == itemStatus && type.lowercase() == "drink" && name == itemName) {
                    count += 1 * intQuantity
                    break
                }
            }
            count
        }
    }

    fun countDataDrink(context: Context, itemStatus: String): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            var count = 0
            // Lấy danh sách các giá trị từ DataStore
            val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
            val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
            val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
            val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

            // Duyệt qua danh sách và kiểm tra điều kiện
            for (index in itemNames.indices) {
                val status = itemStatuses.getOrNull(index) ?: ""
                val type = itemTypes.getOrNull(index) ?: ""
                val quantity = itemQuantities.getOrNull(index) ?: ""
                val intQuantity = quantity.toIntOrNull() ?: 0

                // Kiểm tra trạng thái "notOrderYet" và loại khác "drink"
                if (status == itemStatus && type.lowercase() == "drink") {
                    count += 1 * intQuantity
                }
            }
            count
        }
    }

    fun getQuantityForItem(context: Context, item: CartItem): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            val itemNames = preferences[ITEM_NAME]?.split(",") ?: emptyList()
            val itemStatuses = preferences[ITEM_STATUS]?.split(",") ?: emptyList()
            val itemTypes = preferences[ITEM_TYPE]?.split(",") ?: emptyList()
            val itemQuantities = preferences[ITEM_QUANTITY]?.split(",") ?: emptyList()

            val index = itemNames.indexOfFirst {
                it == item.name && itemStatuses.getOrNull(itemNames.indexOf(it)) == item.status
            }

            if (index != -1) {
                itemQuantities.getOrNull(index)?.toIntOrNull() ?: 0
            } else {
                0
            }
        }
    }

    suspend fun increaseNumber(dataStore: DataStore<Preferences>, item: CartItem) {
        dataStore.edit { preferences ->
            printDataStore(dataStore)

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

            val index = existingItems.indexOfFirst {
                existingItems.indexOf(it) != -1 && it == item.name && existingStatuses[existingItems.indexOf(it)] == item.status
            }

            existingQuantities[index] += 1

            // Lưu lại vào DataStore
            preferences[ITEM_NAME] = existingItems.joinToString(",")
            preferences[ITEM_QUANTITY] = existingQuantities.joinToString(",")
            preferences[ITEM_PRICE] = existingPrices.joinToString(",")
            preferences[ITEM_STATUS] = existingStatuses.joinToString(",")
            preferences[ITEM_TYPE] = existingTypes.joinToString(",")

            printDataStore(dataStore)
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
            Log.d("DataStore","OrderID: $orderId,\nNames: $names,\nQuantities: $quantities,\nPrices: $prices,\nStatuses: $statuses,\nServed: $served,\nTypes: $types")
        }
    }

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

            val existingStatuses = (preferences[ITEM_STATUS]?.let { itemStatuses ->
                itemStatuses.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val existingServed = (preferences[ITEM_SERVED]?.let { itemServed ->
                itemServed.split(",").map { it.trim().toInt() }
            } ?: emptyList()).toMutableList()

            val existingTypes = (preferences[ITEM_TYPE]?.let { itemTypes ->
                itemTypes.split(",").map { it.trim() }
            } ?: emptyList()).toMutableList()

            val updatedItems = mutableListOf<String>()
            val updatedQuantities = mutableListOf<Int>()
            val updatedPrices = mutableListOf<Int>()
            val updatedStatuses = mutableListOf<String>()
            val updatedSered = mutableListOf<Int>()
            val updatedTypes = mutableListOf<String>()

//        for (i in existingItems.indices) {
//            if (existingStatuses[i] == "notOrderYet") {
//                // Chuyển trạng thái thành "ordered"
//                updatedStatuses.add("ordered")
//                updatedQuantities.add(existingQuantities[i]) // Giữ nguyên số lượng
//            } else {
//                updatedStatuses.add(existingStatuses[i])
//                updatedQuantities.add(existingQuantities[i])
//            }
//        }

            for (i in 0..<(existingItems.size - 1)) {
                if (existingItems[i] !in updatedItems) {
                    for (j in 1..<existingItems.size) {
                        if (existingItems[i] == existingItems[j]) {
                            existingQuantities[i] += existingQuantities[j]
                        }
                    }
                    updatedItems.add(existingItems[i])
                    updatedQuantities.add(existingQuantities[i])
                    updatedPrices.add(existingPrices[i])
                    updatedStatuses.add("ordered")
                    updatedSered.add(existingServed[i])
                    updatedTypes.add(existingTypes[i])
                }
            }

            // Lưu lại vào DataStore
            preferences[ITEM_NAME] = updatedItems.joinToString(",")
            preferences[ITEM_QUANTITY] = updatedQuantities.joinToString(",")
            preferences[ITEM_PRICE] = updatedPrices.joinToString(",")
            preferences[ITEM_STATUS] = updatedStatuses.joinToString(",")
            preferences[ITEM_SERVED] = updatedSered.joinToString(",")
            preferences[ITEM_TYPE] = updatedTypes.joinToString(",")
        }
    }

    suspend fun getCartItems(dataStore: DataStore<Preferences>): List<CartItem> {
        return dataStore.data.map { preferences ->
            val itemNames = preferences[ITEM_NAME]?.split(",")?.map { it.trim() } ?: emptyList()
            val itemQuantities = preferences[ITEM_QUANTITY]?.split(",")?.map { it.trim().toInt() } ?: emptyList()
            val itemPrices = preferences[ITEM_PRICE]?.split(",")?.map { it.trim().toInt() } ?: emptyList()
            val itemStatuses = preferences[ITEM_STATUS]?.split(",")?.map { it.trim() } ?: emptyList()
            val itemTypes = preferences[ITEM_TYPE]?.split(",")?.map { it.trim() } ?: emptyList()

            // Khởi tạo danh sách CartItem
            val cartItems = itemNames.mapIndexed { index, name ->
                CartItem(
                    name = name,
                    quantity = itemQuantities.getOrElse(index) { 0 },
                    price = itemPrices.getOrElse(index) { 0 },
                    status = itemStatuses.getOrElse(index) { "notOrderYet" }, // Mặc định là "notOrderYet"
                    type = itemTypes.getOrElse(index) { "unknown" }
                )
            }

            // Gộp các món trùng nhau (cùng tên và trạng thái)
            cartItems.groupBy { it.name to it.status }
                .map { (key, items) ->
                    val totalQuantity = items.sumOf { it.quantity }
                    val price = items.first().price // Giá giữ nguyên từ món đầu tiên
                    val type = items.first().type // Loại giữ nguyên từ món đầu tiên
                    CartItem(
                        name = key.first,
                        quantity = totalQuantity,
                        price = price,
                        status = key.second,
                        type = type
                    )
                }
        }.first() // Chỉ lấy giá trị đầu tiên
    }

    // Hàm để xóa dữ liệu trong DataStore
    suspend fun clearDataStore(dataStore: DataStore<Preferences>) {
        dataStore.edit { preferences ->
            // Xóa các key tương ứng
            preferences.clear() // Xóa tất cả dữ liệu
        }
    }

    fun clearSpecificKey(dataStore: DataStore<Preferences>, key: String) {
        val preferencesKey = stringPreferencesKey(key) // Tạo khóa từ tên
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences.remove(preferencesKey) // Xóa khóa cụ thể
            }
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

    suspend fun saveUserPoints(context: Context, response: UserPointsResponse) {
        context.userDataStore.edit { preferences ->
            preferences[USER_POINTS] = response.points ?: 0
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

    fun loginResponseToMap(response: LoginResponse): Map<String, Any?> {
        return mapOf(
            "message" to response.message,
            "userId" to response.userId,
            "fullName" to response.fullName,
            "points" to response.points,
            "error" to response.error
        )
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

        for (index in existingItems.indices) {
            if (existingStatuses[index] == "notOrderYet") {
                dishes.add(Dish(existingItems[index], existingQuantities[index]))
            }
        }

        return dishes
    }
}