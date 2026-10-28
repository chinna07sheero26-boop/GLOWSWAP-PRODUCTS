package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BeautyProduct
import com.example.data.model.ConditionRating
import com.example.data.model.ProductCategory
import com.example.ui.components.CategoryIcon
import com.example.ui.components.ConditionChip
import com.example.ui.components.SavingsTag
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkPlum
import com.example.ui.theme.DiscountRed
import com.example.ui.theme.RoseWine
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.SwapPurple
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun WebStorefrontScreen(
  viewModel: MarketplaceViewModel,
  modifier: Modifier = Modifier
) {
  BackHandler {
    viewModel.navigateBack()
  }

  val products by viewModel.filteredProducts.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()
  val totalCartCount = cartItems.sumOf { it.quantity }
  var newsletterEmail by remember { mutableStateOf("") }
  var isSubscribed by remember { mutableStateOf(false) }
  var activeWebTab by remember { mutableStateOf("ALL") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFFCF9F9))
  ) {
    // 1. Browser Simulation Chrome Bar
    BrowserChromeBar(
      url = "https://www.glowswap.beauty/marketplace",
      onReturnToApp = { viewModel.navigateBack() }
    )

    // 2. Web Announcement Ribbon
    WebAnnouncementRibbon()

    // 3. Web Navigation Header
    WebNavBar(
      cartCount = totalCartCount,
      onOpenCart = { viewModel.navigateTo(AppScreen.CartCheckout) },
      onOpenBatchChecker = { viewModel.navigateTo(AppScreen.BatchChecker) },
      onOpenSell = { viewModel.navigateTo(AppScreen.SellWizard) },
      onOpenSwap = { viewModel.navigateTo(AppScreen.SwapHub) }
    )

    // Web Page Content Scroll
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("web_storefront_scroll")
    ) {
      // 4. Web Hero Section
      item {
        WebHeroSection(
          onShopNow = { /* scroll or focus */ },
          onSellWithUs = { viewModel.navigateTo(AppScreen.SellWizard) }
        )
      }

      // 5. Trust Value Proposition Cards (Web 3-pillar banner)
      item {
        WebTrustPillars()
      }

      // 6. Web Catalog Section Header & Category Pills
      item {
        WebCatalogHeader(
          activeTab = activeWebTab,
          onSelectTab = { activeWebTab = it }
        )
      }

      // 7. Web Product Grid (List format for web scroll)
      val displayedProducts = when (activeWebTab) {
        "OVERSTOCK" -> products.filter { it.isOverstockSale }
        "SWAPS" -> products.filter { it.isOpenToSwap }
        "SKINCARE" -> products.filter { it.category == ProductCategory.SKINCARE }
        "MAKEUP" -> products.filter { it.category == ProductCategory.MAKEUP }
        else -> products
      }

      items(displayedProducts, key = { it.id }) { product ->
        WebProductRowCard(
          product = product,
          onClick = { viewModel.navigateTo(AppScreen.ProductDetail(product)) },
          onAddToCart = { viewModel.addToCart(product) }
        )
      }

      // 8. Press Quotes & Social Proof
      item {
        WebPressProofSection()
      }

      // 9. Newsletter Signup
      item {
        WebNewsletterSection(
          email = newsletterEmail,
          onEmailChange = { newsletterEmail = it },
          isSubscribed = isSubscribed,
          onSubscribe = {
            if (newsletterEmail.contains("@")) {
              isSubscribed = true
              viewModel.showSnackbar("Subscribed! Use promo code 'GLOW20' for 20% off.")
            }
          }
        )
      }

      // 10. Web Footer
      item {
        WebFooter(onReturnToApp = { viewModel.navigateBack() })
      }
    }
  }
}

@Composable
private fun BrowserChromeBar(
  url: String,
  onReturnToApp: () -> Unit
) {
  Surface(
    color = Color(0xFF261D29),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Return to mobile native app button
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = RoseWine,
        modifier = Modifier.clickable(onClick = onReturnToApp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Mobile View", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      // URL display capsule
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF382C3C),
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Lock, contentDescription = "SSL Secure", tint = VerifiedGreen, modifier = Modifier.size(11.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = url,
            color = Color(0xFFE2D9E2),
            fontSize = 11.sp,
            maxLines = 1
          )
        }
      }

      IconButton(
        onClick = { /* reload */ },
        modifier = Modifier.size(28.dp)
      ) {
        Icon(Icons.Default.Refresh, contentDescription = "Refresh Webpage", tint = Color.LightGray, modifier = Modifier.size(16.dp))
      }
    }
  }
}

