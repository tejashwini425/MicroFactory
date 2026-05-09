package com.vtu.mmproject.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vtu.mmproject.model.BusinessProfile
import com.vtu.mmproject.model.Product
import com.vtu.mmproject.ui.components.ProductCard
import com.vtu.mmproject.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.sellerProfile.collectAsState()
    val products by viewModel.sellerProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seller Micro-Factory") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Roles") }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddProductDialog = true },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (isLoading && profile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val businessProfile = profile ?: return@Scaffold
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            item { BusinessProfileHeader(profile = businessProfile) }
            item { SellerSummaryCard(products = products) }
            item {
                CapacityMeter(
                    profile = businessProfile,
                    onUpdateCapacity = viewModel::updateCapacity
                )
            }
            item { SellerTrustCard(products = products) }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Product Catalog",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${products.size} products listed for buyers",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (products.isEmpty()) {
                item {
                    EmptyCatalogCard()
                }
            } else {
                items(products.chunked(2)) { rowProducts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowProducts.forEach { product ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = product,
                                    onDelete = { viewModel.deleteProduct(product) }
                                )
                            }
                        }
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        if (showEditDialog) {
            EditProfileDialog(
                profile = businessProfile,
                onDismiss = { showEditDialog = false },
                onSave = { updatedProfile, imageUri ->
                    viewModel.saveProfile(updatedProfile, imageUri)
                    showEditDialog = false
                }
            )
        }

        if (showAddProductDialog) {
            AddProductDialog(
                businessId = businessProfile.id,
                onDismiss = { showAddProductDialog = false },
                onSave = { newProduct, imageUri ->
                    viewModel.addProduct(newProduct, imageUri)
                    showAddProductDialog = false
                }
            )
        }
    }
}

