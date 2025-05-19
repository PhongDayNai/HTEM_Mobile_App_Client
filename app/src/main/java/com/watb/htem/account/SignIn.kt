package com.watb.htem.account

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.watb.htem.R
import com.watb.htem.api.ApiClient
import com.watb.htem.helper.Helper
import com.watb.htem.main.userDataStore
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(navController: NavHostController) {
    val context = LocalContext.current
    var isShowPassword by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val passwordRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val userDataStore = context.userDataStore

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.sign_in_up_scan),
            contentDescription = "Background Image",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = 0.5f),
            contentScale = ContentScale.FillHeight
        )
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            val horizontalGuideline = createGuidelineFromTop(0.5f)
            val (titleRef, usernameRef, passwordRef, guessRef, signInRef, forgotPasswordRef, registerRef, logoRef, backRef) = createRefs()
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            IconButton(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = false
                        }
                    }
                },
                modifier = Modifier.constrainAs(backRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }

            Image(
                painter = painterResource(id = R.drawable.logo_res),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .constrainAs(logoRef) {
                        top.linkTo(parent.top, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                    }
            )

            Text(
                text = "Đăng nhập",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru)),
                modifier = Modifier.constrainAs(titleRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(usernameRef.top, margin = 54.dp)
                }
            )

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Số điện thoại") },
                trailingIcon = {
                    IconButton(
                        onClick = { username = "" }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = null
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 1,
                singleLine = false,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Phone
                ),
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .wrapContentHeight()
                    .constrainAs(usernameRef) {
                        bottom.linkTo(passwordRef.top, margin = 40.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                trailingIcon = {
                    IconButton(
                        onClick = { isShowPassword = !isShowPassword }
                    ) {
                        Image(
                            painter = if (isShowPassword) painterResource(R.drawable.open) else painterResource(R.drawable.close),
                            contentDescription = null,
                            modifier = Modifier.height(24.dp)
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 1,
                singleLine = false,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .wrapContentHeight()
                    .focusRequester(passwordRequester)
                    .constrainAs(passwordRef) {
                        bottom.linkTo(horizontalGuideline)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        passwordRequester.requestFocus()
                        keyboardController?.hide()
                    }
                ),
                visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation()
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        val response = ApiClient.login(username, password)
                        Log.d("Login", "Login response: $response")
                        message = response?.message ?: response?.error ?: "Login failed"
                        if (response != null) {
                            message?.let {
                                if (message == "Đăng nhập thành công") {
                                    keyboardController?.hide()
                                    Helper.saveLoginState(context, true, response)
                                    Helper.printDataStoreUser(userDataStore)
                                    navController.navigate("qrScanner")
                                }
                            }
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(signInRef) {
                        top.linkTo(horizontalGuideline, margin = 28.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = colorResource(R.color.white)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 16.dp,
                    pressedElevation = 20.dp
                )
            ) {
                Text(text = "Đăng nhập")
            }

            TextButton(
                onClick = {
//                navController.navigate("forgotPassword")
                },
                modifier = Modifier
                    .constrainAs(forgotPasswordRef) {
                        top.linkTo(signInRef.bottom, margin = (-6).dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Quên mật khẩu?"
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(registerRef) {
                        top.linkTo(forgotPasswordRef.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(
                    text = "Bạn chưa có tài khoản?",
                    color = Color.Black
                )
                TextButton(
                    onClick = {
                        navController.navigate("signUp")
                    }
                ) {
                    Text(
                        text = "Đăng ký ngay",
                    )
                }
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
                    top.linkTo(registerRef.bottom, margin = (-16).dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            ) {
                Text(
                    text = "Tiếp tục với vai trò khách vãng lai",
                    color = Color.Black
                )
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
}