@Composable
private fun WebAnnouncementRibbon() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(RoseWine)
      .padding(vertical = 5.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "✦ COMPLIMENTARY COURIER SHIPPING ON ORDERS OVER $45 • 20% OFF WITH 'GLOW20' ✦",
      color = Color(0xFFFFE4E6),
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp
    )
  }
}

@Composable
private fun WebNavBar(
  cartCount: Int,
  onOpenCart: () -> Unit,
  onOpenBatchChecker: () -> Unit,
  onOpenSell: () -> Unit,
  onOpenSwap: () -> Unit
) {
  Surface(
    color = Color.White,
    shadowElevation = 2.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Logo
        Column {
          Text(
            text = "GLOWSWAP",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            color = DarkPlum
          )
          Text(
            text = "THE BEAUTY RESALE DESTINATION",
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = ChampagneGold
          )
        }

        // Web Actions
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = SoftBlush,
            modifier = Modifier.clickable(onClick = onOpenSell)
          ) {
            Text(
              text = "Sell Online",
              color = RoseWine,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          IconButton(
            onClick = onOpenCart,
            modifier = Modifier.size(36.dp)
          ) {
            BadgedBox(
              badge = {
                if (cartCount > 0) {
                  Badge(containerColor = RoseWine, contentColor = Color.White) {
                    Text(cartCount.toString())
                  }
                }
              }
            ) {
              Icon(Icons.Default.ShoppingBag, contentDescription = "Web Bag", tint = DarkPlum, modifier = Modifier.size(20.dp))
            }
          }
        }
      }

      // Desktop nav links strip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFFAF5F6))
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        listOf(
          "All Products",
          "Overstock Deals",
          "Fragrance Vault",
          "Swap Lounge",
          "Batch Checker"
        ).forEach { link ->
          Text(
            text = link,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (link == "Batch Checker" || link == "Swap Lounge") RoseWine else DarkPlum,
            modifier = Modifier.clickable {
              when (link) {
                "Batch Checker" -> onOpenBatchChecker()
                "Swap Lounge" -> onOpenSwap()
                "Sell" -> onOpenSell()
              }
            }
          )
        }
      }
    }
  }
}

@Composable
private fun WebHeroSection(
  onShopNow: () -> Unit,
  onSellWithUs: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF241427),
            Color(0xFF43193B)
          )
        )
      )
      .padding(20.dp)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0x33FFFFFF)
      ) {
        Text(
          text = "✦ CERTIFIED LUXURY BEAUTY EXCHANGE ✦",
          color = Color(0xFFFFD180),
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Pre-Loved & Overstock Cosmetics, Authenticated.",
        color = Color.White,
        fontSize = 22.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        lineHeight = 28.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Save up to 70% on prestige skincare, fragrances, and viral makeup. Every bottle, palette, and flacon is verified by our GlowCheck™ lab before shipping.",
        color = Color(0xFFF3E5F5),
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
          onClick = onShopNow,
          colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = DarkPlum),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Explore Drops", fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onSellWithUs,
          colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f), contentColor = Color.White),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Sell Your Vanity", fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

@Composable
private fun WebTrustPillars() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    val pillars = listOf(
      Triple(Icons.Default.Shield, "Triple-Verified", "Batch checked against official manufacturer lots"),
      Triple(Icons.Default.CleaningServices, "UV-C Sanitized", "Medical-grade sterilization for pre-loved products"),
      Triple(Icons.Default.Eco, "Circular Eco-Luxe", "Diverting premium cosmetics from carbon landfills")
    )

    pillars.forEach { (icon, title, desc) ->
      Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier.padding(10.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(icon, contentDescription = null, tint = RoseWine, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.height(4.dp))
          Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkPlum, textAlign = TextAlign.Center)
          Spacer(modifier = Modifier.height(2.dp))
          Text(desc, fontSize = 9.sp, color = Color.Gray, textAlign = TextAlign.Center, lineHeight = 12.sp)
        }
      }
    }
  }
}

