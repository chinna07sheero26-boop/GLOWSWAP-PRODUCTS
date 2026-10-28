package com.example.data.model

enum class ProductCategory(val displayName: String) {
  ALL("All"),
  SKINCARE("Skincare"),
  MAKEUP("Makeup"),
  FRAGRANCE("Fragrance"),
  HAIRCARE("Haircare"),
  TOOLS("Tools & Sets")
}

enum class ConditionRating(
  val label: String,
  val shortBadge: String,
  val description: String,
  val fillEstimate: Int
) {
  OVERSTOCK_SEALED(
    label = "Overstock Sealed",
    shortBadge = "Sealed",
    description = "Authentic manufacturer surplus, factory sealed box",
    fillEstimate = 100
  ),
  BNIB(
    label = "BNIB - Brand New in Box",
    shortBadge = "BNIB",
    description = "Never opened, mint condition in original packaging",
    fillEstimate = 100
  ),
  SWATCHED_ONCE(
    label = "Swatched Once",
    shortBadge = "Swatched 1x",
    description = "Tested once with clean spatula on clean skin, sanitized",
    fillEstimate = 98
  ),
  LIGHTLY_LOVED_90(
    label = "Lightly Loved (90%+)",
    shortBadge = "90%+ Left",
    description = "90%+ volume remaining, UV-C & 70% alcohol sanitized",
    fillEstimate = 92
  )
}

enum class VerificationStatus(val label: String, val badgeColor: Long) {
  AUTHENTIC_VERIFIED("GlowCheck™ Verified", 0xFF1E7E52),
  BATCH_VALIDATED("Batch Validated", 0xFF2D6A4F),
  SEAL_INSPECTED("Seal Inspected", 0xFF1B4332)
}

data class BeautyProduct(
  val id: String,
  val title: String,
  val brand: String,
  val category: ProductCategory,
  val originalPrice: Double,
  val glowPrice: Double,
  val condition: ConditionRating,
  val batchCode: String,
  val sanitizedMethod: String,
  val fillPercentage: Int,
  val sellerName: String,
  val sellerRating: Double,
  val sellerSalesCount: Int,
  val isVerifiedSeller: Boolean,
  val isOpenToSwap: Boolean,
  val swapWishlist: String = "",
  val description: String,
  val volumeMl: String,
  val accentColorHex: Long = 0xFFF3E5F5,
  val tags: List<String> = emptyList(),
  val isOverstockSale: Boolean = false
) {
  val discountPercent: Int
    get() = if (originalPrice > 0) (((originalPrice - glowPrice) / originalPrice) * 100).toInt() else 0
}

data class CartItem(
  val product: BeautyProduct,
  val quantity: Int = 1
)

enum class SwapOfferStatus {
  PENDING,
  ACCEPTED,
  SHIPPED_TO_HUB,
  AUTHENTICATING,
  COMPLETED
}

data class SwapOffer(
  val id: String,
  val targetProductId: String,
  val targetProductTitle: String,
  val targetProductBrand: String,
  val offeredItemTitle: String,
  val offeredItemBrand: String,
  val offeredItemCondition: ConditionRating,
  val cashAdjustment: Double, // positive if offering extra cash, negative if requesting cash
  val status: SwapOfferStatus = SwapOfferStatus.PENDING,
  val dateFormatted: String = "Just now"
)

data class BatchCheckResult(
  val brand: String,
  val batchCode: String,
  val isValid: Boolean,
  val mfgDateFormatted: String,
  val ageInMonths: Int,
  val paoMonths: Int,
  val safetyStatus: SafetyStatus,
  val safetyMessage: String
)

enum class SafetyStatus {
  FRESH,
  GOOD,
  APPROACHING_EXPIRY,
  EXPIRED,
  INVALID_CODE
}
