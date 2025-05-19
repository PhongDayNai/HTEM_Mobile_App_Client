package com.watb.htem.payment

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.watb.htem.data.CartItem
import com.watb.htem.R
import com.watb.htem.main.dataStore
import com.watb.htem.helper.Helper
import com.watb.htem.ui.theme.HTEMTheme
import kotlinx.coroutines.launch

@Composable
fun PaymentScreen(navController: NavHostController, tableCode: String) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val width = screenWidth * 0.9f
    val height = (width.value * 16 / 10).dp
    var totalBill = 0
    var showDialog by remember { mutableStateOf(false) }
    val pointsUsedNumber = remember { mutableStateOf("") }
    val userPoints = remember { mutableIntStateOf(0) }
    val isLoggedIn = remember { mutableStateOf(true) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current
    val dataStore = context.dataStore
    val coroutineScope = rememberCoroutineScope()
    val cartItemsList = remember { mutableStateOf(emptyList<CartItem>()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoggedIn.value = Helper.isLoggedIn(context)
            cartItemsList.value = Helper.getCartItems(dataStore)
            userPoints.intValue = Helper.getUserPoints(context)
        }
    }

    cartItemsList.value.forEach { item ->
        if (item.status == "ordered") {
            totalBill += item.price * item.quantity
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.payment_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            val (userPointsRef, billRef, buttonRef) = createRefs()

            IconButton(
                onClick = {
                    navController.navigate("tableDetail/$tableCode")
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.constrainAs(buttonRef) {
                    top.linkTo(billRef.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.dark_purple)
                )
            ) {
                Text(
                    text = "Thanh toán"
                )
            }

            ConstraintLayout(
                modifier = Modifier
                    .width(width)
                    .height(height)
                    .constrainAs(billRef) {
                        top.linkTo(parent.top, margin = (-16).dp)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                val billContent = createRef()
                Image(
                    painter = painterResource(id = R.drawable.bill_background),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.8f)
                        .constrainAs(billContent) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start, margin = (width * 0.0185f))
                            end.linkTo(parent.end)
                        }
                ) {
                    val (titleRef, billCodeRef, billHeadingRef, billContentRef, billTotalRef, thankYouRef, addressRef) = createRefs()
                    val horizontalGuideline = createGuidelineFromTop(0.1f)

                    Text(
                        text = "Hóa đơn",
                        fontFamily = FontFamily(Font(R.font.bariol)),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(start = 24.dp)
                            .constrainAs(titleRef) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            }
                    )

                    Text(
                        text = "Mã hóa đơn: 123456789",
//                    fontFamily = FontFamily(Font(R.font.comic_sans_ms)),
                        fontSize = 10.sp,
                        modifier = Modifier
                            .constrainAs(billCodeRef) {
                                top.linkTo(horizontalGuideline)
                                end.linkTo(parent.end)
                            }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .padding(start = 2.dp, end = 2.dp)
                            .background(
                                color = colorResource(id = R.color.dark_purple),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .constrainAs(billHeadingRef) {
                                top.linkTo(billCodeRef.bottom)
                                start.linkTo(parent.start)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tên món",
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            color = colorResource(id = R.color.white),
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .weight(2.4f)
                        )
                        Text(
                            text = "Số lượng",
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            color = colorResource(id = R.color.white),
                            modifier = Modifier
                                .padding(start = 2.dp, end = 2.dp)
                                .weight(1f)
                        )
                        Text(
                            text = "Đơn giá",
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            color = colorResource(id = R.color.white),
                            modifier = Modifier
                                .padding(start = 2.dp, end = 2.dp)
                                .weight(1f)
                        )
                        Text(
                            text = "Thành tiền",
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            color = colorResource(id = R.color.white),
                            modifier = Modifier
                                .padding(start = 2.dp)
                                .weight(1.2f)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.32f)
                            .padding(start = 2.dp, end = 2.dp)
                            .constrainAs(billContentRef) {
                                top.linkTo(billHeadingRef.bottom)
                                start.linkTo(parent.start)
                            }
                    ) {
                        Spacer(modifier = Modifier.height(2.dp))
                        cartItemsList.value.forEach {item ->
                            if (item.status == "ordered") {
                                if (item.type == "set" || item.type == "Drink") {
                                    PaymentDetail(cartItem = item)
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(billTotalRef) {
                                top.linkTo(billContentRef.bottom, margin = 6.dp)
                                start.linkTo(parent.start, margin = 6.dp)
                            }
                    ) {
                        Text(
                            text = "Tổng tiền: ${Helper.formatCurrency(totalBill)}",
                            fontFamily = FontFamily(Font(R.font.bariol)),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Sử dụng ",
                                fontFamily = FontFamily(Font(R.font.bariol)),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            TextField(
                                value = pointsUsedNumber.value,
                                onValueChange = { newValue ->
                                    if (newValue != "") {
                                        if (newValue.toInt() > userPoints.intValue) {
                                            Toast.makeText(context, "Bạn không đủ điểm!", Toast.LENGTH_SHORT).show()
                                            pointsUsedNumber.value = ""
                                        } else if (newValue.toInt() * 1000 >= totalBill / 2) {
                                            Toast.makeText(context, "Không thể áp dụng quá một nửa giá trị hóa đơn!", Toast.LENGTH_SHORT).show()
                                            pointsUsedNumber.value = ""
                                        } else {
                                            pointsUsedNumber.value = newValue
                                        }
                                    } else {
                                        pointsUsedNumber.value = newValue
                                    }
                                },
                                textStyle = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.bariol)),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                placeholder = {
                                    Text(
                                        text = "0",
                                        fontFamily = FontFamily(Font(R.font.bariol)),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions {
                                    keyboardController?.hide()
                                },
                                modifier = Modifier.width(75.dp)
                            )
                            Text(
                                text = "điểm",
                                fontFamily = FontFamily(Font(R.font.bariol)),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    }
                    Text(
                        text = "Thank you!",
                        fontSize = 64.sp,
                        fontFamily = FontFamily(Font(R.font.italianno_regular)),
                        modifier = Modifier
                            .constrainAs(thankYouRef) {
                                bottom.linkTo(parent.bottom, margin = 18.dp)
                                start.linkTo(parent.start)
                            }
                    )
                    Text(
                        text = "Địa chỉ: 175 Tây Sơn, Đống Đa, Hà Nội",
                        fontSize = 10.sp,
                        modifier = Modifier
                            .constrainAs(addressRef) {
                                bottom.linkTo(parent.bottom, margin = (width * 0.03f))
                                end.linkTo(parent.end)
                            }
                    )
                }
            }
            if (isLoggedIn.value) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = R.color.white)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    modifier = Modifier
                        .constrainAs(userPointsRef) {
                            top.linkTo(parent.top, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                        }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "Số điểm: ${userPoints.intValue}",
                            modifier =  Modifier
                                .padding(top = 8.dp)
                        )
//                        Text(
//                            text = "Không áp dung điểm quá một nửa giá trị",
//                            fontFamily = FontFamily(Font(R.font.bariol)),
//                            fontWeight = FontWeight.SemiBold,
//                            fontSize = 12.sp
//                        )
//                        Text(
//                            text = "Không áp dung điểm",
//                            fontFamily = FontFamily(Font(R.font.bariol)),
//                            fontWeight = FontWeight.SemiBold,
//                            fontSize = 12.sp
//                        )
//                        Text(
//                            text = "quá một nửa giá trị",
//                            fontFamily = FontFamily(Font(R.font.bariol)),
//                            fontWeight = FontWeight.SemiBold,
//                            fontSize = 12.sp
//                        )
                        Text(
                            text = "(1 điểm = 1000 VND)",
                            fontFamily = FontFamily(Font(R.font.bariol)),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            if (showDialog) {
                if (pointsUsedNumber.value == "") {
                    pointsUsedNumber.value = "0"
                }
                CertainlyPayment(navController, tableCode, totalBill, pointsUsedNumber.value.toInt(), onDismiss = { showDialog = false })
            }
        }
    }
}

@Composable
fun PaymentDetail(
    cartItem: CartItem,
    modifier: Modifier = Modifier
) {
    val quantity = cartItem.quantity
    val total = cartItem.price * cartItem.quantity

    if (quantity > 0) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(22.dp)
                .padding(start = 2.dp, end = 2.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cartItem.name,
                    fontSize = 10.sp,
                    color = colorResource(id = R.color.dark_purple),
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .weight(2.25f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${cartItem.quantity}",
                    fontSize = 10.sp,
                    color = colorResource(id = R.color.dark_purple),
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp)
                        .weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = Helper.formatCurrency(cartItem.price),
                    fontSize = 10.sp,
                    color = colorResource(id = R.color.dark_purple),
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp)
                        .weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = Helper.formatCurrency(total),
                    fontSize = 10.sp,
                    color = colorResource(id = R.color.dark_purple),
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .weight(1.2f),
                    textAlign = TextAlign.Center
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 2.dp),
                thickness = 2.dp,
                color = colorResource(id = R.color.dark_purple)
            )
        }
    }
}

@Preview
@Composable
fun DisplayPreviewPayment() {
    HTEMTheme {
        PaymentScreen(rememberNavController(), "123")
    }
}
