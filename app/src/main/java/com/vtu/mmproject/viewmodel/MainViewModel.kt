package com.vtu.mmproject.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtu.mmproject.data.FirebaseRepository
import com.vtu.mmproject.model.BusinessProfile
import com.vtu.mmproject.model.Product
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _sellerProfile = MutableStateFlow<BusinessProfile?>(null)
    val sellerProfile: StateFlow<BusinessProfile?> = _sellerProfile.asStateFlow()

    private val _sellerProducts = MutableStateFlow<List<Product>>(emptyList())
    val sellerProducts: StateFlow<List<Product>> = _sellerProducts.asStateFlow()

    private val _allBusinesses = MutableStateFlow<List<BusinessProfile>>(emptyList())
    val allBusinesses: StateFlow<List<BusinessProfile>> = _allBusinesses.asStateFlow()

    private val _selectedBusiness = MutableStateFlow<BusinessProfile?>(null)
    val selectedBusiness: StateFlow<BusinessProfile?> = _selectedBusiness.asStateFlow()

    private val _selectedBusinessProducts = MutableStateFlow<List<Product>>(emptyList())
    val selectedBusinessProducts: StateFlow<List<Product>> = _selectedBusinessProducts.asStateFlow()

    private val _buyerProductFilter = MutableStateFlow("All")
    val buyerProductFilter: StateFlow<String> = _buyerProductFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    private val sellerId = "my_micro_factory_58"

    init {
        loadSellerData()
        loadBuyerData()
    }

    private fun loadSellerData() {
        viewModelScope.launch {
            val profile = repository.getBusinessProfile(sellerId)
            if (profile == null) {
                _sellerProfile.value = BusinessProfile(
                    id = sellerId,
                    industryName = "Kutira-Kushala",
                    name = "Kutira-Kushala",
                    skillArea = "Papad Making",
                    category = "Food",
                    location = "",
                    description = "",
                    capacityUnit = "pieces"
                )
            } else {
                _sellerProfile.value = profile
                _sellerProducts.value = repository.getProducts(sellerId)
            }
        }
    }

    fun loadBuyerData() {
        viewModelScope.launch {
            _isLoading.value = true
            refreshBuyerDirectory()
            _isLoading.value = false
        }
    }

    fun selectBusiness(businessId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val profile = if (businessId == sellerId) {
                _sellerProfile.value ?: repository.getBusinessProfile(businessId)
            } else {
                repository.getBusinessProfile(businessId)
            }
            _selectedBusiness.value = profile
            _selectedBusinessProducts.value = if (profile == null) {
                emptyList()
            } else if (businessId == sellerId && _sellerProducts.value.isNotEmpty()) {
                _sellerProducts.value
            } else {
                repository.getProducts(businessId)
            }
            _isLoading.value = false
        }
    }

    fun setBuyerProductFilter(category: String) {
        _buyerProductFilter.value = category
    }

    fun saveProfile(profile: BusinessProfile, imageUri: Uri? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            var updatedProfile = profile.copy(
                id = sellerId,
                industryName = profile.industryName.ifBlank { "Kutira-Kushala" },
                name = profile.industryName.ifBlank { profile.name }.ifBlank { "Kutira-Kushala" },
                phoneNumber = normalizeIndianPhone(profile.phoneNumber)
            )

            if (imageUri != null) {
                _toastMessage.emit("Uploading industry photo...")
                val url = repository.uploadImage(imageUri, "profiles")
                updatedProfile = if (url != null) {
                    updatedProfile.copy(photoUrl = url)
                } else {
                    _toastMessage.emit("Cloud upload failed, keeping selected local photo for demo.")
                    updatedProfile.copy(photoUrl = imageUri.toString())
                }
            }

            val savedToCloud = repository.saveBusinessProfile(updatedProfile)
            _sellerProfile.value = updatedProfile
            if (_selectedBusiness.value?.id == sellerId) {
                _selectedBusiness.value = updatedProfile
            }
            refreshBuyerDirectory()
            if (savedToCloud) {
                _toastMessage.emit("Profile saved!")
            } else {
                _toastMessage.emit("Profile saved in app for demo. Check Firebase connection later.")
            }
            _isLoading.value = false
        }
    }

    fun updateCapacity(newCapacity: Int, isReady: Boolean) {
        val current = _sellerProfile.value ?: return
        updateCapacity(newCapacity, current.capacityUnit, isReady)
    }

    fun updateCapacity(newCapacity: Int, capacityUnit: String, isReady: Boolean) {
        val current = _sellerProfile.value ?: return
        viewModelScope.launch {
            val cleanUnit = capacityUnit.ifBlank { "units" }
            if (repository.updateCapacity(current.id, newCapacity, cleanUnit, isReady)) {
                val updatedProfile = current.copy(
                    weeklyCapacity = newCapacity,
                    dailyCapacity = newCapacity,
                    capacityUnit = cleanUnit,
                    isReadyForOrders = isReady
                )
                _sellerProfile.value = updatedProfile
                if (_selectedBusiness.value?.id == sellerId) {
                    _selectedBusiness.value = updatedProfile
                }
                refreshBuyerDirectory()
                _toastMessage.emit("Capacity updated!")
            } else {
                _toastMessage.emit("Capacity update failed.")
            }
        }
    }

    fun addProduct(product: Product, imageUri: Uri? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            var productToSave = product.copy(businessId = sellerId)

            if (imageUri != null) {
                _toastMessage.emit("Uploading product photo...")
                val url = repository.uploadImage(imageUri, "products")
                productToSave = if (url != null) {
                    productToSave.copy(imageUrl = url)
                } else {
                    _toastMessage.emit("Cloud upload failed, keeping selected local product photo for demo.")
                    productToSave.copy(imageUrl = imageUri.toString())
                }
            }

            val savedToCloud = repository.saveProduct(productToSave)
            val refreshedProducts = if (savedToCloud) {
                repository.getProducts(sellerId)
            } else {
                _sellerProducts.value + productToSave.copy(
                    id = productToSave.id.ifBlank { System.currentTimeMillis().toString() }
                )
            }
            _sellerProducts.value = refreshedProducts
            if (_selectedBusiness.value?.id == sellerId) {
                _selectedBusinessProducts.value = refreshedProducts
            }
            refreshBuyerDirectory()
            if (savedToCloud) {
                _toastMessage.emit("Product added!")
            } else {
                _toastMessage.emit("Product added in app for demo. Check Firebase connection later.")
            }
            _isLoading.value = false
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            val remainingProducts = _sellerProducts.value.filterNot { it.id == product.id }
            _sellerProducts.value = remainingProducts
            if (_selectedBusiness.value?.id == sellerId) {
                _selectedBusinessProducts.value = remainingProducts
            }

            if (product.id.isNotBlank() && repository.deleteProduct(product.id)) {
                _toastMessage.emit("Product deleted.")
            } else {
                _toastMessage.emit("Product removed from this app session.")
            }
            refreshBuyerDirectory()
        }
    }

    private suspend fun refreshBuyerDirectory() {
        val businesses = repository.getAllBusinesses()
        _allBusinesses.value = businesses
        _sellerProfile.value?.let { seller ->
            _allBusinesses.value = businesses.filterNot { it.id == seller.id } + seller
        }
    }

    private fun normalizeIndianPhone(rawPhone: String): String {
        val digits = rawPhone.filter { it.isDigit() }
        return when {
            digits.length == 10 -> "+91$digits"
            digits.length == 12 && digits.startsWith("91") -> "+$digits"
            rawPhone.trim().startsWith("+") -> rawPhone.trim()
            else -> rawPhone.trim()
        }
    }
}
