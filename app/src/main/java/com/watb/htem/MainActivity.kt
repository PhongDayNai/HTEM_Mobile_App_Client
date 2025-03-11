package com.watb.htem

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.watb.htem.account.SignInScreen
import com.watb.htem.account.SignUpScreen
import com.watb.htem.helper.Helper
import com.watb.htem.menu.MenuScreen
import com.watb.htem.payment.CheckoutScreen
import com.watb.htem.payment.PaymentScreen
import com.watb.htem.payment.QRCodeScreen
import com.watb.htem.ui.theme.HTEMTheme
import com.watb.htem.welcome.QRCodeScannerScreen
import com.watb.htem.welcome.WelcomeScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cart")
val ORDER_ID = intPreferencesKey("order_id")
val ITEM_NAME = stringPreferencesKey("item_name")
val ITEM_QUANTITY = stringPreferencesKey("item_quantity")
val ITEM_PRICE = stringPreferencesKey("item_price")
val ITEM_STATUS = stringPreferencesKey("item_status")
val ITEM_SERVED = stringPreferencesKey("item_served")
val ITEM_TYPE = stringPreferencesKey("item_type")

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
val USER_ID = intPreferencesKey("user_id")
val FULL_NAME = stringPreferencesKey("full_name")
val USER_POINTS = intPreferencesKey("user_points")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            Helper.clearDataStore(dataStore = dataStore)
        }

        enableEdgeToEdge()
        setContent {
            HTEMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    )
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, graph = navGraph(navController))
}

private fun navGraph(navController: NavHostController): NavGraph {
    return navController.createGraph("home") {
        composable("home") {
            WelcomeScreen(navController)
        }
        composable("signIn") {
            SignInScreen(navController)
        }
        composable("signUp") {
            SignUpScreen(navController)
        }
        composable("qrScanner") {
            QRCodeScannerScreen(navController)
        }
        composable("payment/{tableCode}") { backStackEntry ->
            val tableCode = backStackEntry.arguments?.getString("tableCode") ?: ""
            PaymentScreen(navController, tableCode)
        }
        composable("checkout/{tableCode}") { backStackEntry ->
            val tableCode = backStackEntry.arguments?.getString("tableCode") ?: ""
            CheckoutScreen(tableCode)
        }
        composable("qrCode/{totalPrice}/{pointsUsedNumber}") { backStackEntry ->
            val totalPrice = backStackEntry.arguments?.getString("totalPrice") ?: ""
            val pointsUsedNumber = backStackEntry.arguments?.getString("pointsUsedNumber") ?: ""
            val intTotalPrice = totalPrice.toInt()
            val intPointsUsedNumber = pointsUsedNumber.toInt()
            QRCodeScreen(navController, intTotalPrice, intPointsUsedNumber)
        }
        composable("tableDetail/{tableCode}") { backStackEntry ->
            val tableCode = backStackEntry.arguments?.getString("tableCode") ?: ""
            MenuScreen(navController, tableCode)
        }
    }
}

@Composable
fun CommonSpaceColumn() {
    Spacer(modifier = Modifier.height(20.dp))
}
