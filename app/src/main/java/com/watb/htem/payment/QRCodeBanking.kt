package com.watb.htem.payment

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.watb.htem.main.ORDER_ID
import com.watb.htem.R
import com.watb.htem.main.USER_ID
import com.watb.htem.api.ApiClient
import com.watb.htem.main.dataStore
import com.watb.htem.helper.Helper
import com.watb.htem.main.userDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun QRCodeScreen(navController: NavHostController, totalBill: Int, pointsUsedNumber: Int) {
    val isLoggedIn = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val orderId = remember { mutableIntStateOf(0) }
    val userId = remember { mutableIntStateOf(0) }
    val usingPoints = remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoggedIn.value = Helper.isLoggedIn(context)
            if (isLoggedIn.value) {
                val preferencesUser = context.userDataStore.data.first()
                val preferencesCart = context.dataStore.data.first()
                userId.intValue = preferencesUser[USER_ID] ?: 0
                orderId.intValue = preferencesCart[ORDER_ID] ?: 0
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))
            .safeDrawingPadding()
    ) {
        val (qrRef, nameRef, stkRef, amountRef, btnRef) = createRefs()
        val verticalGuideline = createGuidelineFromTop(0.275f)

        Text(
            text = "DUONG HUNG PHONG",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.constrainAs(nameRef) {
                bottom.linkTo(verticalGuideline)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Text(
            text = "Số tài khoản: 19626062004 - TP Bank",
            fontWeight = FontWeight.Normal,
            modifier = Modifier.constrainAs(stkRef) {
                top.linkTo(nameRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Box(
            modifier = Modifier.constrainAs(qrRef) {
                top.linkTo(stkRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            QRCodeBanking(totalBill)
        }
        Text(
            text = "Số tiền: ${Helper.formatCurrency(totalBill)} VND",
            modifier = Modifier.constrainAs(amountRef) {
                top.linkTo(qrRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    if (isLoggedIn.value) {
                        if (pointsUsedNumber == 0) {
                            usingPoints.intValue = 0
                        } else {
                            usingPoints.intValue = 1
                        }
                        val response = ApiClient.paymentUser(userId = userId.intValue, orderId = orderId.intValue, usingPoints = usingPoints.intValue, pointsUsedNumber = pointsUsedNumber, amount = totalBill, paymentMethod = "banking")
                        if (ApiClient.getStatusCode() != 201) {
                            Toast.makeText(context, "Thanh toán không thành công. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            return@launch
                        } else {
                            if (response != null) {
                                if (isLoggedIn.value) Helper.saveUserPoints(context, response.nowPoints ?: 0)
                                Helper.setPaidState(context, true)
                                Helper.clearDataStore(context.dataStore)
                                navController.navigate("paymentSuccessful") {
                                    popUpTo("paymentSuccessful") {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    } else {
                        val response = ApiClient.paymentGuess(orderId = orderId.intValue, amount = totalBill, paymentMethod = "banking")
                        if (ApiClient.getStatusCode() != 201) {
                            Toast.makeText(context, "Thanh toán không thành công. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            return@launch
                        } else {
                            if (response != null) {
                                Helper.setPaidState(context, true)
                                Helper.clearDataStore(context.dataStore)
                                navController.navigate("paymentSuccessful") {
                                    popUpTo("paymentSuccessful") {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                    isLoading = false
                }
            },
            modifier = Modifier.constrainAs(btnRef) {
                top.linkTo(amountRef.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Text(text = "Hoàn thành")
        }
        if (isLoading) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
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

@Composable
fun QRCodeBanking(totalBill: Int) {
    val context = LocalContext.current
    val qr = VietQR()
    val qrCodeString = qr
        .setBeneficiaryOrganization("970423", "19626062004")
        .setTransactionAmount(totalBill.toString())
        .setAdditionalDataFieldTemplate("Thanh toan QR")
        .build()
    val qrCodeBitmap = generateQRCodeImage(qrCodeString, context)

    if (qrCodeBitmap != null) {
        Image(
            bitmap = qrCodeBitmap.asImageBitmap(),
            contentDescription = "QR Code Banking",
            modifier = Modifier.size(200.dp)
        )
    } else {
        Text(text = "Có lỗi khi tạo mã QR Banking")
    }
}

@Preview
@Composable
fun PreviewQRCodeBanking() {
    val navConstant = rememberNavController()
    QRCodeScreen(navConstant, 10000, 0)
}