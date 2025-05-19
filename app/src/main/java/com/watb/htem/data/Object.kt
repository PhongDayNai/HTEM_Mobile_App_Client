package com.watb.htem.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Food(
    @DrawableRes val image: Int,
    @StringRes val name: Int,
    @StringRes val shortDescription: Int,
    @StringRes val description: Int,
    @StringRes val type: Int
)

data class Set(
    @DrawableRes val image: Int,
    @StringRes val name: Int,
    @StringRes val price: Int,
    val details: List<Food>
)

data class CartItem(
    val name: String,
    var quantity: Int,
    val price: Int,
    var status: String,
    val type: String
)

data class LoginRequest(
    val phone: String,
    val password: String
)

data class RegisterRequest(
    val fullName: String,
    val phone: String,
    val password: String
)

data class LoginResponse(
    val message: String? = null,
    val userId: Int? = null,
    val fullName: String? = null,
    val points: Int? = null,
    val error: String? = null
)

data class UserPointsRequest(
    val userId: Int
)

data class UserPointsResponse(
    val userId: Int? = null,
    val points: Int? = null,
    val error: String? = null
)

data class UserTransactionRequest(
    val userId: Int
)

data class Transaction(
    val paymentDate: String,
    val amount: Int
)

data class UserTransactionResponse(
    val transactionHistory: List<Transaction>
)

data class UserBuffetOrderRequest(
    val userId: Int,
    val buffetId: Int,
    val tableId: Int,
    val quantity: Int,
    val totalPrice: Int
)

data class GuessBuffetOrderRequest(
    val buffetId: Int,
    val tableId: Int,
    val quantity: Int,
    val totalPrice: Int
)

data class BuffetOrderResponse(
    val message: String? = null,
    val orderId: Int? = null,
    val error: String? = null
)

data class Dish(
    val dishName: String,
    val quantity: Int
)

data class DishesOrderRequest(
    val orderId: Int,
    val selectedDishes: List<Dish>
)

data class DishesOrderResponse(
    val orderId: Int? = null,
    val totalPrice: Int? = null,
    val error: String? = null
)

data class PaymentUserRequest(
    val userId: Int,
    val orderId: Int,
    val usingPoints: Int,
    val pointsUsedNumber: Int,
    val amount: Int,
    val paymentMethod: String
)

data class PaymentUserResponse(
    val paymentId: Int? = null,
    val nowPoints: Int? = null,
    val error: String? = null
)

data class PaymentGuessRequest(
    val orderId: Int,
    val amount: Int,
    val paymentMethod: String
)

data class PaymentGuessResponse(
    val paymentId: Int? = null,
    val error: String? = null
)

data class GetServedDishesRequest(
    val orderId: Int
)

data class GetServedDishesResponse(
    val servedDishes: List<ServedDish>
)

data class ServedDish(
    val DishName: String,
    val ServedQuantity: Int
)

data class CallStaffRequest(
    val tableId: Int,
)

data class CallStaffResponse(
    val isSuccess: Boolean? = null,
    val message: String? = null
)

data class CheckPaymentStateRequest(
    val orderId: Int
)

data class CheckPaymentStateResponse(
    val paymentState: String? = null,
    val message: String? = null
)