package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConditionRating
import com.example.data.model.ProductCategory
import com.example.ui.theme.DarkPlum
import com.example.ui.theme.RoseWine
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.SwapPurple
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellWizardScreen(
  viewModel: MarketplaceViewModel,
  modifier: Modifier = Modifier
) {
  BackHandler {
    viewModel.navigateBack()
  }

  var brand by remember { mutableStateOf("") }
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf(ProductCategory.SKINCARE) }
  var retailPriceInput by remember { mutableStateOf("65") }
  var glowPriceInput by remember { mutableStateOf("38") }
  var condition by remember { mutableStateOf(ConditionRating.BNIB) }
  var fillPercentage by remember { mutableFloatStateOf(100f) }
  var batchCode by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var volumeMl by remember { mutableStateOf("30 ml / 1.0 oz") }
  var isOpenToSwap by remember { mutableStateOf(true) }
  var swapWishlist by remember { mutableStateOf("") }

  var sanitizedConfirmed by remember { mutableStateOf(true) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text("List an Item on GlowSwap", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
      Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(modifier = Modifier.padding(16.dp)) {
          Button(
            onClick = {
              val retail = retailPriceInput.toDoubleOrNull() ?: 50.0
              val glow = glowPriceInput.toDoubleOrNull() ?: 25.0
              viewModel.createListing(
                title = title.ifBlank { "Prestige Beauty Item" },
                brand = brand.ifBlank { "Luxury Brand" },
                category = category,
                originalPrice = retail,
                glowPrice = glow,
                condition = condition,
                batchCode = batchCode.ifBlank { "LOT-2025" },
                sanitizedMethod = if (sanitizedConfirmed) "Verified 70% alcohol and clean spatula protocol" else "Unsanitized (Buyer must sanitize)",
                fillPercentage = fillPercentage.toInt(),
                isOpenToSwap = isOpenToSwap,
                swapWishlist = swapWishlist,
                description = description.ifBlank { "Authentic cosmetic listing in mint condition. Kept in climate controlled drawer." },
                volumeMl = volumeMl
              )
            },
            enabled = brand.isNotBlank() && title.isNotBlank() && sanitizedConfirmed,
            colors = ButtonDefaults.buttonColors(containerColor = RoseWine, contentColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("publish_listing_button")
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Publish to Marketplace", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }
      }
    },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      // Section 1: Item Details
      Text("1. Basic Information", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkPlum)
      Spacer(modifier = Modifier.height(6.dp))

      OutlinedTextField(
        value = brand,
        onValueChange = { brand = it },
        label = { Text("Brand (e.g. Dior, Chanel, Rhode)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("sell_brand_input")
      )

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Product Name & Shade/Size") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("sell_title_input")
      )

      Spacer(modifier = Modifier.height(10.dp))

      Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf(
          ProductCategory.SKINCARE,
          ProductCategory.MAKEUP,
          ProductCategory.FRAGRANCE,
          ProductCategory.HAIRCARE
        ).forEach { cat ->
          val isSelected = cat == category
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) RoseWine else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable { category = cat }
          ) {
            Text(
              text = cat.displayName,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 2: Condition & Fill Level
      Text("2. Condition & Volume", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkPlum)
      Spacer(modifier = Modifier.height(6.dp))

      ConditionRating.entries.forEach { cond ->
        val isSelected = condition == cond
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isSelected) SoftBlush else MaterialTheme.colorScheme.surfaceVariant,
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RoseWine else Color.Transparent),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable {
              condition = cond
              fillPercentage = cond.fillEstimate.toFloat()
            }
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(cond.label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkPlum)
              Text(cond.description, fontSize = 10.sp, color = Color.Gray)
            }
            if (isSelected) {
              Icon(Icons.Default.Check, contentDescription = null, tint = RoseWine, modifier = Modifier.size(16.dp))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text("Fill Level: ${fillPercentage.toInt()}% Remaining", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      Slider(
        value = fillPercentage,
        onValueChange = { fillPercentage = it },
        valueRange = 50f..100f,
        steps = 9,
        colors = SliderDefaults.colors(thumbColor = RoseWine, activeTrackColor = RoseWine)
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Section 3: Pricing
      Text("3. Resale Pricing", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkPlum)
      Spacer(modifier = Modifier.height(6.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = retailPriceInput,
          onValueChange = { retailPriceInput = it },
          label = { Text("Original Retail ($)") },
          singleLine = true,
          modifier = Modifier.weight(1f).testTag("sell_retail_input")
        )

        OutlinedTextField(
          value = glowPriceInput,
          onValueChange = { glowPriceInput = it },
          label = { Text("GlowSwap Price ($)") },
          singleLine = true,
          modifier = Modifier.weight(1f).testTag("sell_glow_input")
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 4: Batch Code & Authenticity
      Text("4. GlowCheck™ Authenticity", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkPlum)
      Spacer(modifier = Modifier.height(6.dp))

      OutlinedTextField(
        value = batchCode,
        onValueChange = { batchCode = it },
        label = { Text("Batch Code (printed on base / crimp)") },
        placeholder = { Text("e.g. 4D02, A82, 23W101") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("sell_batch_input")
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Sanitization Checkbox
      Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SoftBlush),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Checkbox(
            checked = sanitizedConfirmed,
            onCheckedChange = { sanitizedConfirmed = it },
            colors = CheckboxDefaults.colors(checkedColor = RoseWine)
          )
          Column(modifier = Modifier.weight(1f)) {
            Text("Sanitization & Hygiene Pledge", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkPlum)
            Text(
              "I certify that container exteriors, pumps, or compacts were sanitized using cosmetic alcohol mist or sterile spatulas.",
              fontSize = 10.sp,
              color = Color.DarkGray
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 5: Swap Preferences
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.SyncAlt, contentDescription = null, tint = SwapPurple, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Open to Beauty Trades / Swaps", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Switch(
              checked = isOpenToSwap,
              onCheckedChange = { isOpenToSwap = it },
              colors = SwitchDefaults.colors(checkedThumbColor = SwapPurple, checkedTrackColor = Color(0xFFF3E8FF))
            )
          }

          if (isOpenToSwap) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = swapWishlist,
              onValueChange = { swapWishlist = it },
              label = { Text("Wishlist (What would you trade for?)") },
              placeholder = { Text("e.g. Looking for Charlotte Tilbury Flawless Filter") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(100.dp))
    }
  }
}
