package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConditionRating
import com.example.data.model.ProductCategory
import com.example.ui.components.CategoryIcon
import com.example.ui.components.ProductCard
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkPlum
import com.example.ui.theme.RoseWine
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.SwapPurple
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun HomeScreen(
  viewModel: MarketplaceViewModel,
  modifier: Modifier = Modifier
) {
  val products by viewModel.filteredProducts.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val selectedCondition by viewModel.selectedCondition.collectAsState()
  val swapOnly by viewModel.swapOnlyFilter.collectAsState()
  val overstockOnly by viewModel.overstockOnlyFilter.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()
  val totalCartCount = cartItems.sumOf { it.quantity }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { viewModel.navigateTo(AppScreen.SellWizard) },
        icon = { Icon(Icons.Default.Add, contentDescription = "Sell") },
        text = { Text("List an Item", fontWeight = FontWeight.SemiBold) },
        containerColor = RoseWine,
        contentColor = Color.White,
        modifier = Modifier.testTag("sell_item_fab")
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Top App Bar with Branding & Navigation Badges
      HomeHeader(
        cartCount = totalCartCount,
        onOpenCart = { viewModel.navigateTo(AppScreen.CartCheckout) },
        onOpenBatchChecker = { viewModel.navigateTo(AppScreen.BatchChecker) },
        onOpenSwapHub = { viewModel.navigateTo(AppScreen.SwapHub) },
        onOpenWebStore = { viewModel.navigateTo(AppScreen.WebStorefront) }
      )

      // Search and Filter Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          placeholder = { Text("Search brand, shade, or skincare concern...", fontSize = 13.sp) },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = RoseWine)
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { viewModel.setSearchQuery("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color.Gray)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RoseWine,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("search_input")
        )
      }

      // Categories horizontal selector
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ProductCategory.entries.forEach { category ->
          val isSelected = category == selectedCategory
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) RoseWine else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clickable { viewModel.selectCategory(category) }
              .testTag("category_chip_${category.name}")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              CategoryIcon(
                category = category,
                tint = if (isSelected) Color.White else RoseWine,
                size = 16
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = category.displayName,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }

      // Secondary Filter Pills (Overstock, Swaps Only, BNIB)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilterChip(
          selected = overstockOnly,
          onClick = { viewModel.toggleOverstockFilter() },
          label = { Text("Overstock (Up to 70% off)", fontSize = 11.sp) },
          leadingIcon = {
            Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(14.dp))
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = ChampagneGold.copy(alpha = 0.2f),
            selectedLabelColor = DarkPlum
          )
        )

        FilterChip(
          selected = swapOnly,
          onClick = { viewModel.toggleSwapFilter() },
          label = { Text("Open to Swap", fontSize = 11.sp) },
          leadingIcon = {
            Icon(Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(14.dp))
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SwapPurple.copy(alpha = 0.15f),
            selectedLabelColor = SwapPurple
          )
        )

        ConditionRating.entries.forEach { cond ->
          val isSelected = selectedCondition == cond
          FilterChip(
            selected = isSelected,
            onClick = {
              viewModel.setConditionFilter(if (isSelected) null else cond)
            },
            label = { Text(cond.shortBadge, fontSize = 11.sp) }
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Products Grid with Featured Promo Banner
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        // Featured Hero Banner
        item(span = { GridItemSpan(2) }) {
          HeroDropBanner(
            onShopOverstock = { viewModel.toggleOverstockFilter() },
            onSwitchToWeb = { viewModel.navigateTo(AppScreen.WebStorefront) }
          )
        }

        // Trust Strip
        item(span = { GridItemSpan(2) }) {
          TrustNoticeStrip(
            onVerifyClick = { viewModel.navigateTo(AppScreen.BatchChecker) }
          )
        }

        // Section Title
        item(span = { GridItemSpan(2) }) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (searchQuery.isNotBlank()) "Search Results (${products.size})" else "Featured Resale & Drops",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${products.size} available",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Product Cards
        items(products, key = { it.id }) { product ->
          ProductCard(
            product = product,
            onClick = { viewModel.navigateTo(AppScreen.ProductDetail(product)) },
            onAddToCart = { viewModel.addToCart(product) }
          )
        }

        if (products.isEmpty()) {
          item(span = { GridItemSpan(2) }) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  Icons.Default.FilterList,
                  contentDescription = null,
                  modifier = Modifier.size(48.dp),
                  tint = Color.LightGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("No matching beauty listings found", fontWeight = FontWeight.SemiBold)
                Text(
                  "Try clearing active filters or searching another brand",
                  fontSize = 12.sp,
                  color = Color.Gray
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun HomeHeader(
  cartCount: Int,
  onOpenCart: () -> Unit,
  onOpenBatchChecker: () -> Unit,
  onOpenSwapHub: () -> Unit,
  onOpenWebStore: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Brand title
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "GLOW",
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          color = RoseWine,
          letterSpacing = 1.sp
        )
        Text(
          text = "SWAP",
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          color = ChampagneGold,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(RoseWine.copy(alpha = 0.1f))
            .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
          Text("BEAUTY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = RoseWine)
        }
      }
      Text(
        text = "Verified Pre-Loved & Overstock Resale",
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // Header Action Icons
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Website View Button (Directly satisfies "create website")
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = SoftBlush,
        modifier = Modifier
          .clickable(onClick = onOpenWebStore)
          .testTag("toggle_web_store_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Language,
            contentDescription = "Website Storefront",
            tint = RoseWine,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Web Store",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = RoseWine
          )
        }
      }

      Spacer(modifier = Modifier.width(4.dp))

      // Batch Checker shortcut
      IconButton(
        onClick = onOpenBatchChecker,
        modifier = Modifier.testTag("batch_checker_header_btn")
      ) {
        Icon(
          imageVector = Icons.Default.QrCodeScanner,
          contentDescription = "Batch Authenticator",
          tint = MaterialTheme.colorScheme.onSurface
        )
      }

      // Swap Hub
      IconButton(
        onClick = onOpenSwapHub,
        modifier = Modifier.testTag("swap_hub_header_btn")
      ) {
        Icon(
          imageVector = Icons.Default.SyncAlt,
          contentDescription = "Swap Hub",
          tint = SwapPurple
        )
      }

      // Cart with badge
      IconButton(
        onClick = onOpenCart,
        modifier = Modifier.testTag("cart_header_btn")
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
          Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = "Bag",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

@Composable
private fun HeroDropBanner(
  onShopOverstock: () -> Unit,
  onSwitchToWeb: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkPlum),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              DarkPlum,
              Color(0xFF4A1E40),
              Color(0xFF2E1736)
            )
          )
        )
        .padding(16.dp)
    ) {
      Column {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = ChampagneGold
          ) {
            Text(
              text = "EDITORIAL DROP",
              color = DarkPlum,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Text(
            text = "Code: GLOW20 (20% Off)",
            color = Color(0xFFFFD180),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Luxury Beauty Without the Markup.",
          color = Color.White,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Shop authenticated overstock surplus & sanitized pre-loved holy grails from Augustinus Bader, Margiela, Dior, & Tilbury.",
          color = Color(0xFFF3E5F5),
          fontSize = 12.sp,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.clickable(onClick = onShopOverstock)
          ) {
            Text(
              text = "Shop Up to 70% Off",
              color = DarkPlum,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.15f),
            modifier = Modifier.clickable(onClick = onSwitchToWeb)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
              Icon(Icons.Default.Language, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Preview Web Store",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun TrustNoticeStrip(onVerifyClick: () -> Unit) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = Color(0xFFE8F5E9),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7)),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onVerifyClick)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Verified,
          contentDescription = null,
          tint = VerifiedGreen,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "GlowCheck™ Triple-Authentication Promise",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
          )
          Text(
            text = "Batch codes verified + UV-C & alcohol sanitized. Tap to test code.",
            fontSize = 10.sp,
            color = Color(0xFF2E7D32)
          )
        }
      }
      Icon(
        imageVector = Icons.Default.QrCodeScanner,
        contentDescription = "Test Code",
        tint = VerifiedGreen,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}
