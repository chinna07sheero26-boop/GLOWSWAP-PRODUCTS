package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BeautyProduct
import com.example.data.model.ConditionRating
import com.example.data.model.ProductCategory
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkPlum
import com.example.ui.theme.DiscountRed
import com.example.ui.theme.RoseWine
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.SwapPurple
import com.example.ui.theme.VerifiedGreen

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(Color(0xFFE8F5E9))
      .padding(horizontal = 6.dp, vertical = 3.dp)
  ) {
    Icon(
      imageVector = Icons.Default.CheckCircle,
      contentDescription = "Verified Authentic",
      tint = VerifiedGreen,
      modifier = Modifier.size(12.dp)
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
      text = "GlowCheck™",
      color = VerifiedGreen,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun ConditionChip(condition: ConditionRating, modifier: Modifier = Modifier) {
  val (bgColor, textColor) = when (condition) {
    ConditionRating.OVERSTOCK_SEALED -> Pair(Color(0xFFFEF3C7), Color(0xFF92400E))
    ConditionRating.BNIB -> Pair(Color(0xFFEFF6FF), Color(0xFF1E40AF))
    ConditionRating.SWATCHED_ONCE -> Pair(Color(0xFFFCE7F3), Color(0xFF9D174D))
    ConditionRating.LIGHTLY_LOVED_90 -> Pair(Color(0xFFF3E8FF), Color(0xFF6B21A8))
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = bgColor,
    modifier = modifier
  ) {
    Text(
      text = condition.shortBadge,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
    )
  }
}

@Composable
fun SavingsTag(discountPercent: Int, modifier: Modifier = Modifier) {
  if (discountPercent > 0) {
    Box(
      modifier = modifier
        .clip(RoundedCornerShape(4.dp))
        .background(DiscountRed)
        .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
      Text(
        text = "-$discountPercent%",
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun CategoryIcon(category: ProductCategory, tint: Color = RoseWine, size: Int = 20) {
  val icon: ImageVector = when (category) {
    ProductCategory.ALL -> Icons.Default.AutoAwesome
    ProductCategory.SKINCARE -> Icons.Default.Spa
    ProductCategory.MAKEUP -> Icons.Default.LocalFlorist
    ProductCategory.FRAGRANCE -> Icons.Default.FilterVintage
    ProductCategory.HAIRCARE -> Icons.Default.Face
    ProductCategory.TOOLS -> Icons.Default.AutoAwesome
  }
  Icon(
    imageVector = icon,
    contentDescription = category.displayName,
    tint = tint,
    modifier = Modifier.size(size.dp)
  )
}

@Composable
fun ProductVisualPlaceholder(
  category: ProductCategory,
  title: String,
  accentColor: Color,
  fillPercentage: Int,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .background(
        Brush.verticalGradient(
          colors = listOf(
            accentColor.copy(alpha = 0.25f),
            accentColor.copy(alpha = 0.08f)
          )
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.85f))
          .border(1.dp, accentColor.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        CategoryIcon(category = category, tint = accentColor, size = 32)
      }
      Spacer(modifier = Modifier.height(6.dp))
      // Fill level pill indicator
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.9f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22000000))
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(if (fillPercentage >= 95) VerifiedGreen else ChampagneGold)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "$fillPercentage% Fill",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = DarkPlum
          )
        }
      }
    }
  }
}

@Composable
fun ProductCard(
  product: BeautyProduct,
  onClick: () -> Unit,
  onAddToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .testTag("product_card_${product.id}")
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column {
      // Top visual banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
      ) {
        ProductVisualPlaceholder(
          category = product.category,
          title = product.title,
          accentColor = Color(product.accentColorHex),
          fillPercentage = product.fillPercentage,
          modifier = Modifier.matchParentSize()
        )

        // Top badges row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          ConditionChip(condition = product.condition)
          if (product.isOpenToSwap) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFF3E8FF),
              modifier = Modifier.padding(start = 4.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.SyncAlt,
                  contentDescription = "Swap available",
                  tint = SwapPurple,
                  modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "Swap",
                  color = SwapPurple,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        // Bottom pill on image: discount
        if (product.discountPercent > 0) {
          SavingsTag(
            discountPercent = product.discountPercent,
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(8.dp)
          )
        }
      }

      // Details section
      Column(modifier = Modifier.padding(10.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = product.brand.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.8.sp
          )
          VerifiedBadge()
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = product.title,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Price comparison row
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "$${String.format("%.0f", product.glowPrice)}",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = RoseWine
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "$${String.format("%.0f", product.originalPrice)}",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textDecoration = TextDecoration.LineThrough
            )
          }

          IconButton(
            onClick = onAddToCart,
            modifier = Modifier
              .size(32.dp)
              .testTag("add_to_cart_${product.id}")
              .background(RoseWine.copy(alpha = 0.1f), CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.AddShoppingCart,
              contentDescription = "Add to Bag",
              tint = RoseWine,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}
