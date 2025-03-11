package com.watb.htem.menu

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.watb.htem.CommonSpaceColumn
import com.watb.htem.Food
import com.watb.htem.Set
import com.watb.htem.R

@Composable
fun SetMenuScreen(navController: NavHostController, tableCode: String, setList: List<Set>) {
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
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                verticalAlignment = Alignment.Top
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
                        text = "Lựa chọn thực đơn",
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(end = 4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(72.dp, 30.dp).padding(0.dp),
                        contentScale = ContentScale.Fit
                    )
//                    Button(
//                        onClick = {
//                            val currentTime = System.currentTimeMillis()
//                            if (currentTime - lastClickTime < 1000) {
//                                clickCount++
//                                if (clickCount == 3) {
//                                    CoroutineScope(Dispatchers.IO).launch {
//                                        orderItems(dataStore = dataStore)
//                                    }
//                                    Toast.makeText(context, "Gọi món thành công!", Toast.LENGTH_LONG).show()
//                                    clickCount = 0
//                                    onDismiss()
//                                }
//                            } else {
//                                clickCount = 1
//                            }
//                            lastClickTime = currentTime
//
//                            if (clickCount < 3) {
//                                coroutineScope.launch {
//                                    delay(1000)
//                                    if (clickCount < 3) {
//                                        Toast.makeText(context, "Nhấn đủ 3 lần để gọi món!", Toast.LENGTH_LONG).show()
//                                    }
//                                }
//                            }
//                        },
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.Red
//                        ),
//                        modifier = Modifier
//                            .wrapContentWidth()
//                            .height(36.dp)
//                            .padding(0.dp),
//                    ) {
//                        Text(
//                            text = "Order",
//                            color = colorResource(id = R.color.white)
//                        )
//                    }
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 50.dp).verticalScroll(state = rememberScrollState()).constrainAs(menuRef) {
                top.linkTo(bannerRef.bottom)
                bottom.linkTo(parent.bottom)
            }
        ) {
            setList.forEach { set ->
                SetItem(navController = navController, set = set, tableCode = tableCode)
                CommonSpaceColumn()
            }
            CommonSpaceColumn()
            CommonSpaceColumn()
        }
    }
}

@Composable
fun SetItem(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    set: Set,
    tableCode: String
) {
    var expand by remember { mutableStateOf(false) }
    var showDialogSelect by remember { mutableStateOf(false) }
    var setChosen = ""
    val price = stringResource(id = set.price)

    Card(
        modifier = modifier.padding(start = 16.dp, end = 16.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .animateContentSize()
                .padding(bottom = 8.dp)
        ) {
            val setName = stringResource(id = set.name)

            Image(
                painter = painterResource(id = set.image),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = set.name),
                    modifier = Modifier.padding(start = 8.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clickable(onClick = {})
                        .padding(0.dp)
                ) {
                    Text(
                        text = "Lựa chọn",
                        color = Color.Red,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                setChosen = setName
                                showDialogSelect = true
                            },
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = set.price),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
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
                        .clickable(onClick = { expand = !expand})
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
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(10.dp))
                set.details.forEach { detail ->
                    SetDetailItem(detail = detail)
                    CommonSpaceColumn()
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
        EnterNumberScreen(
            navController = navController,
            showDialog = showDialogSelect,
            tableCode = tableCode,
            setName = setChosen,
            price = price,
            onDismiss = { showDialogSelect = false }
        )
    }
}

@Composable
fun SetDetailItem(detail: Food) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = detail.name),
            style = TextStyle(
                color = Color.DarkGray,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(id = detail.shortDescription),
            style = TextStyle(
                color = Color.Gray,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            modifier = Modifier.fillMaxWidth(0.4f),
            textAlign = TextAlign.End,
            maxLines = 2
        )
    }
}

@Preview
@Composable
fun PreviewSetMenu() {
    val foodListHotpot = listOf(
        Food(R.drawable.chawanmushi, R.string.chawanmushi_name, R.string.chawanmushi_shortDescription, R.string.chawanmushi_description, R.string.chawanmushi_type),
        Food(R.drawable.tempura, R.string.tempura_name, R.string.tempura_shortDescription, R.string.tempura_description, R.string.tempura_type),
        Food(R.drawable.nishin, R.string.nishin_name, R.string.nishin_shortDescription, R.string.nishin_description, R.string.nishin_type),
        Food(R.drawable.nishin, R.string.nishin_name, R.string.nishin_shortDescription, R.string.nishin_description, R.string.nishin_type),
        Food(R.drawable.nishin, R.string.nishin_name, R.string.nishin_shortDescription, R.string.nishin_description, R.string.nishin_type)
    )

    val foodListHotpotSeaFood = listOf(
        Food(R.drawable.chawanmushi, R.string.chawanmushi_name, R.string.chawanmushi_shortDescription, R.string.chawanmushi_description, R.string.chawanmushi_type)
    )

    val foodListBBQ = listOf(
        Food(R.drawable.chawanmushi, R.string.chawanmushi_name, R.string.chawanmushi_shortDescription, R.string.chawanmushi_description, R.string.chawanmushi_type)
    )

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

    val foodListSashimiKiKu = listOf(
        Food(R.drawable.chawanmushi, R.string.chawanmushi_name, R.string.chawanmushi_shortDescription, R.string.chawanmushi_description, R.string.chawanmushi_type)
    )

    val foodListSashimiSakura = listOf(
        Food(R.drawable.chawanmushi, R.string.chawanmushi_name, R.string.chawanmushi_shortDescription, R.string.chawanmushi_description, R.string.chawanmushi_type)
    )

    val foodListSashimiShinjuku = listOf(
        Food(R.drawable.hikari_sake, R.string.hikari_sake_name, R.string.hikari_sake_shortDescription, R.string.hikari_sake_description, R.string.hikari_sake_type)
    )

    val setList = listOf(
        Set(R.drawable.buffet_sashimi_hasu, R.string.sashimi_hasu_name, R.string.sashimi_hasu_price, foodListSashimiHasu),
        Set(R.drawable.buffet_sashimi_kiku, R.string.sashimi_kiku_name, R.string.sashimi_kiku_price, foodListSashimiKiKu),
        Set(R.drawable.buffet_sashimi_sakura, R.string.sashimi_sakura_name, R.string.sashimi_sakura_price, foodListSashimiSakura),
        Set(R.drawable.buffet_sashimi_shinjuku, R.string.sashimi_shinjuku_name, R.string.sashimi_shinjuku_price, foodListSashimiShinjuku),
        Set(R.drawable.buffet_lau, R.string.hotpot_name, R.string.hotpot_price, foodListHotpot),
        Set(R.drawable.buffet_lau_hai_san, R.string.hotpot_seafood_name, R.string.hotpot_seafood_price, foodListHotpotSeaFood),
        Set(R.drawable.buffet_nuong_bbq, R.string.bbq_name, R.string.bbq_price, foodListBBQ)
    )

    SetMenuScreen(rememberNavController(), "1", setList)
}