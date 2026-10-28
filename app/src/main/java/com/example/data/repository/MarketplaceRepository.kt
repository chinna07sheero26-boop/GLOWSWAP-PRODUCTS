package com.example.data.repository

import com.example.data.model.BatchCheckResult
import com.example.data.model.BeautyProduct
import com.example.data.model.CartItem
import com.example.data.model.ConditionRating
import com.example.data.model.ProductCategory
import com.example.data.model.SafetyStatus
import com.example.data.model.SwapOffer
import com.example.data.model.SwapOfferStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MarketplaceRepository {

  private val _products = MutableStateFlow<List<BeautyProduct>>(initialProducts)
  val products: StateFlow<List<BeautyProduct>> = _products.asStateFlow()

  private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
  val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

  private val _swapOffers = MutableStateFlow<List<SwapOffer>>(initialSwapOffers)
  val swapOffers: StateFlow<List<SwapOffer>> = _swapOffers.asStateFlow()

  private val _appliedPromoCode = MutableStateFlow<String?>(null)
  val appliedPromoCode: StateFlow<String?> = _appliedPromoCode.asStateFlow()

  fun addProduct(product: BeautyProduct) {
    _products.value = listOf(product) + _products.value
  }

  fun addToCart(product: BeautyProduct) {
    val current = _cartItems.value.toMutableList()
    val index = current.indexOfFirst { it.product.id == product.id }
    if (index >= 0) {
      current[index] = current[index].copy(quantity = current[index].quantity + 1)
    } else {
      current.add(CartItem(product = product, quantity = 1))
    }
    _cartItems.value = current
  }

  fun removeFromCart(productId: String) {
    _cartItems.value = _cartItems.value.filter { it.product.id != productId }
  }

  fun updateQuantity(productId: String, delta: Int) {
    val current = _cartItems.value.toMutableList()
    val index = current.indexOfFirst { it.product.id == productId }
    if (index >= 0) {
      val newQty = current[index].quantity + delta
      if (newQty <= 0) {
        current.removeAt(index)
      } else {
        current[index] = current[index].copy(quantity = newQty)
      }
      _cartItems.value = current
    }
  }

  fun clearCart() {
    _cartItems.value = emptyList()
    _appliedPromoCode.value = null
  }

  fun applyPromo(code: String): Boolean {
    val clean = code.trim().uppercase()
    return if (clean == "GLOW20" || clean == "BEAUTY10" || clean == "SWAP50" || clean == "ECOFREE") {
      _appliedPromoCode.value = clean
      true
    } else {
      false
    }
  }

  fun removePromo() {
    _appliedPromoCode.value = null
  }

  fun submitSwapOffer(
    targetProduct: BeautyProduct,
    offeredTitle: String,
    offeredBrand: String,
    offeredCondition: ConditionRating,
    cashTopUp: Double
  ) {
    val offer = SwapOffer(
      id = "SWAP-${UUID.randomUUID().toString().take(6).uppercase()}",
      targetProductId = targetProduct.id,
      targetProductTitle = targetProduct.title,
      targetProductBrand = targetProduct.brand,
      offeredItemTitle = offeredTitle,
      offeredItemBrand = offeredBrand,
      offeredItemCondition = offeredCondition,
      cashAdjustment = cashTopUp,
      status = SwapOfferStatus.PENDING,
      dateFormatted = "Just now"
    )
    _swapOffers.value = listOf(offer) + _swapOffers.value
  }

  fun checkBatchCode(brand: String, code: String): BatchCheckResult {
    val cleanCode = code.trim().uppercase()
    if (cleanCode.length < 3) {
      return BatchCheckResult(
        brand = brand,
        batchCode = code,
        isValid = false,
        mfgDateFormatted = "Unknown",
        ageInMonths = 0,
        paoMonths = 12,
        safetyStatus = SafetyStatus.INVALID_CODE,
        safetyMessage = "Batch code format too short. Check bottle bottom or crimp seal."
      )
    }

    // Algorithmic estimation based on luxury cosmetic lot conventions
    val hash = (cleanCode + brand).hashCode().let { kotlin.math.abs(it) }
    val monthsAgo = (hash % 16) + 2 // 2 to 18 months old
    val year = 2026 - (monthsAgo / 12)
    val monthInt = ((12 - (monthsAgo % 12)) % 12) + 1
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val mfgString = "${monthNames[monthInt - 1]} $year"

    val (status, msg) = when {
      monthsAgo <= 6 -> Pair(
        SafetyStatus.FRESH,
        "Optimal Freshness. Manufactured $monthsAgo months ago. Formula active and highly stable."
      )
      monthsAgo <= 14 -> Pair(
        SafetyStatus.GOOD,
        "Good Condition. Manufactured $monthsAgo months ago. Unopened shelf life is typically 36 months."
      )
      monthsAgo <= 24 -> Pair(
        SafetyStatus.APPROACHING_EXPIRY,
        "Acceptable for immediate use. Period After Opening (PAO) should be monitored upon seal break."
      )
      else -> Pair(
        SafetyStatus.EXPIRED,
        "Approaching end of recommended cosmetic shelf life. Do not use if separation or odor change occurs."
      )
    }

    return BatchCheckResult(
      brand = brand,
      batchCode = cleanCode,
      isValid = true,
      mfgDateFormatted = mfgString,
      ageInMonths = monthsAgo,
      paoMonths = if (brand.contains("Fragrance", ignoreCase = true) || brand.contains("Le Labo", ignoreCase = true)) 36 else 12,
      safetyStatus = status,
      safetyMessage = msg
    )
  }

  companion object {
    val initialProducts = listOf(
      BeautyProduct(
        id = "glow_001",
        title = "The Rich Cream with TFC8®",
        brand = "Augustinus Bader",
        category = ProductCategory.SKINCARE,
        originalPrice = 290.0,
        glowPrice = 145.0,
        condition = ConditionRating.BNIB,
        batchCode = "24AB801",
        sanitizedMethod = "Factory Foil Sealed + External UV-C Scan",
        fillPercentage = 100,
        sellerName = "CosmeticCurator_NYC",
        sellerRating = 4.9,
        sellerSalesCount = 184,
        isVerifiedSeller = true,
        isOpenToSwap = true,
        swapWishlist = "Seeking Sisley Black Rose or La Mer Moisturizing Soft Cream",
        description = "Full 50ml size brand new in original gold-embossed box with unbroken safety tamper seal. Overstock from luxury department store distributor.",
        volumeMl = "50 ml / 1.7 oz",
        accentColorHex = 0xFF1D2D50,
        tags = listOf("Overstock", "BNIB", "Holy Grail", "Anti-Aging"),
        isOverstockSale = true
      ),
      BeautyProduct(
        id = "glow_002",
        title = "Replica Jazz Club Eau de Toilette",
        brand = "Maison Margiela",
        category = ProductCategory.FRAGRANCE,
        originalPrice = 165.0,
        glowPrice = 88.0,
        condition = ConditionRating.LIGHTLY_LOVED_90,
        batchCode = "38W8001",
        sanitizedMethod = "Nozzle Atomizer Sanitized with 70% Isopropyl Alcohol",
        fillPercentage = 94,
        sellerName = "ScentCollector_LA",
        sellerRating = 5.0,
        sellerSalesCount = 92,
        isVerifiedSeller = true,
        isOpenToSwap = true,
        swapWishlist = "Open to trade for Byredo Gypsy Water or Diptyque Philosykos",
        description = "Stored strictly in temperature-controlled dark vanity. Sprayed approximately 10-12 times. Batch code verified authentic 2025 production.",
        volumeMl = "100 ml / 3.4 oz",
        accentColorHex = 0xFFD4A373,
        tags = listOf("Pre-Loved", "90%+ Left", "Warm Tobacco & Rum", "Unisex")
      ),
      BeautyProduct(
        id = "glow_003",
        title = "Pillow Talk Luxury Eyeshadow Palette",
        brand = "Charlotte Tilbury",
        category = ProductCategory.MAKEUP,
        originalPrice = 55.0,
        glowPrice = 32.0,
        condition = ConditionRating.SWATCHED_ONCE,
        batchCode = "CT92A",
        sanitizedMethod = "Sanitized with sterile professional UV-C chamber & surface alcohol mist",
        fillPercentage = 98,
        sellerName = "GlowMuse_Elena",
        sellerRating = 4.8,
        sellerSalesCount = 47,
        isVerifiedSeller = true,
        isOpenToSwap = false,
        swapWishlist = "",
        description = "Iconic 4-pan nude pink dream palette. Only the 'Pop' shade was swatched once on forearm with clean disposable brush. Zero pan indentation.",
        volumeMl = "5.2 g",
        accentColorHex = 0xFFC77D88,
        tags = listOf("Swatched Once", "Sanitized", "Bestseller")
      ),
      BeautyProduct(
        id = "glow_004",
        title = "Dior Addict Lip Glow Oil (001 Pink)",
        brand = "Dior",
        category = ProductCategory.MAKEUP,
        originalPrice = 40.0,
        glowPrice = 24.0,
        condition = ConditionRating.OVERSTOCK_SEALED,
        batchCode = "4D02",
        sanitizedMethod = "Original Outer Box and Factory Cello Wrap Intact",
        fillPercentage = 100,
        sellerName = "BeautyVault_Express",
        sellerRating = 5.0,
        sellerSalesCount = 310,
        isVerifiedSeller = true,
        isOpenToSwap = true,
        swapWishlist = "Looking for Rhode Peptide Lip Tint in Toast or Espresso",
        description = "Cherry oil-infused nourishing lip oil that brings out natural rosy sheen. Brand new sealed overstock from authorized European retailer.",
        volumeMl = "6 ml / 0.20 oz",
        accentColorHex = 0xFFF7CAD0,
        tags = listOf("Brand New", "Sealed", "Trending TikTok"),
        isOverstockSale = true
      ),
      BeautyProduct(
        id = "glow_005",
        title = "Soft Pinch Liquid Blush (Joy)",
        brand = "Rare Beauty",
        category = ProductCategory.MAKEUP,
        originalPrice = 23.0,
        glowPrice = 14.0,
        condition = ConditionRating.SWATCHED_ONCE,
        batchCode = "RB3211",
        sanitizedMethod = "Doe-foot applicator cleaned with cosmetic sanitizer",
        fillPercentage = 97,
        sellerName = "MinimalGlam_Sam",
        sellerRating = 4.9,
        sellerSalesCount = 28,
        isVerifiedSeller = true,
        isOpenToSwap = true,
        swapWishlist = "Will swap for Rare Beauty blush in Encourage or Hope",
        description = "Weightless, long-lasting dewy liquid blush. Gorgeous muted peach glow. Swatched only once on clean skin palette.",
        volumeMl = "7.5 ml / 0.25 oz",
        accentColorHex = 0xFFFFB4A2,
        tags = listOf("Dewy Finish", "Swatched 1x", "Viral")
      ),
      BeautyProduct(
        id = "glow_006",
        title = "Brazilian Bum Bum Cream (Overstock)",
        brand = "Sol de Janeiro",
        category = ProductCategory.SKINCARE,
        originalPrice = 48.0,
        glowPrice = 29.0,
        condition = ConditionRating.OVERSTOCK_SEALED,
        batchCode = "SDJ882",
        sanitizedMethod = "Inner Protective Pull-Tab Foil Seal Intact",
        fillPercentage = 100,
        sellerName = "SoleilResale",
        sellerRating = 4.7,
        sellerSalesCount = 65,
        isVerifiedSeller = false,
        isOpenToSwap = false,
        swapWishlist = "",
        description = "Award-winning body cream infused with Guaraná and Pistachio & Salted Caramel Cheirosa '62 scent. 100% genuine sealed overstock.",
        volumeMl = "240 ml / 8.1 oz",
        accentColorHex = 0xFFF4A261,
        tags = listOf("Sealed", "Overstock", "Cheirosa 62"),
        isOverstockSale = true
      ),
      BeautyProduct(
        id = "glow_007",
        title = "No. 3 Hair Perfector Repairing Treatment",
        brand = "Olaplex",
        category = ProductCategory.HAIRCARE,
        originalPrice = 30.0,
        glowPrice = 18.0,
        condition = ConditionRating.LIGHTLY_LOVED_90,
        batchCode = "OLA771",
        sanitizedMethod = "Bottle exterior & cap sanitized with UV-C",
        fillPercentage = 92,
        sellerName = "ManeAesthetic",
        sellerRating = 4.9,
        sellerSalesCount = 53,
        isVerifiedSeller = true,
        isOpenToSwap = true,
        swapWishlist = "Will trade for K18 Leave-In Hair Mask",
        description = "Patented bond-building pre-shampoo treatment. Gently used twice, remaining fill is well over 90% (marked on bottle side).",
        volumeMl = "100 ml / 3.3 oz",
        accentColorHex = 0xFFE0E1DD,
        tags = listOf("Bond Builder", "Salon Quality", "Clean Hair")
      ),
      BeautyProduct(
        id = "glow_008",
        title = "Santal 33 Eau de Parfum Flacon",
        brand = "Le Labo",
        category = ProductCategory.FRAGRANCE,
        originalPrice = 320.0,
        glowPrice = 175.0,
        condition = ConditionRating.LIGHTLY_LOVED_90,
        batchCode = "LL9842M",
        sanitizedMethod = "Vaporizer cap sterilized, store receipt authenticated",
        fillPercentage = 91,
        sellerName = "NicheAromas_SF",
        sellerRating = 5.0,
        sellerSalesCount = 215,
        isVerifiedSeller = true,
        isOpenToSwap = true,
        swapWishlist = "Seeking Tom Ford Tobacco Vanille or Byredo Mojave Ghost",
        description = "Cult smoky cardamom, iris, violet, and Australian sandalwood. Purchased at Le Labo boutique Nolita. Guaranteed authentic with batch stamp.",
        volumeMl = "100 ml / 3.4 oz",
        accentColorHex = 0xFFB08968,
        tags = listOf("Niche Fragrance", "Verified", "High Savings")
      )
    )

    val initialSwapOffers = listOf(
      SwapOffer(
        id = "SWAP-8910AB",
        targetProductId = "glow_002",
        targetProductTitle = "Replica Jazz Club Eau de Toilette",
        targetProductBrand = "Maison Margiela",
        offeredItemTitle = "Philosykos Eau de Parfum (75ml, 90%)",
        offeredItemBrand = "Diptyque",
        offeredItemCondition = ConditionRating.LIGHTLY_LOVED_90,
        cashAdjustment = 10.0,
        status = SwapOfferStatus.AUTHENTICATING,
        dateFormatted = "Yesterday"
      ),
      SwapOffer(
        id = "SWAP-4412CD",
        targetProductId = "glow_001",
        targetProductTitle = "The Rich Cream with TFC8®",
        targetProductBrand = "Augustinus Bader",
        offeredItemTitle = "Black Rose Precious Face Oil (BNIB)",
        offeredItemBrand = "Sisley Paris",
        offeredItemCondition = ConditionRating.BNIB,
        cashAdjustment = 0.0,
        status = SwapOfferStatus.PENDING,
        dateFormatted = "2 hours ago"
      )
    )
  }
}
