package com.watb.htem.menu

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.watb.htem.CartItem
import com.watb.htem.CommonSpaceColumn
import com.watb.htem.Food
import com.watb.htem.ORDER_ID
import com.watb.htem.R
import com.watb.htem.Set
import com.watb.htem.api.ApiClient
import com.watb.htem.dataStore
import com.watb.htem.helper.Helper
import com.watb.htem.ui.theme.HTEMTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun FoodMenuScreen(tableCode: String, set: Set) {
    var expandSet by remember { mutableStateOf(true) }
    var expandAppetizer by remember { mutableStateOf(true) }
    var expandMain by remember { mutableStateOf(true) }
    var expandDessert by remember { mutableStateOf(true) }
    var expandDrink by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val dataStore = context.dataStore
    val coroutineScope = rememberCoroutineScope()
    var clickCount by remember { mutableIntStateOf(0) }
    var lastClickTime by remember { mutableLongStateOf(0L) }
    var orderId by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }

    val itemCountSet by Helper.countDataFood(context, "notOrderYet").collectAsState(initial = 0)
    val itemCountDrink by Helper.countDataDrink(context, "notOrderYet").collectAsState(initial = 0)

    val itemList: MutableList<CartItem> = mutableListOf()

    set.details.forEach {
        val name = stringResource(it.name)
        val type = stringResource(it.type)
        val price = stringResource(it.shortDescription)
        if (type == "Drink") {
            val intPrice = price.replace("đ", "").replace(".","").toInt()
            itemList.add(CartItem(name, 0, intPrice, "ordered", type))
        } else {
            itemList.add(CartItem(name, 0, 0, "ordered", type))
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val preferences = context.dataStore.data.first()
            orderId = preferences[ORDER_ID] ?: 0
            Helper.addItemMenuToDataStore(dataStore, itemList)
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp)
    ) {
        val (bannerRef, menuRef) = createRefs()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 12.dp)
                .constrainAs(bannerRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "Mã bàn: $tableCode",
                        modifier = Modifier
                            .height(20.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = set.name),
                        modifier = Modifier
                            .height(28.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(end = 4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp, 24.dp)
                            .padding(0.dp),
                        contentScale = ContentScale.Fit
                    )
                    Row {
                        Button(
                            onClick = {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastClickTime < 1000) {
                                    clickCount++
                                    if (clickCount == 3) {
                                        coroutineScope.launch {
                                            isLoading = true
                                            val response = ApiClient.dishesOrder(orderId,  Helper.getDishes(context))
                                            Log.d("Add Dishes", "Add Dishes response: $response")
                                            if (ApiClient.getStatusCode() != 200) {
                                                Toast.makeText(context, "Không gửi được đơn hàng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
                                                isLoading = false
                                                return@launch
                                            }
                                            Helper.orderItems(dataStore = dataStore)
                                            Toast.makeText(context, "Gọi món thành công!", Toast.LENGTH_LONG).show()
                                            clickCount = 0
                                            isLoading = false
                                        }
                                    }
                                } else {
                                    clickCount = 1
                                }
                                lastClickTime = currentTime

                                if (clickCount < 3) {
                                    coroutineScope.launch {
                                        delay(1000)
                                        if (clickCount < 3) {
                                            Toast.makeText(context, "Nhấn đủ 3 lần để gọi món!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(36.dp)
                                .padding(top = 2.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp
                            )
                        ) {
                            Row {
                                Text(
                                    text = "Gọi ${itemCountSet + itemCountDrink} món",
                                    color = colorResource(id = R.color.white),
                                    modifier = Modifier
                                        .wrapContentWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 42.dp)
                .verticalScroll(state = rememberScrollState())
                .constrainAs(menuRef) {
                    top.linkTo(bannerRef.bottom)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Buffet"
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { expandSet = !expandSet }
                        ) {
                            Icon(
                                if (expandSet) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null
                            )
                        }
                    }
                    HorizontalDivider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .align(Alignment.CenterHorizontally)
                    )
                    if (expandSet) {
                        // Appetizer
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 16.dp, end = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Khai vị"
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { expandAppetizer = !expandAppetizer }
                                ) {
                                    Icon(
                                        if (expandAppetizer) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = null
                                    )
                                }
                            }
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .align(Alignment.CenterHorizontally),
                                thickness = 1.dp,
                                color = Color.Gray
                            )
                            CommonSpaceColumn()
                            if (expandAppetizer) {
                                set.details.forEach { food ->
                                    if (stringResource(id = food.type) == "Appetizer") {
                                        FoodItem(food = food)
                                        CommonSpaceColumn()
                                    }
                                }
                            }
                        }

                        // Main Course
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 16.dp, end = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Món chính"
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { expandMain = !expandMain }
                                ) {
                                    Icon(
                                        if (expandMain) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = null
                                    )
                                }
                            }
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .align(Alignment.CenterHorizontally),
                                thickness = 1.dp,
                                color = Color.Gray
                            )
                            CommonSpaceColumn()
                            if (expandMain) {
                                set.details.forEach { food ->
                                    if (stringResource(id = food.type) == "Main") {
                                        FoodItem(food = food)
                                        CommonSpaceColumn()
                                    }
                                }
                            }
                        }

                        // Dessert
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 16.dp, end = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tráng miệng"
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { expandDessert = !expandDessert }
                                ) {
                                    Icon(
                                        if (expandDessert) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = null
                                    )
                                }
                            }
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .align(Alignment.CenterHorizontally),
                                thickness = 1.dp,
                                color = Color.Gray
                            )
                            CommonSpaceColumn()
                            if (expandDessert) {
                                set.details.forEach { food ->
                                    if (stringResource(id = food.type) == "Dessert") {
                                        FoodItem(food = food)
                                        CommonSpaceColumn()
                                    }
                                }
                            }
                        }
                    }

                    // Drink Menu
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Đồ uống (Gọi thêm)"
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { expandDrink = !expandDrink }
                        ) {
                            Icon(
                                if (expandDrink) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .align(Alignment.CenterHorizontally),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                    CommonSpaceColumn()
                    Column(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    ) {
                        if (expandDrink) {
                            set.details.forEach { food ->
                                if (stringResource(id = food.type) == "Drink") {
                                    FoodItem(food = food)
                                    CommonSpaceColumn()
                                }
                            }
                        }
                    }
                }
            }
            CommonSpaceColumn()
            CommonSpaceColumn()
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

