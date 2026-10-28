package com.example

import com.example.data.model.ConditionRating
import com.example.data.model.ProductCategory
import com.example.data.model.SafetyStatus
import com.example.data.repository.MarketplaceRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  private val repository = MarketplaceRepository()

  @Test
  fun testBatchCodeCheckerValid() {
    val result = repository.checkBatchCode("Augustinus Bader", "24AB801")
    assertTrue(result.isValid)
    assertEquals("Augustinus Bader", result.brand)
    assertEquals("24AB801", result.batchCode)
    assertNotNull(result.mfgDateFormatted)
    assertTrue(result.ageInMonths >= 0)
    assertTrue(result.safetyStatus in listOf(SafetyStatus.FRESH, SafetyStatus.GOOD, SafetyStatus.APPROACHING_EXPIRY))
  }

  @Test
  fun testBatchCodeCheckerShortCode() {
    val result = repository.checkBatchCode("Dior", "1")
    assertFalse(result.isValid)
    assertEquals(SafetyStatus.INVALID_CODE, result.safetyStatus)
  }

  @Test
  fun testCartOperationsAndPromoCode() {
    val product = MarketplaceRepository.initialProducts.first()
    repository.clearCart()
    assertEquals(0, repository.cartItems.value.size)

    repository.addToCart(product)
    assertEquals(1, repository.cartItems.value.size)
    assertEquals(1, repository.cartItems.value.first().quantity)

    repository.updateQuantity(product.id, 1)
    assertEquals(2, repository.cartItems.value.first().quantity)

    val validPromo = repository.applyPromo("GLOW20")
    assertTrue(validPromo)
    assertEquals("GLOW20", repository.appliedPromoCode.value)

    val invalidPromo = repository.applyPromo("INVALID_CODE")
    assertFalse(invalidPromo)

    repository.clearCart()
    assertEquals(0, repository.cartItems.value.size)
  }

  @Test
  fun testSwapOfferSubmission() {
    val targetProduct = MarketplaceRepository.initialProducts[1]
    val initialCount = repository.swapOffers.value.size

    repository.submitSwapOffer(
      targetProduct = targetProduct,
      offeredTitle = "Mojave Ghost 50ml",
      offeredBrand = "Byredo",
      offeredCondition = ConditionRating.BNIB,
      cashTopUp = 15.0
    )

    assertEquals(initialCount + 1, repository.swapOffers.value.size)
    val latest = repository.swapOffers.value.first()
    assertEquals("Mojave Ghost 50ml", latest.offeredItemTitle)
    assertEquals(15.0, latest.cashAdjustment, 0.001)
  }
}
