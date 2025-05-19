package com.watb.htem.api

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.watb.htem.data.BuffetOrderResponse
import com.watb.htem.data.CallStaffRequest
import com.watb.htem.data.CallStaffResponse
import com.watb.htem.data.CheckPaymentStateRequest
import com.watb.htem.data.CheckPaymentStateResponse
import com.watb.htem.data.Dish
import com.watb.htem.data.DishesOrderRequest
import com.watb.htem.data.DishesOrderResponse
import com.watb.htem.data.GetServedDishesRequest
import com.watb.htem.data.GetServedDishesResponse
import com.watb.htem.data.GuessBuffetOrderRequest
import com.watb.htem.data.LoginRequest
import com.watb.htem.data.LoginResponse
import com.watb.htem.data.PaymentGuessRequest
import com.watb.htem.data.PaymentGuessResponse
import com.watb.htem.data.PaymentUserRequest
import com.watb.htem.data.PaymentUserResponse
import com.watb.htem.data.RegisterRequest
import com.watb.htem.data.UserBuffetOrderRequest
import com.watb.htem.data.UserPointsRequest
import com.watb.htem.data.UserPointsResponse
import com.watb.htem.data.UserTransactionRequest
import com.watb.htem.data.UserTransactionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object ApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(300, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private var statusCode: Int? = null

    private fun setStatusCode(code: Int) {
        statusCode = code
    }

    fun getStatusCode(): Int? {
        return statusCode
    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun login(phone: String, password: String): LoginResponse? {
        val loginRequest = LoginRequest(phone, password)
        val json = gson.toJson(loginRequest)
        Log.d("Login", json.toString())

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(Constant.Login_Url)
            .post(requestBody)
            .build()
        Log.d("Login", Constant.Login_Url)

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d("Login", "Response code: ${response.code}")
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            Log.d("Login", it)
                            gson.fromJson(it, LoginResponse::class.java)
                        }
                    } else {
                        Log.d("Login", "Response nul or unsuccessful")
                        null
                    }
                }
            } catch (e: TimeoutException) {
                e.printStackTrace()
                null
            }
        }
    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun register(phone: String, password: String, fullName: String): LoginResponse? {
        val registerRequest = RegisterRequest(fullName, phone, password)
        val json = gson.toJson(registerRequest)
        Log.d("Register", json.toString())

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(Constant.Register_Url)
            .post(requestBody)
            .build()
        Log.d("Register", Constant.Register_Url)

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d("Register", "Response code: ${response.code}")
                    setStatusCode(response.code)
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            Log.d("Register", it)
                            gson.fromJson(it, LoginResponse::class.java)
                        }
                    } else {
                        Log.d("Register", "Response nul or unsuccessful")
                        null
                    }
                }
            } catch (e: TimeoutException) {
                e.printStackTrace()
                null
            }
        }
    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun userBuffetOrder(userId: Int, buffetId: Int, tableId: Int, quantity: Int, totalPrice: Int): BuffetOrderResponse? {
        val userBuffetOrderRequest = UserBuffetOrderRequest(userId, buffetId, tableId, quantity, totalPrice)
        val json = gson.toJson(userBuffetOrderRequest)
        Log.d("UserBuffetOrder", json.toString())

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(Constant.User_Buffet_Url)
            .post(requestBody)
            .build()
        Log.d("UserBuffetOrder", Constant.User_Buffet_Url)

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d("UserBuffetOrder", "Response code: ${response.code}")
                    setStatusCode(response.code)
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            Log.d("UserBuffOrder", it)
                            gson.fromJson(it, BuffetOrderResponse::class.java)
                        }
                    } else {
                        Log.d("UserBuffetOrder", "Response nul or unsuccessful")
                        null
                    }
                }
            } catch (e: TimeoutException) {
                e.printStackTrace()
                null
            }
        }
    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun guessBuffetOrder(buffetId: Int, tableId: Int, quantity: Int, totalPrice: Int): BuffetOrderResponse? {
        val guessBuffetOrderRequest = GuessBuffetOrderRequest(buffetId, tableId, quantity, totalPrice)
        val json = gson.toJson(guessBuffetOrderRequest)
        Log.d("GuessBuffetOrder", json.toString())

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(Constant.Guess_Buffet_Url)
            .post(requestBody)
            .build()
        Log.d("GuessBuffetOrder", Constant.Guess_Buffet_Url)

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d("GuessBuffetOrder", "Response code: ${response.code}")
                    setStatusCode(response.code)
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            Log.d("GuessBuffetOrder", it)
                            gson.fromJson(it, BuffetOrderResponse::class.java)
                        }
                    } else {
                        Log.d("GuessBuffetOrder", "Response nul or unsuccessful")
                        null
                    }
                }
            } catch (e: TimeoutException) {
                e.printStackTrace()
                null
            }
        }
    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun dishesOrder(orderId: Int, dishes: List<Dish>): DishesOrderResponse? {
        val dishesOrderRequest = DishesOrderRequest(orderId, dishes)
        val json = gson.toJson(dishesOrderRequest)
        Log.d("DishesOrder", json.toString())

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(Constant.Add_Dishes_Url)
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d("DishesOrder", "Response code: ${response.code}")
                    setStatusCode(response.code)
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            Log.d("DishesOrder", it)
                            gson.fromJson(it, DishesOrderResponse::class.java)
                        }
                    } else {
                        Log.d("DishesOrder", "Response nul or unsuccessful")
                        null
                    }
                }
            } catch (e: TimeoutException) {
                e.printStackTrace()
                null
            }
        }
    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun getServedDishes(orderId: Int): GetServedDishesResponse? {
        val getServedDishesRequest = GetServedDishesRequest(orderId)
        val json = gson.toJson(getServedDishesRequest)

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(Constant.Get_Served_Dishes_Url)
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d("GetServedDishes", "Response code: ${response.code}")
                    setStatusCode(response.code)
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            Log.d("GetServedDishes", it)
                            gson.fromJson(it, GetServedDishesResponse::class.java)
                        }
                    } else {
                        Log.d("GetServedDishes", "Response null or unsuccessful")
                        null
                    }
                }
            } catch (e: TimeoutException) {
                e.printStackTrace()
                null
            }
        }
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    suspend fun dishesOrder(orderId: Int, dishes: List<Dish>): DishesOrderResponse? {
//        return withContext(Dispatchers.IO) {
//            try {
//                suspendCancellableCoroutine { continuation ->
//                    SocketManager.dishesOrder(orderId, dishes) { response ->
//                        if (response != null) {
//                            setStatusCode(200) // Giả lập mã trạng thái
//                            continuation.resume(
//                                response,
//                                onCancellation = { /*TODO*/ }
//                            )
//                        } else {
//                            setStatusCode(400) // Giả lập lỗi
//                            continuation.resume(
//                                null,
//                                onCancellation = { /*TODO*/ }
//                            )
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//        }
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    suspend fun getServedDishes(orderId: Int): GetServedDishesResponse? {
//        return withContext(Dispatchers.IO) {
//            try {
//                suspendCancellableCoroutine { continuation ->
//                    SocketManager.getServedDishes(orderId) { response ->
//                        if (response != null) {
//                            setStatusCode(200) // Giả lập mã trạng thái
//                            continuation.resume(
//                                response,
//                                onCancellation = { /*TODO*/ }
//                            )
//                        } else {
//                            setStatusCode(400) // Giả lập lỗi
//                            continuation.resume(
//                                null,
//                                onCancellation = { /*TODO*/ }
//                            )
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//        }
//    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun paymentUser(userId: Int, orderId: Int, usingPoints: Int, pointsUsedNumber: Int, amount: Int, paymentMethod: String): PaymentUserResponse? {
        val paymentUserRequest = PaymentUserRequest(userId, orderId, usingPoints, pointsUsedNumber, amount, paymentMethod)
        val json = gson.toJson(paymentUserRequest)
        Log.d("PaymentUser", json.toString())

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(Constant.Payment_User_Url)
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d("PaymentUser", "Response code: ${response.code}")
                    setStatusCode(response.code)
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            Log.d("PaymentUser", it)
                            gson.fromJson(it, PaymentUserResponse::class.java)
                        }
                    } else {
                        Log.d("PaymentUser", "Response nul or unsuccessful")
                        null
                    }
                }
            } catch (e: TimeoutException) {
                e.printStackTrace()
                null
            }
        }
    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun paymentGuess(orderId: Int, amount: Int, paymentMethod: String): PaymentGuessResponse? {
        val paymentGuessRequest = PaymentGuessRequest(orderId, amount, paymentMethod)
        val json = gson.toJson(paymentGuessRequest)
        Log.d("PaymentGuess", json.toString())

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(Constant.Payment_Guess_Url)
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d("PaymentGuess", "Response code: ${response.code}")
                    setStatusCode(response.code)
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            Log.d("PaymentGuess", it)
                            gson.fromJson(it, PaymentGuessResponse::class.java)
                        }
                    } else {
                        Log.d("PaymentGuess", "Response nul or unsuccessful")
                        null
                    }
                }
            } catch (e: TimeoutException) {
                e.printStackTrace()
                null
            }
        }
    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun getUserPoints(userId: Int): UserPointsResponse? {
        val userPointsRequest = UserPointsRequest(userId)
        val json = gson.toJson(userPointsRequest)
        Log.d("UserPoints", json.toString())

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(Constant.User_Points_Url)
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d("UserPoints", "Response code: ${response.code}")
                    setStatusCode(response.code)
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            Log.d("UserPoints", it)
                            gson.fromJson(it, UserPointsResponse::class.java)
                        }
                    } else {
                        Log.d("UserPoints", "Response nul or unsuccessful")
                        null
                    }
                }
            } catch (e: TimeoutException) {
                e.printStackTrace()
                null
            }
        }
    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun checkPaymentState(orderId: Int): CheckPaymentStateResponse? {
        val checkPaymentStateRequest = CheckPaymentStateRequest(orderId)
        val json = gson.toJson(checkPaymentStateRequest)

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(Constant.Check_Payment_State_Url)
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d("CheckPaymentState", "Response code: ${response.code}")
                    setStatusCode(response.code)
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            Log.d("CheckPaymentState", it)
                            gson.fromJson(it, CheckPaymentStateResponse::class.java)
                        }
                    } else {
                        Log.d("CheckPaymentState", "Response nul or unsuccessful")
                        null
                    }
                }
            } catch (e: TimeoutException) {
                e.printStackTrace()
                null
            }
        }
    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun getUserTransaction(userId: Int): UserTransactionResponse? {
        val userTransactionRequest = UserTransactionRequest(userId)
        val json = gson.toJson(userTransactionRequest)
        Log.d("UserTransaction", json.toString())

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(Constant.User_Transaction_Url)
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d("UserTransaction", "Response code: ${response.code}")
                    setStatusCode(response.code)
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            Log.d("UserTransaction", it)
                            gson.fromJson(it, UserTransactionResponse::class.java)
                        }
                    } else {
                        Log.d("UserTransaction", "Response nul or unsuccessful")
                        null
                    }
                }
            } catch (e: TimeoutException) {
                e.printStackTrace()
                null
            }
        }
    }

    @SuppressLint("MemberExtensionConflict")
    suspend fun callStaff(tableId: Int): CallStaffResponse? {
        val callStaffRequest = CallStaffRequest(tableId)
        val json = gson.toJson(callStaffRequest)

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(Constant.Call_Staff_Url)
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    Log.d("CallStaff", "Response code: ${response.code}")
                    setStatusCode(response.code)
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            Log.d("CallStaff", it)
                            gson.fromJson(it, CallStaffResponse::class.java)
                        }
                    } else {
                        Log.d("CallStaff", "Response nul or unsuccessful")
                        null
                    }
                }
            } catch (e: TimeoutException) {
                e.printStackTrace()
                null
            }
        }
    }
}
