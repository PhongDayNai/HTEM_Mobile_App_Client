package com.watb.htem.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.watb.htem.R

@Composable
fun CertainlyPayment(navController: NavHostController, tableCode: String, totalPrice: Int, pointsUsedNumber: Int, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp
        ) {
            ConstraintLayout(
                modifier = Modifier
//                    .fillMaxWidth(0.7f)
//                    .fillMaxHeight(0.3f)
                    .size(400.dp, 145.dp)
                    .background(
                        color = colorResource(id = R.color.white),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                val horizontalGuideline = createGuidelineFromTop(0.5f)
                val verticalGuideline = createGuidelineFromStart(0.5f)
                val (quesRef, onlRef, offRef, closeRef) = createRefs()

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.constrainAs(closeRef) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
                Text(
                    text = "Quý khách muốn thanh toán?",
                    modifier = Modifier
                        .constrainAs(quesRef) {
                            top.linkTo(parent.top)
                            bottom.linkTo(horizontalGuideline)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                Button(
                    onClick = {
                        navController.navigate("checkout/$tableCode")
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .constrainAs(onlRef) {
                            top.linkTo(horizontalGuideline)
                            start.linkTo(parent.start)
                            end.linkTo(verticalGuideline)
                        }
                ) {
                    Text(
                        text = "Tại quầy\n(Tiền mặt)",
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = {
                        navController.navigate("qrCode/${totalPrice - pointsUsedNumber*1000}/$pointsUsedNumber")
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .constrainAs(offRef) {
                            top.linkTo(horizontalGuideline)
                            start.linkTo(verticalGuideline)
                            end.linkTo(parent.end)
                        }
                ) {
                    Text(
                        text = "Tại bàn\n(Mã QR)",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCertainlyPayment() {
//    var showDialog by remember { mutableStateOf(true) }
//    CertainlyPayment(rememberNavController(), "123", 0, 0, onDismiss = { showDialog = false })
}
