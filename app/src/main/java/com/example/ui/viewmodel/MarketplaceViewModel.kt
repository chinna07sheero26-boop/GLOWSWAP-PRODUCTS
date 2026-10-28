package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BatchCheckResult
import com.example.data.model.BeautyProduct
import com.example.data.model.CartItem
import com.example.data.model.ConditionRating
import com.example.data.model.ProductCategory
import com.example.data.model.SwapOffer
import com.example.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed class AppScreen {
  data object Home : AppScreen()
  data object WebStorefront : AppScreen() // The full website experience
  data class ProductDetail(val product: BeautyProduct) : AppScreen()
  data object BatchChecker : AppScreen()
  data object SwapHub : AppScreen()
  data object SellWizard : AppScreen()
  data object CartCheckout : AppScreen()
}

data class OrderConfirmation(
  val orderId: String,
  val itemsCount: Int,
  val finalTotal: Double,
  val trackingNumber: String,
  val estimatedDelivery: String = "2-4 Business Days via Insured Courier",
  val timestamp: String = "Just now"
)

class MarketplaceViewModel(
  private val repository: MarketplaceRepository = MarketplaceRepository()
) : ViewModel() {

  private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _navigationStack = MutableStateFlow<List<AppScreen>>(listOf(AppScreen.Home))

  data class FilterCriteria(
    val query: String = "",
    val category: ProductCategory = ProductCategory.ALL,
    val condition: ConditionRating? = null,
    val swapOnly: Boolean = false,
    val overstockOnly: Boolean = false
  )

  private val _criteria = MutableStateFlow(FilterCriteria())

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategory = MutableStateFlow(ProductCategory.ALL)
  val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

  private val _selectedCondition = MutableStateFlow<ConditionRating?>(null)
  val selectedCondition: StateFlow<ConditionRating?> = _selectedCondition.asStateFlow()

  private val _swapOnlyFilter = MutableStateFlow(false)
  val swapOnlyFilter: StateFlow<Boolean> = _swapOnlyFilter.asStateFlow()

  private val _overstockOnlyFilter = MutableStateFlow(false)
  val overstockOnlyFilter: StateFlow<Boolean> = _overstockOnlyFilter.asStateFlow()

  val cartItems: StateFlow<List<CartItem>> = repository.cartItems
  val swapOffers: StateFlow<List<SwapOffer>> = repository.swapOffers
  val appliedPromo: StateFlow<String?> = repository.appliedPromoCode

  private val _batchCheckResult = MutableStateFlow<BatchCheckResult?>(null)
  val batchCheckResult: StateFlow<BatchCheckResult?> = _batchCheckResult.asStateFlow()

  private val _orderConfirmation = MutableStateFlow<OrderConfirmation?>(null)
  val orderConfirmation: StateFlow<OrderConfirmation?> = _orderConfirmation.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  // Filtered products list using 2-flow combine
  val filteredProducts: StateFlow<List<BeautyProduct>> = combine(
    repository.products,
    _criteria
  ) { list, criteria ->
    list.filter { product ->
      val matchesQuery = criteria.query.isBlank() ||
          product.title.contains(criteria.query, ignoreCase = true) ||
          product.brand.contains(criteria.query, ignoreCase = true) ||
          product.tags.any { it.contains(criteria.query, ignoreCase = true) }

      val matchesCategory = (criteria.category == ProductCategory.ALL) || (product.category == criteria.category)
      val matchesCondition = (criteria.condition == null) || (product.condition == criteria.condition)
      val matchesSwap = !criteria.swapOnly || product.isOpenToSwap
      val matchesOverstock = !criteria.overstockOnly || product.isOverstockSale

      matchesQuery && matchesCategory && matchesCondition && matchesSwap && matchesOverstock
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.products.value)

  fun navigateTo(screen: AppScreen) {
    _navigationStack.value = _navigationStack.value + screen
    _currentScreen.value = screen
  }

  fun navigateBack(): Boolean {
    val stack = _navigationStack.value
    if (stack.size > 1) {
      val newStack = stack.dropLast(1)
      _navigationStack.value = newStack
      _currentScreen.value = newStack.last()
      return true
    }
    return false
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
    _criteria.value = _criteria.value.copy(query = query)
  }

  fun selectCategory(category: ProductCategory) {
    _selectedCategory.value = category
    _criteria.value = _criteria.value.copy(category = category)
  }

  fun setConditionFilter(condition: ConditionRating?) {
    _selectedCondition.value = condition
    _criteria.value = _criteria.value.copy(condition = condition)
  }

  fun toggleSwapFilter() {
    val newVal = !_swapOnlyFilter.value
    _swapOnlyFilter.value = newVal
    _criteria.value = _criteria.value.copy(swapOnly = newVal)
  }

  fun toggleOverstockFilter() {
    val newVal = !_overstockOnlyFilter.value
    _overstockOnlyFilter.value = newVal
    _criteria.value = _criteria.value.copy(overstockOnly = newVal)
  }

  fun addToCart(product: BeautyProduct) {
    repository.addToCart(product)
    showSnackbar("Added \"${product.title}\" to Bag")
  }

  fun removeFromCart(productId: String) {
    repository.removeFromCart(productId)
  }

  fun updateCartQuantity(productId: String, delta: Int) {
    repository.updateQuantity(productId, delta)
  }

  fun applyPromoCode(code: String) {
    val success = repository.applyPromo(code)
    if (success) {
      showSnackbar("Promo code '$code' applied successfully!")
    } else {
      showSnackbar("Invalid code. Try 'GLOW20' or 'ECOFREE'")
    }
  }

  fun removePromoCode() {
    repository.removePromo()
  }

  fun checkBatch(brand: String, code: String) {
    _batchCheckResult.value = repository.checkBatchCode(brand, code)
  }

  fun submitSwap(
    product: BeautyProduct,
    offeredTitle: String,
    offeredBrand: String,
    offeredCondition: ConditionRating,
    cashTopUp: Double
  ) {
    repository.submitSwapOffer(product, offeredTitle, offeredBrand, offeredCondition, cashTopUp)
    showSnackbar("Swap proposal submitted for ${product.title}!")
    navigateTo(AppScreen.SwapHub)
  }

  fun createListing(
    title: String,
    brand: String,
    category: ProductCategory,
    originalPrice: Double,
    glowPrice: Double,
    condition: ConditionRating,
    batchCode: String,
    sanitizedMethod: String,
    fillPercentage: Int,
    isOpenToSwap: Boolean,
    swapWishlist: String,
    description: String,
    volumeMl: String
  ) {
    val newProduct = BeautyProduct(
      id = "glow_user_${UUID.randomUUID().toString().take(6)}",
      title = title,
      brand = brand,
      category = category,
      originalPrice = originalPrice,
      glowPrice = glowPrice,
      condition = condition,
      batchCode = batchCode.ifBlank { "VERIFIED_USER" },
      sanitizedMethod = sanitizedMethod.ifBlank { "Standard 70% alcohol and clean cosmetic tools" },
      fillPercentage = fillPercentage,
      sellerName = "You (Verified)",
      sellerRating = 5.0,
      sellerSalesCount = 1,
      isVerifiedSeller = true,
      isOpenToSwap = isOpenToSwap,
      swapWishlist = swapWishlist,
      description = description,
      volumeMl = volumeMl.ifBlank { "Full Size" },
      tags = listOf("New Listing", condition.shortBadge)
    )
    repository.addProduct(newProduct)
    showSnackbar("✨ Listing published! Live in marketplace.")
    navigateTo(AppScreen.Home)
  }

  fun checkout(withShippingProtection: Boolean) {
    val items = cartItems.value
    if (items.isEmpty()) return

    val subtotal = items.sumOf { it.product.glowPrice * it.quantity }
    val discount = if (appliedPromo.value == "GLOW20") subtotal * 0.20 else 0.0
    val protection = if (withShippingProtection) 1.99 else 0.0
    val shipping = if (subtotal - discount > 45.0 || appliedPromo.value == "ECOFREE") 0.0 else 4.95
    val total = subtotal - discount + protection + shipping

    val confirmation = OrderConfirmation(
      orderId = "GLOW-ORDER-${(100000..999999).random()}",
      itemsCount = items.sumOf { it.quantity },
      finalTotal = total,
      trackingNumber = "USPS-GLOW-${(1000000..9999999).random()}"
    )

    _orderConfirmation.value = confirmation
    repository.clearCart()
    showSnackbar("Order confirmed! GlowCheck™ certification generated.")
  }

  fun clearOrderConfirmation() {
    _orderConfirmation.value = null
  }

  fun showSnackbar(message: String) {
    _snackbarMessage.value = message
  }

  fun clearSnackbar() {
    _snackbarMessage.value = null
  }
}
