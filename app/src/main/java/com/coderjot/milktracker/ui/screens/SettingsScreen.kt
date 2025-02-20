package com.coderjot.milktracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SettingsScreen(
    dailyQuantity: Int,
    pricePerLiter: Int,
    onQuantityChange: (Int) -> Unit,
    onPriceChange: (Int) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Text("Settings")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = dailyQuantity.toString(),
            onValueChange = { newValue ->
                //Try catch for number format exception
                try{
                    onQuantityChange(newValue.toInt())
                }catch (e: Exception){
                    //
                }
            },
            label = { Text("Daily Quantity (Liters)") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = pricePerLiter.toString(),
            onValueChange = { newValue ->
                //Try catch for number format exception
                try{
                    onPriceChange(newValue.toInt())
                }catch (e: Exception){
                    //
                }
            },
            label = { Text("Price Per Liter") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            )
        )
    }
}