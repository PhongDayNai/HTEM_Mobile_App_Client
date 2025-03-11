package com.watb.htem.menu

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.watb.htem.CartItem
import com.watb.htem.R
import com.watb.htem.USER_ID
import com.watb.htem.api.ApiClient
import com.watb.htem.dataStore
import com.watb.htem.helper.Helper
import com.watb.htem.userDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun EnterNumberScreen(
    navController: NavController,
    tableCode: String,
    setName: String,
    price: String,
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        EnterNumberPopup(navController, tableCode, setName, price, onDismiss = onDismiss)
    }
}

@Composable
fun EnterNumberPopup(navController: NavController, tableCode: String, setName: String, price: String, onDismiss: () -> Unit) {
    var number by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val dataStore = context.dataStore
    val coroutineScope = rememberCoroutineScope()
    val isLoggedIn = remember { mutableStateOf(false) }
    val userId = remember { mutableIntStateOf(0) }
    val buffetId = remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoggedIn.value = Helper.isLoggedIn(context)
            val preferences = context.userDataStore.data.first()
            userId.intValue = preferences[USER_ID]!!
            when (setName) {
                "Buffet Sashimi Hasu" -> {
                    buffetId.intValue = 1
                }
                "Buffet Sashimi Kiku" -> {
                    buffetId.intValue = 2
                }
                "Buffet Sashimi Sakura" -> {
                    buffetId.intValue = 3
                }
                "Buffet Sashimi Shinjuku" -> {
                    buffetId.intValue = 4
                }
                "Buffet Sashimi Hokaido" -> {
                    buffetId.intValue = 8
                }
                "Buffet lẩu" -> {
                    buffetId.intValue = 9
                }
                "Buffet lẩu hải sản" -> {
                    buffetId.intValue = 10
                }
                "Buffet nướng BBQ" -> {
                    buffetId.intValue = 11
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight(0.25f)
                    .fillMaxWidth(0.8f)
            ) {
                val (titleRef, dividerRef, itemsRef, closeButtonRef, orderButtonRef) = createRefs()
                Text(
                    text = "Welcome",
                    modifier = Modifier
                        .height(30.dp)
                        .constrainAs(titleRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        },
                    fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru))
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .constrainAs(dividerRef) {
                            top.linkTo(titleRef.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp, bottom = 50.dp)
                        .constrainAs(itemsRef) {
                            top.linkTo(dividerRef.bottom)
                            bottom.linkTo(closeButtonRef.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    TextField(
                        value = number,
                        onValueChange = { number = it },
                        label = {
                            Text(
                                text = "Số lượng",
//                                fontFamily = FontFamily(Font(resId = R.font.romajimincho))
                            )
                        },
                        placeholder = { Text(text = "0") },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    number = ""
                                },
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (number == "") {
                                    Toast.makeText(context, "Vui lòng nhập số lượng.", Toast.LENGTH_SHORT).show()
                                } else {
                                    keyboardController?.hide()
                                    isLoading = true
                                    val intPrice = price.replace("đ","").replace(".", "").toInt()
                                    val item = CartItem(name = setName, quantity = number.toInt(), price = intPrice, status = "ordered", type = "set")
                                    coroutineScope.launch {
                                        if (isLoggedIn.value) {
                                            val response = ApiClient.userBuffetOrder(userId.intValue, buffetId.intValue, tableCode.toInt(), number.toInt(), intPrice * number.toInt())
                                            Log.d("Register", "Register response: $response")
                                            if (ApiClient.getStatusCode() != 201) {
                                                Toast.makeText(context, "Không gửi được đơn hàng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
                                                isLoading = false
                                                return@launch
                                            }
                                            message = response?.message ?: response?.error ?: "Order Buffet failed"
                                            if (response?.orderId != null) {
                                                Helper.saveOrderID(context, response.orderId)
                                            }
                                            Helper.addToCart(dataStore = dataStore, newItem = item)
                                            Helper.printDataStore(dataStore)
                                            isLoading = false
//                                            navController.navigate("foodMenu/$tableCode/$setName")
                                            navController.navigate("foodMenu/$setName")
                                        } else {
                                            val response = ApiClient.guessBuffetOrder(buffetId.intValue, tableCode.toInt(), number.toInt(), intPrice * number.toInt())
                                            Log.d("Register", "Register response: $response")
                                            if (ApiClient.getStatusCode() != 201) {
                                                Toast.makeText(context, "Không gửi được đơn hàng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
                                                isLoading = false
                                                return@launch
                                            }
                                            message = response?.message ?: response?.error ?: "Order failed"
                                            if (response?.orderId != null) {
                                                Helper.saveOrderID(context, response.orderId)
                                            }
                                            Helper.addToCart(dataStore = dataStore, newItem = item)
                                            Helper.printDataStore(dataStore)
                                            isLoading = false
//                                            navController.navigate("foodMenu/$tableCode/$setName")
                                            navController.navigate("foodMenu/$setName")
                                        }
                                    }
                                }
                            }
                        )
                    )
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .height(50.dp)
                        .background(Color.Transparent, shape = RoundedCornerShape(2.dp))
                        .constrainAs(closeButtonRef) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                ) {
                    Text(
                        text = "Đóng",
                        color = Color.Blue
                    )
                }
                TextButton(
                    onClick = {
                        if (number == "") {
                            Toast.makeText(context, "Vui lòng nhập số lượng.", Toast.LENGTH_SHORT).show()
                        } else {
                            keyboardController?.hide()
                            isLoading = true
                            val intPrice = price.replace("đ","").replace(".", "").toInt()
                            val item = CartItem(name = setName, quantity = number.toInt(), price = intPrice, status = "ordered", type = "set")
                            coroutineScope.launch {
                                if (isLoggedIn.value) {
                                    val response = ApiClient.userBuffetOrder(userId.intValue, buffetId.intValue, tableCode.toInt(), number.toInt(), intPrice * number.toInt())
                                    Log.d("Register", "Register response: $response")
                                    if (ApiClient.getStatusCode() != 201) {
                                        Toast.makeText(context, "Không gửi được đơn hàng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
                                        isLoading = false
                                        return@launch
                                    }
                                    message = response?.message ?: response?.error ?: "Order failed"
                                    if (response?.orderId != null) {
                                        Helper.saveOrderID(context, response.orderId)
                                    }
                                    Helper.addToCart(dataStore = dataStore, newItem = item)
                                    Helper.printDataStore(dataStore)
                                    isLoading = false
//                                    navController.navigate("foodMenu/$tableCode/$setName")
                                    navController.navigate("foodMenu/$setName")
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .background(Color.Transparent, shape = RoundedCornerShape(2.dp))
                        .constrainAs(orderButtonRef) {
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        }
                ) {
                    Text(
                        text = "Select",
                        color = Color.Red
                    )
                }
                if (isLoading) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(200.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = Color.Red,
                                    modifier = Modifier.size(150.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
