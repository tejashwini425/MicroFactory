package com.vtu.mmproject.ui.navigation

sealed class Screen(val route: String) {
    object RoleSelection : Screen("role_selection")
    object SellerDashboard : Screen("seller_dashboard")
    object BuyerExplore : Screen("buyer_explore")
    object BusinessDetails : Screen("business_details/{businessId}") {
        fun createRoute(businessId: String) = "business_details/$businessId"
    }
}
