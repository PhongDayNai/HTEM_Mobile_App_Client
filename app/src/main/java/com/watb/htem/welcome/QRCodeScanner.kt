package com.watb.htem.welcome

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.journeyapps.barcodescanner.CaptureActivity
import com.watb.htem.R
import com.watb.htem.helper.Helper
import kotlinx.coroutines.launch

@Composable
fun QRCodeScannerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    QRCodeScannerContent(navController) { result ->
        coroutineScope.launch {
            Helper.saveTableCode(context, result)
            navController.navigate("tableDetail/$result")
        }
    }
}

@Composable
fun QRCodeScannerContent(navController: NavHostController, onScanResult: (String) -> Unit) {
    val context = LocalContext.current
    val qrLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val resultString = data?.getStringExtra("SCAN_RESULT")
            if (resultString != null) {
                onScanResult(resultString)
            } else {
                Toast.makeText(context, "Không có kết quả quét.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Quét không thành công hoặc bị hủy.", Toast.LENGTH_SHORT).show()
        }
    }

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
                .safeDrawingPadding(),
        ) {
            val horizontalGuideline = createGuidelineFromTop(0.5f)
            val (textRef, buttonQRRef, buttonSelectRef) = createRefs()

            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        val intent = Intent(context, CaptureActivity::class.java)
                        qrLauncher.launch(intent)
                    } else {
                        ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CODE)
                    }
                },
                modifier = Modifier.constrainAs(buttonQRRef) {
                    top.linkTo(horizontalGuideline, margin = 40.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.light_blue),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 16.dp,
                    pressedElevation = 20.dp
                )
            ) {
                Text("Quét Mã QR")
            }

            Button(
                onClick = {
                    navController.navigate("tableSelection")
                },
                modifier = Modifier.constrainAs(buttonSelectRef) {
                    top.linkTo(horizontalGuideline, margin = 100.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.light_blue),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 16.dp,
                    pressedElevation = 20.dp
                )
            ) {
                Text("Chọn bàn")
            }

            Text(
                text = "Quét mã bàn\nđể gọi món",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = Color.Black,
                    fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru)),
                    fontSize = 50.sp
                ),
                modifier = Modifier.constrainAs(textRef) {
                    bottom.linkTo(horizontalGuideline, margin = 90.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            IconButton(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = false
                        }
                    }
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    }
}

private const val REQUEST_CODE = 1001
