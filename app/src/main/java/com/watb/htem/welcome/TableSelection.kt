package com.watb.htem.welcome

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.watb.htem.R
import com.watb.htem.helper.Helper
import kotlinx.coroutines.launch

@SuppressLint("MemberExtensionConflict")
@Composable
fun TableSelection(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.table_selection_bg),
            contentDescription = "Background Image",
            contentScale = ContentScale.FillHeight,
            alpha = 0.5f,
            modifier = Modifier
                .fillMaxSize()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(state = rememberScrollState())
        ) {
            Text(
                text = "CHỌN BÀN",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru)),
                letterSpacing = 3.sp,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(4f, 4f),
                        blurRadius = 4f
                    )
                ),
            )
            for (index in 1..5) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth(0.875f)
                ) {
                    for (i in 1..2) {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    Helper.saveTableCode(context, (index * 100 + i).toString())
                                    navController.navigate("tableDetail/${index * 100 + i}")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFFFFF).copy(alpha = 0.075f)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
//                                .background(color = Color(0xFFFFFFFF).copy(alpha = 0.075f), shape = RoundedCornerShape(12.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.table),
                                    contentDescription = "Table",
                                    modifier = Modifier
                                        .width(130.dp)
                                )
                                Text(
                                    text = "Bàn ${index * 100 + i}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W600,
                                    letterSpacing = (0.5).sp,
                                    fontFamily = FontFamily(Font(R.font.bariol)),
                                    color = Color(0xFFFFFFFF)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TableSelectionPreview() {
    val navController = rememberNavController()

    TableSelection(navController)
}