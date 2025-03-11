package com.watb.htem.welcome

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import com.watb.htem.FULL_NAME
import com.watb.htem.R
import com.watb.htem.USER_ID
import com.watb.htem.account.ProfileScreen
import com.watb.htem.account.SignInScreen
import com.watb.htem.api.ApiClient
import com.watb.htem.api.Constant
import com.watb.htem.helper.Helper
import com.watb.htem.userDataStore
import kotlinx.coroutines.launch

@Preview
@Composable
fun WelcomePreview() {
    val navController = rememberNavController()
//    WelcomeScreen(navController)
    SignInScreen(navController)
}

@Composable
fun WelcomeScreen(navController: NavHostController) {
    var showDialog by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val userDataStore = context.userDataStore
    val isLoggedIn = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val fullName = remember { mutableStateOf("") }
    var showProfile by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val userId = remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoggedIn.value = Helper.isLoggedIn(context)
            val preferences = context.userDataStore.data.first()
            fullName.value = preferences[FULL_NAME] ?: ""
            userId.intValue = preferences[USER_ID] ?: 0
        }
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val horizontalGuideline = createGuidelineFromTop(0.76f)
        val horizontalGuideline2 = createGuidelineFromTop(0.275f)
        val (buttonRef, guessRef, resNameRef) = createRefs()

        Image(
            painter = painterResource(R.drawable.welcome_background),
            contentDescription = "Welcome",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillHeight
        )

        Text(
            text = "SASHIMI\nBBQ GARDEN",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.white),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru)),
            letterSpacing = 3.sp,
            style = TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black,
                    offset = Offset(4f, 4f),
                    blurRadius = 4f
                )
            ),
            modifier = Modifier.constrainAs(resNameRef) {
                top.linkTo(horizontalGuideline2)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        if (isLoggedIn.value) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(buttonRef) {
                        top.linkTo(horizontalGuideline, margin = (-24).dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(
                    text = "Welcome",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(id = R.color.white),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru)),
                    style = TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorResource(id = R.color.white)),
                        modifier = Modifier
                            .size(28.dp)
                            .padding(end = 8.dp)
                            .clickable {
                                coroutineScope.launch {
                                    isLoading = true
                                    val response = ApiClient.getUserPoints(userId.intValue)
                                    if (ApiClient.getStatusCode() != 200) {
                                        Toast.makeText(context, "Không cập nhật được điểm. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
                                        isLoading = false
                                        return@launch // Dừng thực hiện tiếp
                                    }
                                    if (response != null) {
                                        Helper.saveUserPoints(context, response)
                                        showProfile = true
                                    }
                                    isLoading = false
                                }
                            }
                    )
                    Text(
                        text = "${fullName.value}!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(id = R.color.white),
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru)),
                        style = TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }
                Button(
                    onClick = {
                        navController.navigate("qrScanner") {
                            popUpTo("home") {
                                inclusive = false
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 16.dp,
                        pressedElevation = 20.dp
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    Text(
                        text = "Vào trong"
                    )
                }
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            Helper.clearDataStore(dataStore = userDataStore)
                            navController.navigate("home") {
                                popUpTo("home") {
                                    inclusive = false
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        text = "Đăng xuất",
                        color = colorResource(id = R.color.light_blue),
                        style = TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }
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
        } else {
            Button(
                onClick = {
                    navController.navigate("signIn") {
                        popUpTo("home") {
                            inclusive = false
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 16.dp,
                    pressedElevation = 20.dp
                ),
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(buttonRef) {
                        top.linkTo(horizontalGuideline)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(
                    text = "Đăng nhập",
                    color = Color.White
                )
            }
            TextButton(
                onClick = {
                    navController.navigate("qrScanner") {
//                    popUpTo("home") {
//                        inclusive = true
//                    }
                    }
                },
                modifier = Modifier.constrainAs(guessRef) {
                    top.linkTo(buttonRef.bottom, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 16.dp,
                    pressedElevation = 20.dp
                )
            ) {
                Text(
                    text = "Tiếp tục với vai trò khách vãng lai",
                    color = Color.White
                )
            }
        }

        if (showDialog) {
            EnterMainLink(onDismiss = { showDialog = false })
        }
        ProfileScreen(navController, showProfile, onDismiss = { showProfile = false })
    }
}

@Composable
fun EnterMainLink(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.white), shape = RoundedCornerShape(4.dp))
            ) {
                val (textRef, buttonRef) = createRefs()
                val horizontalGuideline = createGuidelineFromTop(0.5f)
                var link by remember { mutableStateOf("") }

                TextField(
                    value = link,
                    onValueChange = { newString ->
                        link = newString
                    },
                    modifier = Modifier.constrainAs(textRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(horizontalGuideline)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

                Button(
                    onClick = {
                        Constant.setMainUrl(link)
                        Log.d("URL", Constant.Main_Url)
                    },
                    modifier = Modifier.constrainAs(buttonRef) {
                        top.linkTo(horizontalGuideline)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ) {
                    Text(
                        text = "Enter"
                    )
                }
            }
        }
    }
}
