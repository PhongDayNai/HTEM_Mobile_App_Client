package com.watb.htem.account

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.watb.htem.CommonSpaceColumn
import com.watb.htem.FULL_NAME
import com.watb.htem.R
import com.watb.htem.Transaction
import com.watb.htem.USER_ID
import com.watb.htem.USER_POINTS
import com.watb.htem.api.ApiClient
import com.watb.htem.userDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.core.net.toUri
import com.watb.htem.helper.Helper

@Composable
fun ProfileScreen(navController: NavHostController, showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        Profile(navController, onDismiss = onDismiss)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(navController: NavHostController, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val fullName = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val userId = remember { mutableIntStateOf(0) }
    val userPoints = remember { mutableIntStateOf(0) }
    val isLoading = remember { mutableStateOf(false) }
    val isShowTransaction = remember { mutableStateOf(false) }
    val isShowContact = remember { mutableStateOf(true) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val transactionHistory = mutableListOf<Transaction>()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoggedIn.value = Helper.isLoggedIn(context)
            val preferences = context.userDataStore.data.first()
            userId.intValue = preferences[USER_ID] ?: 0
            fullName.value = preferences[FULL_NAME] ?: ""
            userPoints.intValue = preferences[USER_POINTS] ?: 0
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.5f)
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(id = R.color.white))
            ) {
                val (cardRef, transactionHistoryRef, settingsRef, contactRef, btnRef) = createRefs()

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = R.color.light_gray)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                        .constrainAs(cardRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(175.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile_image),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(125.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CommonSpaceColumn()
                            if (isLoggedIn.value) {
                                Text(
                                    text = "Welcome\n${fullName.value}!",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru)),
                                    modifier = Modifier.width(200.dp)
                                )
                                CommonSpaceColumn()
                                Text(
                                    text = "Tổng điểm",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru))
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = userPoints.intValue.toString(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        textAlign = TextAlign.Center,
                                        fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru))
                                    )
                                    Image(
                                        painter = painterResource(id = R.drawable.coin),
                                        contentDescription = "Point",
                                        Modifier.size(30.dp)
                                    )
                                }
                            } else {
                                Text(
                                    text = "Vui lòng đăng nhập hoặc đăng ký tài khoản",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru))
                                )
                            }
                        }
                    }
                }

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                        .constrainAs(transactionHistoryRef) {
                            top.linkTo(cardRef.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    val (titleRef, contentRef) = createRefs()

                    if (isShowTransaction.value) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(1.dp, Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.25f)
                                .padding(start = 20.dp, end = 20.dp)
                                .constrainAs(contentRef) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }
                        ) {
                            CommonSpaceColumn()
                            CommonSpaceColumn()
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .verticalScroll(state = rememberScrollState())
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .padding(start = 8.dp, end = 8.dp)
                                ) {
                                    Text(
                                        text = "Thời điểm",
                                        textAlign = TextAlign.Center
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        thickness = 2.dp,
                                        color = Color.Black
                                    )
                                    transactionHistory.forEach {
                                        Log.d("Profile", it.paymentDate)
                                        // Chuyển đổi từ chuỗi ISO 8601 sang Instant
                                        val instant = Instant.parse(it.paymentDate)
                                        // Chuyển đổi Instant sang LocalDateTime tại múi giờ mặc định (hoặc múi giờ bạn muốn)
                                        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

                                        // Định dạng lại LocalDateTime thành chuỗi theo định dạng mong muốn
                                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                        val formattedDateTime = localDateTime.format(formatter)
                                        Text(
                                            text = formattedDateTime,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, end = 8.dp)
                                ) {
                                    Text(
                                        text = "Số tiền",
                                        textAlign = TextAlign.Center
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        thickness = 2.dp,
                                        color = Color.Black
                                    )
                                    transactionHistory.forEach {
                                        Text(
                                            text = Helper.formatCurrency(it.amount),
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(id = R.color.light_gray)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .constrainAs(titleRef) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = "Lịch sử giao dịch")
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = {
                                    if (isShowTransaction.value) {
                                        isShowTransaction.value = false
                                    } else {
                                        coroutineScope.launch {
                                            if (isLoggedIn.value) {
                                                isLoading.value = true
                                                val response = ApiClient.getUserTransaction(userId.intValue)
                                                Log.d("Profile", response.toString())
                                                if (ApiClient.getStatusCode() != 200) {
                                                    Toast.makeText(context, "Không cập nhật được lịch sử giao dịch. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
//                                    response = ApiClient.getUserTransaction(userId.value)
                                                    isLoading.value = false
                                                    return@launch
                                                }
                                                if (response != null) {
                                                    transactionHistory.clear()
                                                    transactionHistory.addAll(response.transactionHistory)
                                                    isShowTransaction.value = !isShowTransaction.value
                                                    isLoading.value = false
                                                }
                                            } else {
                                                Toast.makeText(context, "Vui lòng đăng nhập để sử dụng tính năng này.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                },
                                enabled = isLoggedIn.value,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    if (isShowTransaction.value) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                        .constrainAs(settingsRef) {
                            top.linkTo(transactionHistoryRef.bottom, margin = 0.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .clickable(
                            enabled = isLoggedIn.value
                        ) {  }
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(id = R.color.light_gray)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .constrainAs(createRef()) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    ) {
                        Text(
                            text = "Thay đổi mật khẩu",
                            Modifier.padding(8.dp)
                        )
                    }
                }

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                        .constrainAs(contactRef) {
                            top.linkTo(settingsRef.bottom, margin = 0.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    val (titleRef, contentRef) = createRefs()

                    if (isShowContact.value) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(1.dp, Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 20.dp, end = 20.dp)
                                .constrainAs(contentRef) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }
                        ) {
                            CommonSpaceColumn()
                            CommonSpaceColumn()
                            Column(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .padding(start = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.phone),
                                        contentDescription = "Call",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "0855576569",
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = "tel:0855576569".toUri()
                                                }
                                                context.startActivity(intent)
                                            }
                                    )
                                }
                                Row(
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.mail),
                                        contentDescription = "Mail",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "buffetbbq24@gmail.com",
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                    data = "mailto:buffetbbq24@gmail.com".toUri()
                                                }
                                                context.startActivity(intent)
                                            }
                                    )
                                }
                                Row(
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.facebook),
                                        contentDescription = "Call",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Sashimi BBQ Garden",
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                    data = "https://www.facebook.com/LangAmThucNhatBan".toUri()
                                                }
                                                context.startActivity(intent)
                                            }
                                    )
                                }
                            }
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(id = R.color.light_gray)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .constrainAs(titleRef) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = "Liên hệ với chúng tôi")
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = {},
                                modifier = Modifier.size(24.dp),
