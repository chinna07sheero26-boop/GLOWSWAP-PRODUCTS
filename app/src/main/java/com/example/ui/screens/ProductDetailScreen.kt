package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BeautyProduct
import com.example.ui.components.CategoryIcon
import com.example.ui.components.ConditionChip
import com.example.ui.components.SavingsTag
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkPlum
import com.example.ui.theme.RoseWine
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.SwapPurple
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
  product: BeautyProduct,
  viewModel: MarketplaceViewModel,
  modifier: Modifier = Modifier
) {
  BackHandler {
    viewModel.navigateBack()
  }

  var showSwapDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = product.brand,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateBack() },
            modifier = Modifier.testTag("detail_back_button")
          ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { viewModel.navigateTo(AppScreen.CartCheckout) }) {
            Icon(Icons.Default.ShoppingBag, contentDescription = "Bag")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      DetailBottomBar(
        product = product,
        onAddToCart = { viewModel.addToCart(product) },
        onBuyNow = {
          viewModel.addToCart(product)
          viewModel.navigateTo(AppScreen.CartCheckout)
        },
        onOfferSwap = { showSwapDialog = true }
      )
    },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
    ) {
      // 1. Hero Product Artwork Presentation
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .background(
            Brush.verticalGradient(
              listOf(
                Color(product.accentColorHex).copy(alpha = 0.25f),
                Color(product.accentColorHex).copy(alpha = 0.08f)
              )
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(90.dp)
              .clip(CircleShape)
              .background(Color.White)
              .border(1.dp, Color(product.accentColorHex).copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            CategoryIcon(category = product.category, tint = Color(product.accentColorHex), size = 48)
          }
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.9f)
          ) {
            Text(
              text = product.volumeMl,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = DarkPlum,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        // Overstock or Swap badge on top right
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          ConditionChip(condition = product.condition)
          if (product.isOpenToSwap) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = SwapPurple
            ) {
              Text(
                text = "Open to Trade / Swap",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      // 2. Title, Pricing, & Savings
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = product.brand.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = RoseWine,
            letterSpacing = 1.sp
          )
          VerifiedBadge()
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = product.title,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          lineHeight = 26.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Price Section
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "$${String.format("%.2f", product.glowPrice)}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = RoseWine
          )

          Text(
            text = "Retail: $${String.format("%.2f", product.originalPrice)}",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textDecoration = TextDecoration.LineThrough
          )

          SavingsTag(discountPercent = product.discountPercent)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Fill Level & Condition Meter
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Certified Fill Level",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${product.fillPercentage}% Remaining",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (product.fillPercentage >= 95) VerifiedGreen else DarkPlum
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
              progress = { product.fillPercentage / 100f },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = if (product.fillPercentage >= 95) VerifiedGreen else ChampagneGold,
              trackColor = Color.LightGray.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = product.condition.description,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. GlowCheck™ Authenticity & Sanitization Passport
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SoftBlush),
          border = androidx.compose.foundation.BorderStroke(1.dp, RoseWine.copy(alpha = 0.2f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = RoseWine, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "GlowCheck™ Product Passport",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = DarkPlum
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Batch Code", fontSize = 10.sp, color = Color.Gray)
                Text(product.batchCode, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkPlum)
              }
              Column {
                Text("Authenticity", fontSize = 10.sp, color = Color.Gray)
                Text("100% Genuine", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VerifiedGreen)
              }
              Column {
                Text("Protection", fontSize = 10.sp, color = Color.Gray)
                Text("Buyer Escrow", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkPlum)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = RoseWine.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Top) {
              Icon(Icons.Default.CleaningServices, contentDescription = null, tint = RoseWine, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Sanitization Protocol: ${product.sanitizedMethod}",
                fontSize = 11.sp,
                color = DarkPlum,
                lineHeight = 15.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Description
        Text(
          text = "About This Item",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = product.description,
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 18.sp
        )

        // Swap wishlist if available
        if (product.isOpenToSwap && product.swapWishlist.isNotBlank()) {
          Spacer(modifier = Modifier.height(12.dp))
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFF3E8FF),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SyncAlt, contentDescription = null, tint = SwapPurple, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Seller's Swap Wishlist",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = SwapPurple
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = product.swapWishlist,
                fontSize = 11.sp,
                color = DarkPlum
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Seller Profile
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(RoseWine),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = product.sellerName.take(1).uppercase(),
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 18.sp
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = product.sellerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                  if (product.isVerifiedSeller) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                      Icons.Default.CheckCircle,
                      contentDescription = "Verified Seller",
                      tint = VerifiedGreen,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(13.dp))
                  Text(
                    text = " ${product.sellerRating} • ${product.sellerSalesCount} sales",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = ChampagneGold.copy(alpha = 0.2f)
            ) {
              Text(
                text = "Fast Shipper",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = DarkPlum,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // Swap Offer Dialog
  if (showSwapDialog) {
    SwapOfferDialog(
      targetProduct = product,
      onDismiss = { showSwapDialog = false },
      onSubmit = { title, brand, cond, cash ->
        viewModel.submitSwap(product, title, brand, cond, cash)
        showSwapDialog = false
      }
    )
  }
}

@Composable
private fun DetailBottomBar(
  product: BeautyProduct,
  onAddToCart: () -> Unit,
  onBuyNow: () -> Unit,
  onOfferSwap: () -> Unit
) {
  Surface(
    shadowElevation = 8.dp,
    color = MaterialTheme.colorScheme.surface,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (product.isOpenToSwap) {
        OutlinedButton(
          onClick = onOfferSwap,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = SwapPurple),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, SwapPurple),
          modifier = Modifier
            .weight(1f)
            .testTag("offer_swap_button")
        ) {
          Icon(Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Trade / Swap", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }

      OutlinedButton(
        onClick = onAddToCart,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseWine),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, RoseWine),
        modifier = Modifier
          .weight(1f)
          .testTag("detail_add_to_cart_button")
      ) {
        Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Add to Bag", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = onBuyNow,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = RoseWine, contentColor = Color.White),
        modifier = Modifier
          .weight(1.2f)
          .testTag("instant_buy_button")
      ) {
        Text("Buy $${String.format("%.0f", product.glowPrice)}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}
