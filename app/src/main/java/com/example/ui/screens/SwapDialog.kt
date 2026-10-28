package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.BeautyProduct
import com.example.data.model.ConditionRating
import com.example.ui.theme.DarkPlum
import com.example.ui.theme.SwapPurple

@Composable
fun SwapOfferDialog(
  targetProduct: BeautyProduct,
  onDismiss: () -> Unit,
  onSubmit: (title: String, brand: String, condition: ConditionRating, cashTopUp: Double) -> Unit
) {
  var offeredTitle by remember { mutableStateOf("") }
  var offeredBrand by remember { mutableStateOf("") }
  var selectedCondition by remember { mutableStateOf(ConditionRating.BNIB) }
  var cashAdjustment by remember { mutableStateOf("0") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.SyncAlt, contentDescription = null, tint = SwapPurple, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Propose Beauty Swap", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Target item card
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("YOU RECEIVE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SwapPurple)
            Text(targetProduct.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("by ${targetProduct.brand} • Valued at $${targetProduct.glowPrice.toInt()}", fontSize = 11.sp, color = Color.Gray)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("WHAT YOU ARE OFFERING IN TRADE:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkPlum)

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = offeredBrand,
          onValueChange = { offeredBrand = it },
          label = { Text("Brand (e.g. Diptyque, Byredo, Sisley)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("swap_brand_input")
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = offeredTitle,
          onValueChange = { offeredTitle = it },
          label = { Text("Product Name & Shade/Size") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("swap_title_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Condition:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          ConditionRating.entries.forEach { cond ->
            val isSelected = selectedCondition == cond
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isSelected) SwapPurple else Color(0xFFEDE7F6),
              modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
            ) {
              OutlinedButton(
                onClick = { selectedCondition = cond },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp),
                border = null
              ) {
                Text(
                  text = cond.shortBadge,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else DarkPlum
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = cashAdjustment,
          onValueChange = { cashAdjustment = it },
          label = { Text("Cash Difference Top-Up ($)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("swap_cash_input")
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "GlowCheck™ Escrow: Both items are shipped to our hub for physical authenticity inspection before forwarding.",
          fontSize = 10.sp,
          color = Color.Gray,
          lineHeight = 13.sp
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val cash = cashAdjustment.toDoubleOrNull() ?: 0.0
          if (offeredTitle.isNotBlank()) {
            onSubmit(offeredTitle, offeredBrand.ifBlank { "Luxury Brand" }, selectedCondition, cash)
          }
        },
        enabled = offeredTitle.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = SwapPurple, contentColor = Color.White),
        modifier = Modifier.testTag("submit_swap_offer_button")
      ) {
        Text("Send Proposal", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
