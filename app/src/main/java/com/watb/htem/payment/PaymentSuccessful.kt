package com.watb.htem.payment

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.streams.toList

@Composable
fun PaymentSuccessful(navController: NavController) {
    val isVisible = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible.value) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible.value) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )

    var isVisibleText by remember { mutableStateOf(false) }
    val scaleText by animateFloatAsState(
        targetValue = if (isVisibleText) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )
    val alphaText by animateFloatAsState(
        targetValue = if (isVisibleText) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )


    LaunchedEffect(Unit) {
        isVisible.value = true
        launch {
            delay(7500)
            isVisibleText = true
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                enabled = isVisibleText,
                onClick = {
                    navController.navigate("home") {
                        popUpTo("paymentSuccessful") {
                            inclusive = true
                        }
                    }
                }
            )
    ) {
        val (contentRef, textRef) = createRefs()
        val verticalGuidelineTop = createGuidelineFromTop(0.4f)
        val verticalGuidelineBottom = createGuidelineFromTop(0.8f)

        Icon(
            painter = painterResource(R.drawable.payment_successful),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(0.6f)
                .constrainAs(contentRef) {
                    top.linkTo(verticalGuidelineTop)
                    bottom.linkTo(verticalGuidelineTop)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .scale(scale)
                .alpha(alpha)
        )
        TypewriterTextErrorMessage(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .constrainAs(createRef()) {
                    top.linkTo(contentRef.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = "Chạm để trở về màn hình chính.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight(500),
            fontFamily = FontFamily(Font(resId = R.font.comic_sans_ms)),
            modifier = Modifier
                .constrainAs(textRef) {
                    top.linkTo(verticalGuidelineBottom)
                    bottom.linkTo(verticalGuidelineBottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .scale(scaleText)
                .alpha(alphaText)
        )
    }
}

@Composable
fun TypewriterTextErrorMessage(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        TypewriterText(
            texts = listOf(
                "Thanh toán thành công!",
                "Cảm ơn bạn đã ghé thăm.",
                "Hẹn gặp lại lần sau."
            ),
        )
    }
}

@Composable
fun TypewriterText(
    texts: List<String>,
) {
    var textIndex by remember {
        mutableIntStateOf(0)
    }
    var textToDisplay by remember {
        mutableStateOf("")
    }
    val textCharsList: List<List<String>> = remember {
        texts.map {
            it.splitToCodePoints()
        }
    }

    LaunchedEffect(
        key1 = texts,
    ) {
        while (textIndex < textCharsList.size) {
            textCharsList[textIndex].forEachIndexed { charIndex, _ ->
                textToDisplay = textCharsList[textIndex]
                    .take(
                        n = charIndex + 1,
                    ).joinToString(
                        separator = "",
                    )
                delay(100)
            }
            textIndex = (textIndex + 1) % texts.size
            delay(750)
        }
    }

    Text(
        text = textToDisplay,
        fontSize = 48.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight(600),
        fontFamily = FontFamily(Font(resId = R.font.comic_sans_ms)),
        lineHeight = 54.sp,
    )
}

fun String.splitToCodePoints(): List<String> {
    return codePoints()
        .toList()
        .map {
            String(Character.toChars(it))
        }
}

@Preview
@Composable
fun PaymentSuccessfulPreview() {
    PaymentSuccessful(rememberNavController())
}