@Composable
private fun EmptyCatalogCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No products yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap the add button to upload product photos, wholesale price, minimum order quantity, and daily capacity.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SellerTrustCard(products: List<Product>) {
    val readyProducts = products.count { it.dailyCapacity > 0 && it.wholesalePrice > 0.0 }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Showcase Strength",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$readyProducts products are buyer-ready with capacity and bulk price.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SellerSummaryCard(products: List<Product>) {
    val totalCapacity = products.sumOf { it.dailyCapacity }
    val categories = products.map { it.category }.filter { it.isNotBlank() }.distinct().size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryMetric("Products", products.size.toString(), Modifier.weight(1f))
            SummaryMetric("Categories", categories.toString(), Modifier.weight(1f))
            SummaryMetric("Daily Units", totalCapacity.toString(), Modifier.weight(1f))
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EditProfileDialog(
    profile: BusinessProfile,
    onDismiss: () -> Unit,
    onSave: (BusinessProfile, Uri?) -> Unit
) {
    var industryName by remember { mutableStateOf(profile.displayName) }
    var ownerName by remember { mutableStateOf(profile.ownerName) }
    var skillArea by remember { mutableStateOf(profile.skillArea) }
    var category by remember { mutableStateOf(profile.category) }
    var location by remember { mutableStateOf(profile.location) }
    var phoneNumber by remember { mutableStateOf(profile.phoneNumber) }
    var description by remember { mutableStateOf(profile.description) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        selectedImageUri = uri
        uri?.let {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Micro-Factory Profile") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AsyncImage(
                    model = selectedImageUri ?: profile.photoUrl.ifBlank { null },
                    contentDescription = "Industry photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = if (selectedImageUri == null && profile.photoUrl.isBlank()) {
                        "No image selected yet"
                    } else {
                        "Image selected for this profile"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(onClick = { imagePicker.launch(arrayOf("image/*")) }) {
                    Text("Choose family/team or workshop photo")
                }
                OutlinedTextField(
                    value = industryName,
                    onValueChange = { industryName = it },
                    label = { Text("Industry / Business Name") },
                    placeholder = { Text("e.g. Lakshmi Papad Works") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("Owner / Team Name") },
                    placeholder = { Text("e.g. Kavitha SHG Team") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = skillArea,
                    onValueChange = { skillArea = it },
                    label = { Text("Skill Area") },
                    placeholder = { Text("Basket weaving, agarbatti rolling") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Product Category") },
                    placeholder = { Text("Food, Craft, Textile, Agarbatti") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Seller Location") },
                    placeholder = { Text("Village/Town, District, State") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Indian Mobile Number") },
                    placeholder = { Text("10 digits or +91 number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("About the Manufacturing Setup") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    profile.copy(
                        industryName = industryName.ifBlank { "My Micro-Factory" },
                        name = industryName.ifBlank { "My Micro-Factory" },
                        ownerName = ownerName,
                        skillArea = skillArea.ifBlank { "Cottage Industry" },
                        category = category.ifBlank { "Craft" },
                        location = location,
                        phoneNumber = phoneNumber,
                        description = description
                    ),
                    selectedImageUri
                )
            }) {
                Text("Save Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddProductDialog(
    businessId: String,
    onDismiss: () -> Unit,
    onSave: (Product, Uri?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var minQtyText by remember { mutableStateOf("100") }
    var dailyCapacityText by remember { mutableStateOf("") }
    var capacityUnit by remember { mutableStateOf("units") }
    var category by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        selectedImageUri = uri
        uri?.let {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Product") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Product photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = if (selectedImageUri == null) "No product image selected yet" else "Product image selected",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(onClick = { imagePicker.launch(arrayOf("image/*")) }) {
                    Text("Choose product photo")
                }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Wholesale Price per Unit") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = minQtyText,
                    onValueChange = { minQtyText = it },
                    label = { Text("Minimum Bulk Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dailyCapacityText,
                    onValueChange = { dailyCapacityText = it },
                    label = { Text("Daily Production Capacity") },
                    placeholder = { Text("e.g. 500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = capacityUnit,
                    onValueChange = { capacityUnit = it },
                    label = { Text("Capacity Unit") },
                    placeholder = { Text("baskets, packets, kg, pieces") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    Product(
                        businessId = businessId,
                        name = name.ifBlank { "New Product" },
                        description = description,
                        category = category.ifBlank { "Craft" },
                        wholesalePrice = priceText.toDoubleOrNull() ?: 0.0,
                        minOrderQuantity = minQtyText.toIntOrNull() ?: 1,
                        dailyCapacity = dailyCapacityText.toIntOrNull() ?: 0,
                        capacityUnit = capacityUnit.ifBlank { "units" }
                    ),
                    selectedImageUri
                )
            }) {
                Text("Add to Catalog")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun BusinessProfileHeader(profile: BusinessProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AsyncImage(
                model = profile.photoUrl.ifBlank { null },
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                if (profile.ownerName.isNotBlank()) {
                    Text(
                        text = profile.ownerName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${profile.skillArea.ifBlank { "Cottage Industry" }} - ${profile.category}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (profile.location.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = profile.location,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                if (profile.phoneNumber.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Contact: ${profile.phoneNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (profile.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = profile.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun CapacityMeter(
    profile: BusinessProfile,
    onUpdateCapacity: (Int, String, Boolean) -> Unit
) {
    var capacity by remember(profile.displayCapacity) { mutableStateOf(profile.displayCapacity.toString()) }
    var unit by remember(profile.capacityUnit) { mutableStateOf(profile.capacityUnit) }
    var isReady by remember(profile.isReadyForOrders) { mutableStateOf(profile.isReadyForOrders) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Capacity Meter", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ready to take bulk orders", modifier = Modifier.weight(1f))
                Switch(checked = isReady, onCheckedChange = { isReady = it })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("This week") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit") },
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = if (isReady) {
                    "Ready for ${capacity.toIntOrNull() ?: 0} ${unit.ifBlank { "units" }} this week"
                } else {
                    "Not taking orders now"
                },
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Button(
                onClick = { onUpdateCapacity(capacity.toIntOrNull() ?: 0, unit, isReady) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Capacity")
            }
        }
    }
}
