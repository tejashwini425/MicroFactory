package com.vtu.mmproject.model

data class BusinessProfile(
    val id: String = "",
    val industryName: String = "",
    val ownerName: String = "",
    val name: String = "",
    val description: String = "",
    val skillArea: String = "",
    val location: String = "",
    val photoUrl: String = "",
    val weeklyCapacity: Int = 0,
    val dailyCapacity: Int = 0,
    val capacityUnit: String = "units",
    val isReadyForOrders: Boolean = false,
    val phoneNumber: String = "",
    val category: String = "Craft"
) {
    val displayName: String
        get() {
            val rawName = industryName.ifBlank { name }.trim()
            return when {
                rawName.isBlank() -> "Kutira-Kushala"
                rawName.equals("Set Industry Name", ignoreCase = true) -> "Kutira-Kushala"
                rawName.equals("Unnamed Micro-Factory", ignoreCase = true) -> "Kutira-Kushala"
                else -> rawName
            }
        }

    val displayCapacity: Int
        get() = if (weeklyCapacity > 0) weeklyCapacity else dailyCapacity
}
