package com.vtu.mmproject.data

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.vtu.mmproject.model.BusinessProfile
import com.vtu.mmproject.model.Product
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRepository {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val TAG = "FirebaseRepository"

    suspend fun uploadImage(uri: Uri, folder: String): String? {
        return try {
            val fileName = UUID.randomUUID().toString()
            val ref = storage.reference.child("$folder/$fileName")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e(TAG, "Image upload failed", e)
            null
        }
    }

    suspend fun getBusinessProfile(businessId: String): BusinessProfile? {
        return try {
            val doc = db.collection("businesses").document(businessId).get().await()
            if (doc.exists()) {
                mapDocumentToProfile(doc)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllBusinesses(): List<BusinessProfile> {
        return try {
            val snapshot = db.collection("businesses").get().await()
            snapshot.documents.map { mapDocumentToProfile(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun mapDocumentToProfile(doc: com.google.firebase.firestore.DocumentSnapshot): BusinessProfile {
        return BusinessProfile(
            id = doc.id,
            industryName = doc.getString("industryName") ?: doc.getString("name") ?: "",
            ownerName = doc.getString("ownerName") ?: "",
            name = doc.getString("name") ?: "",
            description = doc.getString("description") ?: "",
            skillArea = doc.getString("skillArea") ?: "",
            location = doc.getString("location") ?: "",
            photoUrl = doc.getString("photoUrl") ?: "",
            weeklyCapacity = doc.getLong("weeklyCapacity")?.toInt()
                ?: doc.getLong("dailyCapacity")?.toInt()
                ?: 0,
            dailyCapacity = doc.getLong("dailyCapacity")?.toInt() ?: 0,
            capacityUnit = doc.getString("capacityUnit") ?: "units",
            isReadyForOrders = doc.getBoolean("isReadyForOrders") ?: false,
            phoneNumber = doc.getString("phoneNumber") ?: "",
            category = doc.getString("category") ?: "Craft"
        )
    }

    suspend fun saveBusinessProfile(profile: BusinessProfile): Boolean {
        return try {
            db.collection("businesses").document(profile.id)
                .set(profile, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun saveProduct(product: Product): Boolean {
        return try {
            val id = product.id.ifEmpty { UUID.randomUUID().toString() }
            db.collection("products").document(id)
                .set(product.copy(id = id), SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteProduct(productId: String): Boolean {
        return try {
            db.collection("products").document(productId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getProducts(businessId: String): List<Product> {
        return try {
            val snapshot = db.collection("products")
                .whereEqualTo("businessId", businessId)
                .get().await()
            snapshot.documents.mapNotNull { doc ->
                Product(
                    id = doc.id,
                    businessId = doc.getString("businessId") ?: "",
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    imageUrl = doc.getString("imageUrl") ?: "",
                    wholesalePrice = doc.getDouble("wholesalePrice") ?: 0.0,
                    category = doc.getString("category") ?: "",
                    minOrderQuantity = doc.getLong("minOrderQuantity")?.toInt() ?: 1,
                    dailyCapacity = doc.getLong("dailyCapacity")?.toInt() ?: 0,
                    capacityUnit = doc.getString("capacityUnit") ?: "units"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateCapacity(businessId: String, newCapacity: Int, isReady: Boolean): Boolean {
        return try {
            val data = mapOf(
                "weeklyCapacity" to newCapacity,
                "dailyCapacity" to newCapacity,
                "isReadyForOrders" to isReady
            )
            db.collection("businesses").document(businessId).set(data, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateCapacity(
        businessId: String,
        newCapacity: Int,
        capacityUnit: String,
        isReady: Boolean
    ): Boolean {
        return try {
            val data = mapOf(
                "weeklyCapacity" to newCapacity,
                "dailyCapacity" to newCapacity,
                "capacityUnit" to capacityUnit,
                "isReadyForOrders" to isReady
            )
            db.collection("businesses").document(businessId).set(data, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
