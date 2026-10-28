package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.ui.components.CategoryIcon
import com.example.ui.components.ConditionChip
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkPlum
import com.example.ui.theme.DiscountRed
import com.example.ui.theme.RoseWine
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
  viewModel: MarketplaceViewModel,
  modifier: Modifier = Modifier
) {
  BackHandler {
    viewModel.navigateBack()
  }

  val cartItems by viewModel.cartItems.collectAsState()
  val appliedPromo by viewModel.appliedPromo.collectAsState()
  val orderConfirmation by viewModel.orderConfirmation.collectAsState()

  var promoInput by remember { mutableStateOf("") }
  var withProtection by remember { mutableStateOf(true) }
  var withThermalPackaging by remember { mutableStateOf(true) }

  val subtotal = cartItems.sumOf { it.product.glowPrice * it.quantity }
  val discount = if (appliedPromo == "GLOW20") subtotal * 0.20 else if (appliedPromo == "BEAUTY10") subtotal * 0.10 else 0.0
  val shippingCost = if (subtotal - discount > 45.0 || appliedPromo == "ECOFREE") 0.0 else 4.95
  val protectionCost = if (withProtection) 1.99 else 0.0
  val finalTotal = (subtotal - discount + shippingCost + protectionCost).coerceAtLeast(0.0)

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text("Shopping Bag (${cartItems.sumOf { it.quantity }})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
    bottomBar = {
      if (cartItems.isNotEmpty()) {
        Surface(
          shadowElevation = 8.dp,
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Estimated Total", fontSize = 11.sp, color = Color.Gray)
              Text(
                text = "$${String.format("%.2f", finalTotal)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = RoseWine
              )
            }

            Button(
              onClick = { viewModel.checkout(withProtection) },
              colors = ButtonDefaults.buttonColors(containerColor = RoseWine, contentColor = Color.White),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .height(48.dp)
                .testTag("complete_checkout_button")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Secure Checkout", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
            }
          }
        }
      }
    },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    if (cartItems.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
          Spacer(modifier = Modifier.height(12.dp))
          Text("Your GlowSwap Bag is Empty", fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Explore curated luxury overstock & pre-loved drops.", color = Color.Gray, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { viewModel.navigateTo(AppScreen.Home) },
            colors = ButtonDefaults.buttonColors(containerColor = RoseWine, contentColor = Color.White)
          ) {
            Text("Browse Marketplace")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(horizontal = 16.dp)
      ) {
        // Free shipping progress
        item {
          val remaining = (45.0 - subtotal).coerceAtLeast(0.0)
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (remaining == 0.0) Color(0xFFE8F5E9) else SoftBlush,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp)
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (remaining == 0.0) Icons.Default.CheckCircle else Icons.Default.LocalShipping,
                contentDescription = null,
                tint = if (remaining == 0.0) VerifiedGreen else RoseWine,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (remaining == 0.0) "You've unlocked complimentary insured delivery!" else "Add $${String.format("%.2f", remaining)} more for Free Insured Courier Delivery",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (remaining == 0.0) VerifiedGreen else DarkPlum
              )
            }
          }
        }

        // Cart items list
        items(cartItems, key = { it.product.id }) { item ->
          CartItemRow(
            item = item,
            onIncrement = { viewModel.updateCartQuantity(item.product.id, 1) },
            onDecrement = { viewModel.updateCartQuantity(item.product.id, -1) },
            onRemove = { viewModel.removeFromCart(item.product.id) }
          )
          Spacer(modifier = Modifier.height(8.dp))
        }

        // Add-ons & protection options
        item {
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("GlowCheck™ Guarantees", fontWeight = FontWeight.Bold, fontSize = 12.sp)

              Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                  checked = withProtection,
                  onCheckedChange = { withProtection = it },
                  colors = CheckboxDefaults.colors(checkedColor = RoseWine)
                )
                Column(modifier = Modifier.weight(1f)) {
                  Text("Transit Breakage & Spoilage Guarantee ($1.99)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                  Text("Instant refund if cosmetic arrives broken or seals tampered", fontSize = 10.sp, color = Color.Gray)
                }
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                  checked = withThermalPackaging,
                  onCheckedChange = { withThermalPackaging = it },
                  colors = CheckboxDefaults.colors(checkedColor = RoseWine)
                )
                Column(modifier = Modifier.weight(1f)) {
                  Text("Eco Insulated Thermal Shield (Complimentary)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                  Text("Protects serum actives and fragrance notes from temperature shifts", fontSize = 10.sp, color = Color.Gray)
                }
              }
            }
          }
        }

        // Promo code box
        item {
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              if (appliedPromo != null) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Code '$appliedPromo' active!", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VerifiedGreen)
                  }
                  IconButton(onClick = { viewModel.removePromoCode() }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Remove promo", tint = Color.Gray)
                  }
                }
              } else {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  OutlinedTextField(
                    value = promoInput,
                    onValueChange = { promoInput = it },
                    placeholder = { Text("Try 'GLOW20' for 20% off", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier
                      .weight(1f)
                      .testTag("promo_code_input")
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Button(
                    onClick = {
                      if (promoInput.isNotBlank()) {
                        viewModel.applyPromoCode(promoInput)
                        promoInput = ""
                      }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPlum, contentColor = Color.White),
                    modifier = Modifier.testTag("apply_promo_button")
                  ) {
                    Text("Apply")
                  }
                }
              }
            }
          }
        }

        // Order Summary Breakdown
        item {
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Spacer(modifier = Modifier.height(8.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", fontSize = 12.sp, color = Color.Gray)
                Text("$${String.format("%.2f", subtotal)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }

              if (discount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Promo Discount", fontSize = 12.sp, color = DiscountRed)
                  Text("-$${String.format("%.2f", discount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DiscountRed)
                }
              }

              Spacer(modifier = Modifier.height(4.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Insured Delivery", fontSize = 12.sp, color = Color.Gray)
                Text(if (shippingCost == 0.0) "FREE" else "$${String.format("%.2f", shippingCost)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }

              if (withProtection) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("GlowCheck™ Transit Guarantee", fontSize = 12.sp, color = Color.Gray)
                  Text("$1.99", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              Divider()
              Spacer(modifier = Modifier.height(8.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("$${String.format("%.2f", finalTotal)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = RoseWine)
              }
            }
          }
          Spacer(modifier = Modifier.height(80.dp))
        }
      }
    }
  }

  // Order Confirmation Modal Dialog
  orderConfirmation?.let { order ->
    AlertDialog(
      onDismissRequest = { viewModel.clearOrderConfirmation() },
      icon = {
        Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(36.dp))
      },
      title = {
        Text("Order Confirmed & Protected", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Thank you for supporting circular beauty! Your items have been reserved and sent to GlowCheck™ for pre-shipping physical inspection.",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = Color.DarkGray
          )

          Spacer(modifier = Modifier.height(14.dp))

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("Order Number: ${order.orderId}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text("Tracking ID: ${order.trackingNumber}", fontSize = 11.sp, color = RoseWine, fontWeight = FontWeight.SemiBold)
              Text("Paid Amount: $${String.format("%.2f", order.finalTotal)}", fontSize = 11.sp)
              Text("Estimated Courier: ${order.estimatedDelivery}", fontSize = 10.sp, color = Color.Gray)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.clearOrderConfirmation()
            viewModel.navigateTo(AppScreen.Home)
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoseWine, contentColor = Color.White)
        ) {
          Text("Continue Exploring")
        }
      }
    )
  }
}

@Composable
private fun CartItemRow(
  item: CartItem,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit,
  onRemove: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(item.product.accentColorHex).copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        CategoryIcon(category = item.product.category, tint = Color(item.product.accentColorHex), size = 26)
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(item.product.brand.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Text(item.product.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Row(verticalAlignment = Alignment.CenterVertically) {
          ConditionChip(condition = item.product.condition)
          Spacer(modifier = Modifier.width(6.dp))
          Text("$${String.format("%.0f", item.product.glowPrice)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoseWine)
        }
      }

      // Quantity adjustments
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        IconButton(
          onClick = onDecrement,
          modifier = Modifier
            .size(28.dp)
            .background(Color.White, CircleShape)
        ) {
          Icon(Icons.Default.Remove, contentDescription = "Decrement", modifier = Modifier.size(14.dp))
        }

        Text(item.quantity.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))

        IconButton(
          onClick = onIncrement,
          modifier = Modifier
            .size(28.dp)
            .background(Color.White, CircleShape)
        ) {
          Icon(Icons.Default.Add, contentDescription = "Increment", modifier = Modifier.size(14.dp))
        }

        IconButton(
          onClick = onRemove,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}
