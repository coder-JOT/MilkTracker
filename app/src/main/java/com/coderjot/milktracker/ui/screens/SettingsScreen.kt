package com.coderjot.milktracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun SettingsScreen(
    dailyQuantity: Int,
    pricePerLiter: Int,
    onQuantityChange: (Int) -> Unit,
    onPriceChange: (Int) -> Unit,
    onBack: () -> Unit
) {
    var quantityText by remember { mutableStateOf(dailyQuantity.toString()) }
    var priceText by remember { mutableStateOf(pricePerLiter.toString()) }

    LaunchedEffect(dailyQuantity, pricePerLiter) {
        quantityText = dailyQuantity.toString()
        priceText = pricePerLiter.toString()
    }

    Column(
        modifier = Modifier
            .padding(16.dp), // Removed fillMaxSize
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        TextField(
            value = quantityText,
            onValueChange = { newValue ->
                quantityText = newValue
                newValue.toIntOrNull()?.let { onQuantityChange(it) }
            },
            label = { Text("Daily Quantity (Liters)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth() // If fillMaxWidth() is problematic, you can remove it
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = priceText,
            onValueChange = { newValue ->
                priceText = newValue
                newValue.toIntOrNull()?.let { onPriceChange(it) }
            },
            label = { Text("Price Per Liter") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth() // Remove if causing issues
        )
    }
}
