package com.watb.htem.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.watb.htem.R
import com.watb.htem.ui.theme.HTEMTheme

@Composable
fun CheckoutScreen(tableCode: String) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))
    ) {
        val (tableCodeRef, titleRef, imageRef, logoRef) = createRefs()
        val horizontalGuideline = createGuidelineFromTop(0.2f)

        Image(
            painter = painterResource(id = R.drawable.logo_res_sharp),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .constrainAs(logoRef) {
                    top.linkTo(parent.top, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }
        )

        Text(
            text = "Mời quý khách tới quầy để thanh toán",
            textAlign = TextAlign.Center,
            style = TextStyle(
                color = Color.Black,
                fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru)),
                fontSize = 30.sp
            ),
            modifier = Modifier
                .constrainAs(titleRef) {
                    top.linkTo(horizontalGuideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Row(
            modifier = Modifier.constrainAs(tableCodeRef) {
                top.linkTo(titleRef.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Text(
                text = "Mã bàn của quý khách: ",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(resId = R.font.gotham_rounded))
            )
            Text(
                text = tableCode,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(resId = R.font.gotham_rounded))
            )
        }

        Image(
            painter = painterResource(id = R.drawable.checkout),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(imageRef) {
                    bottom.linkTo(parent.bottom, margin = 75.dp)
                }
        )
    }
}

@Preview
@Composable
fun PreviewCheckout() {
    HTEMTheme {
        CheckoutScreen("123")
    }
}