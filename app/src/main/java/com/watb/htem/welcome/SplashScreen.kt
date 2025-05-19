package com.watb.htem.welcome

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.watb.htem.api.Constant
import com.watb.htem.api.SocketManager
import com.watb.htem.helper.Helper

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    var isPaid by remember { mutableStateOf(true) }
    var tableCode by remember { mutableStateOf("") }
    var isShowDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val orderId = Helper.getOrderID(context)
        Log.d("OrderID", "orderId: $orderId")
        if (orderId != 0) {
            isPaid = Helper.isPaid(context)
            Log.d("isPaid", "isPaid: $isPaid")
            tableCode = Helper.getTableCode(context)
        }
    }

    Button(
        onClick = {
            if (Constant.Main_Url == "") {
                isShowDialog = true
            } else {
                SocketManager.initialize()
                if (isPaid) {
                    navController.navigate("home") {
                        popUpTo("splash") {
                            inclusive = true
                        }
                    }
                } else {
                    navController.navigate("tableDetail/$tableCode") {
                        popUpTo("splash") {
                            inclusive = true
                        }
                    }
                }
            }
        }
    ) {
        Text(
            text = "Enter"
        )
    }

    if (isShowDialog) {
        EnterMainLink(
            onDismiss = { isShowDialog = false },
        )
    }
}