@Composable
fun FoodItem(
    food: Food,
    modifier: Modifier = Modifier
) {
    var expand by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dataStore = context.dataStore
    val coroutineScope = rememberCoroutineScope()
    val name = stringResource(id = food.name)
    val type = stringResource(id = food.type)
    val drinkPrice = stringResource(id = food.shortDescription)

    val itemCountFood by Helper.countDataFood(context, stringResource(food.name), "notOrderYet").collectAsState(initial = 0)
    val itemCountDrink by Helper.countDataDrink(context, stringResource(food.name), "notOrderYet").collectAsState(initial = 0)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .animateContentSize()
                .padding(bottom = 8.dp)
        ) {
            Image(
                painter = painterResource(id = food.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = food.name),
                    modifier = Modifier.padding(start = 8.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
//                Box(
//                    modifier = Modifier
//                        .clickable(onClick = {
//                            if (type == "Drink") {
//                                val price = drinkPrice.replace("đ", "").replace(".","").toInt()
//                                val item = CartItem(name = name, quantity = 1, price = price, status = "notOrderYet", type = type)
//                                coroutineScope.launch {
//                                    addToCart(dataStore = dataStore, newItem = item)
//                                    printDataStore(dataStore)
//                                }
//                            } else {
//                                val item = CartItem(name = name, quantity = 1, price = 0, status = "notOrderYet", type = type)
//                                coroutineScope.launch {
//                                    addToCart(dataStore = dataStore, newItem = item)
//                                    printDataStore(dataStore)
//                                }
//                            }
//                        })
//                        .padding(0.dp)
//                ) {
//                    Text(
//                        text = "Add to Cart",
//                        color = Color.Red,
//                        modifier = Modifier.padding(end = 8.dp),
//                        style = TextStyle(
//                            fontWeight = FontWeight.SemiBold,
//                            fontSize = 18.sp
//                        )
//                    )
//                }

                if (stringResource(food.type) != "Drink") {
                    IconButton(
                        onClick = {
                            if (itemCountFood > 0) {
                                val item = CartItem(name, 1, 0, "notOrderYet", type)
                                coroutineScope.launch {
                                    Helper.decreaseNumber(dataStore, item)
                                    Helper.printDataStore(dataStore)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 0.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.minus),
                            contentDescription = "Minus",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = itemCountFood.toString(),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                    IconButton(
                        onClick = {
                            if (itemCountFood >= 10) {
                                Toast.makeText(context, "Không chọn 1 món quá 10 lần", Toast.LENGTH_SHORT).show()
                            } else {
                                val item = CartItem(name, 1, 0, "notOrderYet", type)
                                coroutineScope.launch {
                                    Helper.addToCart(dataStore, item)
                                    Helper.printDataStore(dataStore)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(24.dp)
//                            .padding(end = 6.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "Plus",
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 0.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            if (itemCountDrink > 0) {
                                val price = drinkPrice.replace("đ", "").replace(".","").toInt()
                                val item = CartItem(name, 1, price, "notOrderYet", type)
                                coroutineScope.launch {
                                    Helper.decreaseNumber(dataStore, item)
                                    Helper.printDataStore(dataStore)
                                }
                            }
//                            else quantity = 0
                        },
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 0.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.minus),
                            contentDescription = "Minus",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = itemCountDrink.toString(),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                    IconButton(
                        onClick = {
                            if (itemCountDrink >= 10) {
                                Toast.makeText(context, "Không chọn 1 món quá 10 lần", Toast.LENGTH_SHORT).show()
                            } else {
                                val price = drinkPrice.replace("đ", "").replace(".","").toInt()
                                val item = CartItem(name, 1, price, "notOrderYet", type)
                                coroutineScope.launch {
                                    Helper.addToCart(dataStore, item)
                                    Helper.printDataStore(dataStore)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(24.dp)
//                            .padding(end = 6.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "Plus",
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 0.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = food.shortDescription),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(start = 8.dp, top = 4.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clickable(onClick = { expand = !expand })
                        .padding(0.dp)
                ) {
                    Text(
                        text = if (expand) "Ẩn bớt" else "Hiện thêm",
                        color = Color.Blue,
                        modifier = Modifier.padding(end = 8.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            if (expand) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .align(Alignment.CenterHorizontally),
                    thickness = 1.dp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(id = food.description),
                    modifier = Modifier.padding(start = 6.dp, end = 6.dp, bottom = 6.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    HTEMTheme {
//        val food = Food(R.drawable.chawanmushi, R.string.chawanmushi_name, R.string.chawanmushi_shortDescription, R.string.chawanmushi_description, R.string.chawanmushi_type)
//        FoodItem(food)

        val foodListSashimiHasu = listOf(
            // Appetizer
            Food(R.drawable.chawanmushi, R.string.chawanmushi_name, R.string.chawanmushi_shortDescription, R.string.chawanmushi_description, R.string.chawanmushi_type),
            Food(R.drawable.miso_shiru, R.string.miso_shiru_name, R.string.miso_shiru_shortDescription, R.string.miso_shiru_description, R.string.miso_shiru_type),
            Food(R.drawable.shio_ginnan, R.string.shio_ginnan_name, R.string.shio_ginnan_shortDescription, R.string.shio_ginnan_description, R.string.shio_ginnan_type),
            Food(R.drawable.miso_poteto, R.string.miso_poteto_name, R.string.miso_poteto_shortDescription, R.string.miso_poteto_description, R.string.miso_poteto_type),
            Food(R.drawable.bata_imo, R.string.bata_imo_name, R.string.bata_imo_shortDescription, R.string.bata_imo_description, R.string.bata_imo_type),
            Food(R.drawable.kinu_tofu, R.string.kinu_tofu_name, R.string.kinu_tofu_shortDescription, R.string.kinu_tofu_description, R.string.kinu_tofu_type),
            Food(R.drawable.kani_yuzu_sarada, R.string.kani_yuzu_sarada_name, R.string.kani_yuzu_sarada_shortDescription, R.string.kani_yuzu_sarada_description, R.string.kani_yuzu_sarada_type),
            Food(R.drawable.kimchi_baechu, R.string.kimchi_baechu_name, R.string.kimchi_baechu_shortDescription, R.string.kimchi_baechu_description, R.string.kimchi_baechu_type),
            Food(R.drawable.maguro_sarada, R.string.maguro_sarada_name, R.string.maguro_sarada_shortDescription, R.string.maguro_sarada_description, R.string.maguro_sarada_type),
            Food(R.drawable.yuzu_tofu_sarada, R.string.yuzu_tofu_sarada_name, R.string.yuzu_tofu_sarada_shortDescription, R.string.yuzu_tofu_sarada_description, R.string.yuzu_tofu_sarada_type),
            // Main Course
            Food(R.drawable.hikari_sake, R.string.hikari_sake_name, R.string.hikari_sake_shortDescription, R.string.hikari_sake_description, R.string.hikari_sake_type),
            Food(R.drawable.tako_mizu, R.string.tako_mizu_name, R.string.tako_mizu_shortDescription, R.string.tako_mizu_description, R.string.tako_mizu_type),
            Food(R.drawable.ika_kumo, R.string.ika_kumo_name, R.string.ika_kumo_shortDescription, R.string.ika_kumo_description, R.string.ika_kumo_type),
            Food(R.drawable.shime_saba, R.string.shime_saba_name, R.string.shime_saba_shortDescription, R.string.shime_saba_description, R.string.shime_saba_type),
            Food(R.drawable.maguro_hana, R.string.maguro_hana_name, R.string.maguro_hana_shortDescription, R.string.maguro_hana_description, R.string.maguro_hana_type),
            Food(R.drawable.suzuki_no_hikari, R.string.suzuki_no_hikari_name, R.string.suzuki_no_hikari_shortDescription, R.string.suzuki_no_hikari_description, R.string.suzuki_no_hikari_type),
            Food(R.drawable.hasu_no_mori, R.string.hasu_no_mori_name, R.string.hasu_no_mori_shortDescription, R.string.hasu_no_mori_description, R.string.hasu_no_mori_type),
            Food(R.drawable.sake_maki, R.string.sake_maki_name, R.string.sake_maki_shortDescription, R.string.sake_maki_description, R.string.sake_maki_type),
            Food(R.drawable.sake_kinoko_zosui, R.string.sake_kinoko_zosui_name, R.string.sake_kinoko_zosui_shortDescription, R.string.sake_kinoko_zosui_description, R.string.sake_kinoko_zosui_type),
            Food(R.drawable.sake_kabuto_teriyaki, R.string.sake_kabuto_teriyaki_name, R.string.sake_kabuto_teriyaki_shortDescription, R.string.sake_kabuto_teriyaki_description, R.string.sake_kabuto_teriyaki_type),

            // Dessert

            // Drink
            Food(R.drawable.matcha, R.string.matcha_name, R.string.matcha_price, R.string.matcha_description, R.string.matcha_type),
            Food(R.drawable.sake, R.string.sake_name, R.string.sake_price, R.string.sake_description, R.string.sake_type),
            Food(R.drawable.umeshu, R.string.umeshu_name, R.string.umeshu_price, R.string.umeshu_description, R.string.umeshu_type),
        )

        val set = Set(R.drawable.buffet_sashimi_hasu, R.string.sashimi_hasu_name, R.string.sashimi_hasu_price, foodListSashimiHasu)

        FoodMenuScreen("1", set)
    }
}
