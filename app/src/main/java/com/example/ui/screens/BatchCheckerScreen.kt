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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SafetyStatus
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkPlum
import com.example.ui.theme.RoseWine
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchCheckerScreen(
  viewModel: MarketplaceViewModel,
  modifier: Modifier = Modifier
) {
  BackHandler {
    viewModel.navigateBack()
  }

  val brands = listOf(
    "Augustinus Bader", "Charlotte Tilbury", "Dior", "Rare Beauty",
    "Maison Margiela", "Sol de Janeiro", "Le Labo", "Olaplex", "Chanel"
  )

  var selectedBrand by remember { mutableStateOf(brands[0]) }
  var batchCodeInput by remember { mutableStateOf("24AB801") }
  val result by viewModel.batchCheckResult.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text("GlowCheck™ Authenticator", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // Intro Banner
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SoftBlush),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Security, contentDescription = null, tint = RoseWine, modifier = Modifier.size(28.dp))
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text("Cosmetic Batch Code Decoder", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkPlum)
            Text(
              "Verify manufacturing year, shelf-life integrity, and PAO safety before buying or trading.",
              fontSize = 11.sp,
              color = Color.DarkGray
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text("Select Cosmetic Brand:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        brands.forEach { brand ->
          val isSelected = brand == selectedBrand
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) RoseWine else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable {
              selectedBrand = brand
              // Preset typical batch codes for convenience
              batchCodeInput = when (brand) {
                "Augustinus Bader" -> "24AB801"
                "Charlotte Tilbury" -> "CT92A"
                "Dior" -> "4D02"
                "Rare Beauty" -> "RB3211"
                "Maison Margiela" -> "38W8001"
                "Sol de Janeiro" -> "SDJ882"
                "Le Labo" -> "LL9842M"
                "Olaplex" -> "OLA771"
                else -> "CH7701"
              }
            }
          ) {
            Text(
              text = brand,
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text("Enter Batch Code (Lot No.):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(6.dp))

      OutlinedTextField(
        value = batchCodeInput,
        onValueChange = { batchCodeInput = it },
        placeholder = { Text("e.g. 4D02, 24AB801, A63") },
        leadingIcon = {
          Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = RoseWine)
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("batch_input_field")
      )

      Spacer(modifier = Modifier.height(12.dp))

      Button(
        onClick = { viewModel.checkBatch(selectedBrand, batchCodeInput) },
        colors = ButtonDefaults.buttonColors(containerColor = RoseWine, contentColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("run_batch_check_button")
      ) {
        Text("Verify Authenticity & Freshness", fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Verification Result Card
      result?.let { res ->
        val (statusColor, statusIcon, statusTitle) = when (res.safetyStatus) {
          SafetyStatus.FRESH -> Triple(VerifiedGreen, Icons.Default.CheckCircle, "Optimal Freshness")
          SafetyStatus.GOOD -> Triple(Color(0xFF2D6A4F), Icons.Default.CheckCircle, "Verified Good Condition")
          SafetyStatus.APPROACHING_EXPIRY -> Triple(Color(0xFFD97706), Icons.Default.Warning, "Approaching Expiry")
          SafetyStatus.EXPIRED -> Triple(Color(0xFFDC2626), Icons.Default.ErrorOutline, "Expired Formula")
          SafetyStatus.INVALID_CODE -> Triple(Color.Gray, Icons.Default.HelpOutline, "Unrecognized Format")
        }

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, statusColor.copy(alpha = 0.5f)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("batch_result_card")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(statusTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = statusColor)
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = statusColor.copy(alpha = 0.15f)
              ) {
                Text(
                  text = res.brand,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = statusColor,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider()
            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Production Date", fontSize = 10.sp, color = Color.Gray)
                Text(res.mfgDateFormatted, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
              Column {
                Text("Batch Age", fontSize = 10.sp, color = Color.Gray)
                Text("${res.ageInMonths} months old", fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
              Column {
                Text("PAO Period", fontSize = 10.sp, color = Color.Gray)
                Text("${res.paoMonths}M open safe", fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.White.copy(alpha = 0.7f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = res.safetyMessage,
                fontSize = 12.sp,
                color = DarkPlum,
                lineHeight = 16.sp,
                modifier = Modifier.padding(10.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Where to find batch codes guide
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = DarkPlum, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Where to Find the Batch Code", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "• Dropper bottles & jars: Laser-etched or stamped onto the base of the glass/acrylic container.\n• Tubes: Impressed onto the crimped top seam.\n• Palettes: Printed on the back label or stamped inside the mirror casing.\n• Fragrances: Etched onto the bottom flacon glass and mirrored on outer packaging.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
          )
        }
      }
    }
  }
}
