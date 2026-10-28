package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SwapOffer
import com.example.data.model.SwapOfferStatus
import com.example.ui.components.CategoryIcon
import com.example.ui.components.ConditionChip
import com.example.ui.theme.DarkPlum
import com.example.ui.theme.RoseWine
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.SwapPurple
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapHubScreen(
  viewModel: MarketplaceViewModel,
  modifier: Modifier = Modifier
) {
  BackHandler {
    viewModel.navigateBack()
  }

  val swapOffers by viewModel.swapOffers.collectAsState()
  val allProducts by viewModel.filteredProducts.collectAsState()
  val swapEligibleProducts = allProducts.filter { it.isOpenToSwap }
  var selectedTab by remember { mutableIntStateOf(0) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.SyncAlt, contentDescription = null, tint = SwapPurple, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Beauty Swap Lounge", fontWeight = FontWeight.Bold, fontSize = 16.sp)
          }
        },
        navigationIcon = {
          IconButton(onClick = { viewModel.navigateBack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Trust Card: How GlowSwap Trades Work
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF)),
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = SwapPurple, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guaranteed Escrow Swapping", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkPlum)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Never worry about bad trades or empty packages. Both swappers ship their items to our central GlowCheck™ lab. Once both items are authenticated & sanitized, we forward them to their new homes.",
            fontSize = 11.sp,
            color = Color(0xFF4A1E40),
            lineHeight = 15.sp
          )
        }
      }

      // Tabs: Active Swaps vs Available Items to Swap
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = SwapPurple,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = SwapPurple
          )
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Text(
              "Active Proposals (${swapOffers.size})",
              fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
            )
          }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Text(
              "Trade Wishlists (${swapEligibleProducts.size})",
              fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
            )
          }
        )
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        if (selectedTab == 0) {
          // Active Offers List
          items(swapOffers, key = { it.id }) { offer ->
            SwapOfferCard(offer = offer)
            Spacer(modifier = Modifier.height(10.dp))
          }

          if (swapOffers.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 40.dp),
                contentAlignment = Alignment.Center
              ) {
                Text("No active swap proposals yet.", color = Color.Gray)
              }
            }
          }
        } else {
          // Eligible products for swapping
          items(swapEligibleProducts, key = { it.id }) { product ->
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { viewModel.navigateTo(AppScreen.ProductDetail(product)) }
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(product.accentColorHex).copy(alpha = 0.25f)),
                  contentAlignment = Alignment.Center
                ) {
                  CategoryIcon(category = product.category, tint = Color(product.accentColorHex), size = 26)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = product.brand.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                  )
                  Text(
                    text = product.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                  )
                  if (product.swapWishlist.isNotBlank()) {
                    Text(
                      text = "Seeking: ${product.swapWishlist}",
                      fontSize = 11.sp,
                      color = SwapPurple,
                      maxLines = 1
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = SwapPurple.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = "Propose",
                    color = SwapPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SwapOfferCard(offer: SwapOffer) {
  val (statusLabel, statusColor, statusIcon) = when (offer.status) {
    SwapOfferStatus.PENDING -> Triple("Under Seller Review", Color(0xFFD97706), Icons.Default.PendingActions)
    SwapOfferStatus.ACCEPTED -> Triple("Offer Accepted", VerifiedGreen, Icons.Default.CheckCircle)
    SwapOfferStatus.SHIPPED_TO_HUB -> Triple("In Transit to GlowCheck Lab", Color(0xFF2563EB), Icons.Default.LocalShipping)
    SwapOfferStatus.AUTHENTICATING -> Triple("Inspecting in GlowCheck Lab", SwapPurple, Icons.Default.Verified)
    SwapOfferStatus.COMPLETED -> Triple("Exchange Completed", VerifiedGreen, Icons.Default.CheckCircle)
  }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = offer.id,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color.Gray
        )
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = statusColor.copy(alpha = 0.15f)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(statusLabel, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Exchange breakdown
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text("YOU OFFER:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
          Text(offer.offeredItemTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Text(offer.offeredItemBrand, fontSize = 10.sp, color = Color.DarkGray)
          ConditionChip(condition = offer.offeredItemCondition, modifier = Modifier.padding(top = 2.dp))
        }

        Icon(
          Icons.Default.SyncAlt,
          contentDescription = null,
          tint = SwapPurple,
          modifier = Modifier
            .padding(horizontal = 8.dp)
            .size(20.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
          Text("YOU RECEIVE:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
          Text(offer.targetProductTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Text(offer.targetProductBrand, fontSize = 10.sp, color = Color.DarkGray)
        }
      }

      if (offer.cashAdjustment != 0.0) {
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = if (offer.cashAdjustment > 0) "+ $${offer.cashAdjustment.toInt()} Cash difference offered" else "Requesting $${(-offer.cashAdjustment).toInt()} cash offset",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = DarkPlum
        )
      }
    }
  }
}