@Composable
private fun WebCatalogHeader(
  activeTab: String,
  onSelectTab: (String) -> Unit
) {
  Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
    Text(
      text = "Curated Web Storefront",
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = DarkPlum
    )
    Text(
      text = "Direct purchase and fair barter swap listings verified today",
      fontSize = 12.sp,
      color = Color.Gray
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf(
        Pair("ALL", "All Listings"),
        Pair("OVERSTOCK", "Warehouse Overstock"),
        Pair("SWAPS", "Swap Ready"),
        Pair("SKINCARE", "Skincare"),
        Pair("MAKEUP", "Makeup")
      ).forEach { (key, label) ->
        val selected = activeTab == key
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = if (selected) DarkPlum else Color.White,
          border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) DarkPlum else Color(0xFFE5DCDD)),
          modifier = Modifier.clickable { onSelectTab(key) }
        ) {
          Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else DarkPlum,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun WebProductRowCard(
  product: BeautyProduct,
  onClick: () -> Unit,
  onAddToCart: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clickable(onClick = onClick),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    shape = RoundedCornerShape(12.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Visual badge icon
      Box(
        modifier = Modifier
          .size(70.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(product.accentColorHex).copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        CategoryIcon(category = product.category, tint = Color(product.accentColorHex), size = 32)
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = product.brand.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 0.5.sp
          )
          VerifiedBadge()
        }

        Text(
          text = product.title,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = DarkPlum,
          maxLines = 1
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(vertical = 2.dp)
        ) {
          ConditionChip(condition = product.condition)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Batch: ${product.batchCode}",
            fontSize = 10.sp,
            color = Color.Gray
          )
        }

        // Price & Add to bag
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "$${String.format("%.0f", product.glowPrice)}",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = RoseWine
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "$${String.format("%.0f", product.originalPrice)}",
              fontSize = 11.sp,
              color = Color.Gray,
              textDecoration = TextDecoration.LineThrough
            )
            Spacer(modifier = Modifier.width(4.dp))
            SavingsTag(discountPercent = product.discountPercent)
          }

          Button(
            onClick = onAddToCart,
            colors = ButtonDefaults.buttonColors(containerColor = RoseWine, contentColor = Color.White),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.height(30.dp)
          ) {
            Text("Add to Web Bag", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun WebPressProofSection() {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    colors = CardDefaults.cardColors(containerColor = SoftBlush),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "AS FEATURED IN",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = RoseWine,
        letterSpacing = 1.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "\"GlowSwap is revolutionizing beauty sustainability by turning cosmetic vanity overstock into trusted liquid currency.\"",
        fontSize = 13.sp,
        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
        textAlign = TextAlign.Center,
        color = DarkPlum,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "— VOGUE BEAUTY DIRECTORY",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = ChampagneGold
      )
    }
  }
}

@Composable
private fun WebNewsletterSection(
  email: String,
  onEmailChange: (String) -> Unit,
  isSubscribed: Boolean,
  onSubscribe: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkPlum),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "Join The GlowSwap Club",
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Receive early access to verified overstock drops & 20% off your first checkout.",
        color = Color(0xFFE2D9E2),
        fontSize = 11.sp,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(10.dp))

      if (isSubscribed) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .background(Color(0xFF2E7D32), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("You're in! Use code GLOW20 at checkout.", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      } else {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = { Text("Enter your email...", fontSize = 11.sp, color = Color.LightGray) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ChampagneGold,
              unfocusedBorderColor = Color.Gray,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            modifier = Modifier.weight(1f)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = onSubscribe,
            colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = DarkPlum),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Join", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }
  }
}

@Composable
private fun WebFooter(onReturnToApp: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF19101B))
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text("GLOWSWAP BEAUTY INC.", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "Verified Pre-Loved & Warehouse Overstock Marketplace\nNew York • London • Paris",
      color = Color.Gray,
      fontSize = 10.sp,
      textAlign = TextAlign.Center,
      lineHeight = 14.sp
    )

    Spacer(modifier = Modifier.height(14.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
      Text("Authentication Lab", color = Color.LightGray, fontSize = 11.sp)
      Text("Sanitization Standards", color = Color.LightGray, fontSize = 11.sp)
      Text("Privacy", color = Color.LightGray, fontSize = 11.sp)
      Text("Terms", color = Color.LightGray, fontSize = 11.sp)
    }

    Spacer(modifier = Modifier.height(14.dp))

    Button(
      onClick = onReturnToApp,
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38233C), contentColor = Color.White),
      shape = RoundedCornerShape(20.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Switch Back to Native Android App", fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Text("© 2026 GlowSwap Inc. All rights reserved.", color = Color(0xFF6E5F70), fontSize = 9.sp)
  }
}
