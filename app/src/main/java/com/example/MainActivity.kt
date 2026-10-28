package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.BatchCheckerScreen
import com.example.ui.screens.CartCheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SellWizardScreen
import com.example.ui.screens.SwapHubScreen
import com.example.ui.screens.WebStorefrontScreen
import com.example.ui.theme.DarkPlum
import com.example.ui.theme.GlowSwapTheme
import com.example.ui.theme.RoseWine
import com.example.ui.theme.SoftBlush
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MarketplaceViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: MarketplaceViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      GlowSwapTheme {
        GlowSwapApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun GlowSwapApp(viewModel: MarketplaceViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val snackbarMessage by viewModel.snackbarMessage.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()
  val cartCount = cartItems.sumOf { it.quantity }
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearSnackbar()
    }
  }

  // Show bottom navigation bar on primary browsing screens
  val showBottomNav = currentScreen is AppScreen.Home ||
      currentScreen is AppScreen.WebStorefront ||
      currentScreen is AppScreen.BatchChecker ||
      currentScreen is AppScreen.SwapHub ||
      currentScreen is AppScreen.CartCheckout

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    bottomBar = {
      if (showBottomNav) {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = DarkPlum,
          modifier = Modifier
            .navigationBarsPadding()
            .testTag("glowswap_bottom_navigation")
        ) {
          NavigationBarItem(
            selected = currentScreen is AppScreen.Home,
            onClick = { viewModel.navigateTo(AppScreen.Home) },
            icon = { Icon(Icons.Default.Storefront, contentDescription = "Marketplace") },
            label = { Text("App Store", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = RoseWine,
              selectedTextColor = RoseWine,
              indicatorColor = SoftBlush
            ),
            modifier = Modifier.testTag("nav_item_marketplace")
          )

          NavigationBarItem(
            selected = currentScreen is AppScreen.WebStorefront,
            onClick = { viewModel.navigateTo(AppScreen.WebStorefront) },
            icon = { Icon(Icons.Default.Language, contentDescription = "Website Storefront") },
            label = { Text("Web Store", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = RoseWine,
              selectedTextColor = RoseWine,
              indicatorColor = SoftBlush
            ),
            modifier = Modifier.testTag("nav_item_web_store")
          )

          NavigationBarItem(
            selected = currentScreen is AppScreen.BatchChecker,
            onClick = { viewModel.navigateTo(AppScreen.BatchChecker) },
            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Batch Checker") },
            label = { Text("Verify Lab", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = RoseWine,
              selectedTextColor = RoseWine,
              indicatorColor = SoftBlush
            ),
            modifier = Modifier.testTag("nav_item_batch_checker")
          )

          NavigationBarItem(
            selected = currentScreen is AppScreen.SwapHub,
            onClick = { viewModel.navigateTo(AppScreen.SwapHub) },
            icon = { Icon(Icons.Default.SyncAlt, contentDescription = "Swap Lounge") },
            label = { Text("Swaps", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = RoseWine,
              selectedTextColor = RoseWine,
              indicatorColor = SoftBlush
            ),
            modifier = Modifier.testTag("nav_item_swaps")
          )

          NavigationBarItem(
            selected = currentScreen is AppScreen.CartCheckout,
            onClick = { viewModel.navigateTo(AppScreen.CartCheckout) },
            icon = {
              BadgedBox(
                badge = {
                  if (cartCount > 0) {
                    Badge(containerColor = RoseWine, contentColor = Color.White) {
                      Text(cartCount.toString())
                    }
                  }
                }
              ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = "Bag")
              }
            },
            label = { Text("Bag", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = RoseWine,
              selectedTextColor = RoseWine,
              indicatorColor = SoftBlush
            ),
            modifier = Modifier.testTag("nav_item_cart")
          )
        }
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "ScreenTransition"
      ) { screen ->
        when (screen) {
          is AppScreen.Home -> HomeScreen(viewModel = viewModel)
          is AppScreen.WebStorefront -> WebStorefrontScreen(viewModel = viewModel)
          is AppScreen.ProductDetail -> ProductDetailScreen(product = screen.product, viewModel = viewModel)
          is AppScreen.BatchChecker -> BatchCheckerScreen(viewModel = viewModel)
          is AppScreen.SwapHub -> SwapHubScreen(viewModel = viewModel)
          is AppScreen.SellWizard -> SellWizardScreen(viewModel = viewModel)
          is AppScreen.CartCheckout -> CartCheckoutScreen(viewModel = viewModel)
        }
      }
    }
  }
}
