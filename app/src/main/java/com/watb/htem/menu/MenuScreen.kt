package com.watb.htem.menu

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.watb.htem.Food
import com.watb.htem.Set
import com.watb.htem.R
import com.watb.htem.USER_ID
import com.watb.htem.account.ProfileScreen
import com.watb.htem.api.ApiClient
import com.watb.htem.cart.ShoppingCartBottomSheetScreen
import com.watb.htem.helper.Helper
import com.watb.htem.userDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(navController: NavHostController, tableCode: String) {
    val context = LocalContext.current
    var showDialogCart by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val userId = remember { mutableIntStateOf(0) }
    val isLoggedIn = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoggedIn.value = Helper.isLoggedIn(context)
            val preferences = context.userDataStore.data.first()
            userId.intValue = preferences[USER_ID] ?: 0
        }
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val barRef = createRef()

        MenuNavigation(tableCode, context)

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,

                ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 16.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .constrainAs(barRef) {
                    bottom.linkTo(parent.bottom)
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(
                    onClick = {
                        showDialogCart = true
                    }
                ) {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.food_management),
                            contentDescription = "Food Manager",
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(75.dp)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        navController.navigate("payment/$tableCode")
//                        navController.navigate("payment")
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.payment),
                        contentDescription = "Payment",
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(75.dp)
                    )
                }

                IconButton(
                    onClick = {
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
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(75.dp)
                    )
                }
            }

//            ShoppingCartScreen(showDialog = showDialogCart, onDismiss = { showDialogCart = false })
            ShoppingCartBottomSheetScreen(showDialog = showDialogCart, onDismiss = { showDialogCart = false })
            ProfileScreen(navController, showProfile, onDismiss = { showProfile = false })
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
fun MenuNavigation(tableCode: String, context: Context) {
    val navController = rememberNavController()
//    val coroutineScope = rememberCoroutineScope()

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

    NavHost(
        navController = navController,
        graph = navGraphMenu(navController, setList, tableCode, context)
    )
}

fun navGraphMenu(navController: NavHostController, setList: List<Set>, tableCode: String, context: Context): NavGraph {
    val navDestination: String
    if (Helper.isSetChoose(context)) {
        val setName = Helper.getSetName(context)
        var set: Set = setList[0]
        when(setName) {
            "Buffet Sashimi Hasu" -> { set = setList[0] }
            "Buffet Sashimi Kiku" -> { set = setList[1] }
            "Buffet Sashimi Sakura" -> { set = setList[2] }
            "Buffet Sashimi Shinjuku" -> { set = setList[3] }
            "Buffet lẩu" -> { set = setList[4] }
            "Buffet lẩu hải sản" -> { set = setList[5] }
            "Buffet nướng BBQ" -> { set = setList[6] }
        }
        navDestination = "foodMenu/${set.name}"
    } else {
        navDestination = "menu"
    }
    return navController.createGraph(navDestination) {
        composable("menu") {
            SetMenuScreen(navController, tableCode, setList)
        }
        composable("foodMenu/{setName}") { backStackEntry ->
            val setName = backStackEntry.arguments?.getString("setName") ?: ""
            var set: Set = setList[0]
            when(setName) {
                "Buffet Sashimi Hasu" -> { set = setList[0] }
                "Buffet Sashimi Kiku" -> { set = setList[1] }
                "Buffet Sashimi Sakura" -> { set = setList[2] }
                "Buffet Sashimi Shinjuku" -> { set = setList[3] }
                "Buffet lẩu" -> { set = setList[4] }
                "Buffet lẩu hải sản" -> { set = setList[5] }
                "Buffet nướng BBQ" -> { set = setList[6] }
            }
            FoodMenuScreen(tableCode, set)
        }
    }
}

@Preview
@Composable
fun PreviewMenu() {
    MenuScreen(rememberNavController(), "1")
}
