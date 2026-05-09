package com.vtu.mmproject

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vtu.mmproject.ui.navigation.Screen
import com.vtu.mmproject.ui.screens.BusinessDetailsScreen
import com.vtu.mmproject.ui.screens.BuyerExploreScreen
import com.vtu.mmproject.ui.screens.ProfileScreen
import com.vtu.mmproject.ui.screens.RoleSelectionScreen
import com.vtu.mmproject.ui.theme.MmProjectTheme
import com.vtu.mmproject.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toastMessage.collect { message ->
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        setContent {
            MmProjectTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.RoleSelection.route
                ) {
                    composable(Screen.RoleSelection.route) {
                        RoleSelectionScreen(
                            onSellerSelected = { navController.navigate(Screen.SellerDashboard.route) },
                            onBuyerSelected = {
                                viewModel.loadBuyerData()
                                navController.navigate(Screen.BuyerExplore.route)
                            }
                        )
                    }
                    composable(Screen.SellerDashboard.route) {
                        ProfileScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.BuyerExplore.route) {
                        BuyerExploreScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onBusinessSelected = { businessId ->
                                viewModel.selectBusiness(businessId)
                                navController.navigate(Screen.BusinessDetails.createRoute(businessId))
                            }
                        )
                    }
                    composable(Screen.BusinessDetails.route) {
                        BusinessDetailsScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
