package com.watb.htem.cart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.watb.htem.R
import com.watb.htem.helper.Helper
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.watb.htem.data.CartItem
import com.watb.htem.main.CommonSpaceColumn
import com.watb.htem.main.dataStore
import com.watb.htem.ui.theme.HTEMTheme

@Composable
fun ShoppingCartBottomSheetScreen(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        val context = LocalContext.current
        val dataStore = context.dataStore
        val coroutineScope = rememberCoroutineScope()
        val cartItemsList = remember { mutableStateOf(emptyList<CartItem>()) }

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                cartItemsList.value = Helper.getCartItems(dataStore)
            }
        }

        ShoppingCartBottomSheet(cartItems = cartItemsList, onDismiss = onDismiss)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCartBottomSheet(cartItems: MutableState<List<CartItem>>, onDismiss: () -> Unit) {
    val context = LocalContext.current

    // Lấy số lượng món chưa đặt hàng từ DataStore
    val itemCountOrderedFood by Helper.countDataFood(context, "ordered").collectAsState(initial = 0)
    val itemCountOrderedDrink by Helper.countDataDrink(context, "ordered").collectAsState(initial = 0)
    val itemCountServedFood by Helper.getAllServed(context, "notDrink").collectAsState(initial = 0)
    val itemCountServedDrink by Helper.getAllServed(context, "drink").collectAsState(initial = 0)

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp,
            color = colorResource(id = R.color.white)
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth(1f)
                    .background(color = colorResource(id = R.color.white))
            ) {
                val (titleRef, dividerRef, itemsRef, closeButtonRef) = createRefs()
                Text(
                    text = "Trạng thái món",
                    modifier = Modifier
                        .height(30.dp)
                        .constrainAs(titleRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        },
                    fontFamily = FontFamily(Font(resId = R.font.svn_shikamaru))
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .constrainAs(dividerRef) {
                            top.linkTo(titleRef.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp, bottom = 50.dp)
                        .verticalScroll(state = rememberScrollState())
                        .constrainAs(itemsRef) {
                            top.linkTo(dividerRef.bottom)
                            bottom.linkTo(closeButtonRef.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        ConstraintLayout {
                            var expendSet by remember { mutableStateOf(true) }
                            var expendDrink by remember { mutableStateOf(true) }
                            val (setRef, drinkRef, detailSetRef, detailDrinkRef) = createRefs()
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(start = 16.dp, end = 8.dp)
                                    .border(1.dp, Color.Black, shape = RoundedCornerShape(16.dp))
                                    .constrainAs(detailSetRef) {
                                        top.linkTo(setRef.top)
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = colorResource(id = R.color.white)
                                ),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .padding(4.dp)
                                ) {
                                    Text(" ")
                                    Text(" ")
                                }
                                if (expendSet) {
                                    Row(
                                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                                    ) {
                                        Text(
                                            text = "Khai vị",
                                            fontFamily = FontFamily(Font(R.font.italianno_regular)),
                                            fontSize = 20.sp
                                        )
                                    }
                                    cartItems.value.forEach { item ->
                                        if (item.type == "Appetizer") {
                                            CartItemDetail(cartItem = item)
                                        }
                                    }
                                    CommonSpaceColumn()
                                    Row(
                                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                                    ) {
                                        Text(
                                            text = "Món chính",
                                            fontFamily = FontFamily(Font(R.font.italianno_regular)),
                                            fontSize = 20.sp
                                        )
                                    }
                                    cartItems.value.forEach { item ->
                                        if (item.type == "Main" && item.status == "ordered") {
                                            CartItemDetail(cartItem = item)
                                        }
                                    }
                                    CommonSpaceColumn()
                                    Row(
                                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                                    ) {
                                        Text(
                                            text = "Tráng miệng",
                                            fontFamily = FontFamily(Font(R.font.italianno_regular)),
                                            fontSize = 20.sp
                                        )
                                    }
                                    cartItems.value.forEach { item ->
                                        if (item.type == "Dessert" && item.status == "ordered") {
                                            CartItemDetail(cartItem = item)
                                        }
                                    }
                                    CommonSpaceColumn()
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .border(1.dp, Color.Black, shape = RoundedCornerShape(10.dp))
                                    .constrainAs(setRef) {
                                        top.linkTo(parent.top)
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = colorResource(id = R.color.white)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 8.dp
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(start = 8.dp, end = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    cartItems.value.forEach { item ->
                                        if (item.type == "set") {
                                            SetInCart(cartItem = item)
                                        }
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "Còn ${itemCountOrderedFood - itemCountServedFood}...",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White,
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .background(
                                                Color(0xFF27A97A),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .padding(horizontal = 6.dp)
                                    )
                                    IconButton(
                                        onClick = { expendSet = !expendSet }
                                    ) {
                                        Icon(
                                            if (expendSet) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(start = 16.dp, end = 8.dp)
                                    .border(1.dp, Color.Black, shape = RoundedCornerShape(16.dp))
                                    .constrainAs(detailDrinkRef) {
                                        top.linkTo(drinkRef.top)
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = colorResource(id = R.color.white)
                                ),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .padding(4.dp)
                                ) {
                                    Text(" ")
                                    Text(" ")
                                }
                                if (expendDrink) {
                                    cartItems.value.forEach { item ->
                                        if (item.type == "Drink") {
                                            CartItemDetail(cartItem = item)
                                        }
                                    }
                                    CommonSpaceColumn()
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .border(1.dp, Color.Black, shape = RoundedCornerShape(10.dp))
                                    .constrainAs(drinkRef) {
                                        top.linkTo(
                                            if (expendSet) detailSetRef.bottom else setRef.bottom,
                                            margin = 8.dp
                                        )
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = colorResource(id = R.color.white)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 8.dp
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(start = 8.dp, end = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.wrapContentWidth()
                                    ) {
                                        Text(
                                            text = "Đồ uống (Gọi thêm)",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Row {
                                            var totalDrink = 0
                                            cartItems.value.forEach {
                                                if (it.type == "Drink" && it.status == "ordered") {
                                                    totalDrink += it.price * it.quantity
                                                }
                                            }
                                            val stringTotalDrink = Helper.formatCurrency(totalDrink)
                                            Text(
                                                text = stringTotalDrink,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "Còn ${itemCountOrderedDrink - itemCountServedDrink}...",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White,
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .background(
                                                Color(0xFF27A97A),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .padding(horizontal = 6.dp)
                                    )

                                    IconButton(
                                        onClick = {
                                            expendDrink = !expendDrink
                                        }
                                    ) {
                                        Icon(
                                            if (expendDrink) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .height(50.dp)
                        .background(Color.Transparent, shape = RoundedCornerShape(2.dp))
                        .constrainAs(closeButtonRef) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                ) {
                    Text(
                        text = "Close",
                        color = Color.Blue
                    )
                }
            }
        }
    }
}

@Composable
fun SetInCart(
    cartItem: CartItem
) {
    val stringPrice = Helper.formatCurrency(cartItem.price)
    Column(
        modifier = Modifier.wrapContentWidth()
    ) {
        Text(
            text = cartItem.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row {
            Text(
                text = stringPrice,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "x${cartItem.quantity}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun CartItemDetail(
    cartItem: CartItem
) {
    val context = LocalContext.current
    val itemCountOrderedFood by Helper.countDataFood(context, cartItem.name, itemStatus = "ordered", quantityType = "ordered").collectAsState(initial = 0)
//    val itemCountOrderedDrink by Helper.countDataDrink(context, cartItem.name, "ordered").collectAsState(initial = 0)
    val itemCountServedFood by Helper.countDataFood(context, cartItem.name, itemStatus = "ordered", quantityType = "served").collectAsState(initial = 0)
//    val itemCountServedDrink by Helper.countDataDrink(context, cartItem.name, "served").collectAsState(initial = 0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(32.dp)
        ) {
            if (itemCountOrderedFood != 0) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(color = if (itemCountOrderedFood != itemCountServedFood) Color.Red else Color.Green, shape = CircleShape)
                )
            }
            Text(
                text = cartItem.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 10.dp)
            )
            if (cartItem.price != 0) {
                val stringPrice = Helper.formatCurrency(cartItem.price)
                Text(
                    text = "($stringPrice)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
//            if (cartItem.type != "Drink") {
//                Text(
//                    text = "$itemCountServedFood/$itemCountOrderedFood",
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Normal,
//                    modifier = Modifier.padding(end = 8.dp)
//                )
//            } else {
//                Text(
//                    text = "$itemCountServedDrink/$itemCountOrderedDrink",
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Normal,
//                    modifier = Modifier.padding(end = 8.dp)
//                )
//            }

            Text(
                text = "$itemCountServedFood/$itemCountOrderedFood",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 1.dp)
        )
    }
}

@Preview
@Composable
fun PreviewCartItem() {
    HTEMTheme {
        CartItemDetail(CartItem("Ten mon", 4, 100000, "ordered", "Appetizer"))
    }
}