//                                enabled = if (isLoggedIn.value) true else false,
                            ) {
                                Icon(
                                    if (isShowTransaction.value) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        isLoading.value = true
                        coroutineScope.launch {
                            Helper.clearDataStore(dataStore = context.userDataStore)
                            navController.navigate("home") {
                                popUpTo("home") {
                                    inclusive = true
                                }
                            }
                        }
                        isLoading.value = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.light_gray)
                    ),
                    modifier = Modifier.constrainAs(btnRef) {
                        top.linkTo(contactRef.bottom, margin = 20.dp)
                        end.linkTo(parent.end, margin = 16.dp)

                    },
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    ),
                    enabled = isLoggedIn.value
                ) {
                    Text(
                        text = "Đăng xuất",
                        color = Color.Black
                    )
                }

                if (isLoading.value) {
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
    }
}

@Composable
fun ProfilePre() {
    val context = LocalContext.current
    val fullName = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val userPoints = remember { mutableIntStateOf(0) }
    val isShowTransaction = remember { mutableStateOf(true) }
    val isShowContact = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val preferences = context.userDataStore.data.first()
            fullName.value = preferences[FULL_NAME] ?: ""
            userPoints.intValue = preferences[USER_POINTS] ?: 0
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))
    ) {
        val (cardRef, transactionHistoryRef, settingsRef, contactRef, btnRef) = createRefs()

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.light_gray)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                .constrainAs(cardRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Box(
                    modifier = Modifier.size(175.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_image),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(125.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 0.dp)
                ) {
                    CommonSpaceColumn()
                    Text(
                        text = "Welcome\nDuong Hung Phong!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru)),
                        modifier = Modifier.width(200.dp)
                    )
                    CommonSpaceColumn()
                    Text(
                        text = "Tổng điểm",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru))
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = userPoints.intValue.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru))
                        )
                        Image(
                            painter = painterResource(id = R.drawable.coin),
                            contentDescription = "Point",
                            Modifier.size(30.dp)
                        )
                    }
                }
            }
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                .constrainAs(transactionHistoryRef) {
                    top.linkTo(cardRef.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            val (titleRef, contentRef) = createRefs()

            if (isShowTransaction.value) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.25f)
                        .padding(start = 20.dp, end = 20.dp)
                        .constrainAs(contentRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    CommonSpaceColumn()
                    CommonSpaceColumn()
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth(0.4925f)
                                .padding(start = 8.dp, end = 8.dp)
                        ) {
                            Text(
                                text = "Thời điểm",
                                textAlign = TextAlign.Center
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 3.dp),
                                thickness = 2.dp,
                                color = Color.Black
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(top = 8.dp, bottom = 8.dp),
                            thickness = 1.dp,
                            color = Color.Black
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth(0.99f)
                                .padding(start = 8.dp, end = 8.dp)
                        ) {
                            Text(
                                text = "Số tiền thanh toán",
                                textAlign = TextAlign.Center
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 3.dp),
                                thickness = 2.dp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.light_gray)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .constrainAs(titleRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Lịch sử giao dịch")
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { isShowTransaction.value = !isShowTransaction.value },
//                        enabled = if (isLoggedIn.value) true else false,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            if (isShowTransaction.value) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                }
            }
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                .constrainAs(settingsRef) {
                    top.linkTo(transactionHistoryRef.bottom, margin = 0.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .clickable {  }
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.light_gray)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .constrainAs(createRef()) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(
                    text = "Thay đổi mật khẩu",
                    Modifier.padding(8.dp)
                )
            }
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                .constrainAs(contactRef) {
                    top.linkTo(settingsRef.bottom, margin = 0.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            val (titleRef, contentRef) = createRefs()

            if (isShowContact.value) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 20.dp, end = 20.dp)
                        .constrainAs(contentRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    CommonSpaceColumn()
                    CommonSpaceColumn()
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(start = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.phone),
                                contentDescription = "Call",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "0855576569",
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = "tel:0855576569".toUri()
                                        }
                                        context.startActivity(intent)
                                    }
                            )
                        }
                        Row(
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.mail),
                                contentDescription = "Mail",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "buffetbbq24@gmail.com",
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = "mailto:buffetbbq24@gmail.com".toUri()
                                        }
                                        context.startActivity(intent)
                                    }
                            )
                        }
                        Row(
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.facebook),
                                contentDescription = "Call",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Sashimi BBQ Garden",
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data =
                                                "https://www.facebook.com/LangAmThucNhatBan".toUri()
                                        }
                                        context.startActivity(intent)
                                    }
                            )
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.light_gray)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .constrainAs(titleRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Liên hệ với chúng tôi")
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            isShowContact.value = !isShowContact.value
                        },
                        modifier = Modifier.size(24.dp),
//                        enabled = if (isLoggedIn.value) true else false,
                    ) {
                        Icon(
                            if (isShowTransaction.value) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.light_gray)
            ),
            modifier = Modifier.constrainAs(btnRef) {
                top.linkTo(contactRef.bottom, margin = 20.dp)
                end.linkTo(parent.end, margin = 16.dp)

            },
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = "Đăng xuất",
                color = Color.Black
            )
        }
    }
}

@Preview
@Composable
fun ProfilePreview() {
    ProfilePre()
}