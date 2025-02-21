package com.coderjot.milktracker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coderjot.milktracker.ui.screens.CalendarScreen
import com.coderjot.milktracker.ui.screens.SettingsScreen
import com.coderjot.milktracker.ui.theme.MilkTrackerTheme
import com.google.androidgamesdk.gametextinput.Settings
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

// DataStore setup
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val QUANTITY_KEY = intPreferencesKey("daily_quantity")
val PRICE_KEY = intPreferencesKey("price_per_liter")
val MARKED_DATES_KEY = "marked_dates"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MilkTrackerTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Observe DataStore
    val settingsFlow = context.dataStore.data
    val settingsState by settingsFlow.collectAsStateWithLifecycle(initialValue = null)

    // State variables
    var dailyQuantity by remember { mutableStateOf(1) }
    var pricePerLiter by remember { mutableStateOf(50) }
    var markedDates by remember { mutableStateOf<Map<LocalDate, Boolean>>(emptyMap()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showSettings by remember { mutableStateOf(false) }

    // Load data from DataStore
    LaunchedEffect(settingsState) {
        settingsState?.let { preferences ->
            dailyQuantity = preferences[QUANTITY_KEY] ?: 1
            pricePerLiter = preferences[PRICE_KEY] ?: 50
            val loadedMarkedDatesString = preferences[stringPreferencesKey(MARKED_DATES_KEY)] ?: ""
            markedDates = parseMarkedDates(loadedMarkedDatesString)
        }
    }

    // Handlers for changes
    val onQuantityChange: (Int) -> Unit = { newQuantity ->
        dailyQuantity = newQuantity
        coroutineScope.launch { context.dataStore.edit { it[QUANTITY_KEY] = newQuantity } }
    }

    val onPriceChange: (Int) -> Unit = { newPrice ->
        pricePerLiter = newPrice
        coroutineScope.launch { context.dataStore.edit { it[PRICE_KEY] = newPrice } }
    }

    val onDateClick: (LocalDate) -> Unit = { date ->
        val newMarkedDates = markedDates.toMutableMap().apply { this[date] = !(this[date] ?: false) }
        markedDates = newMarkedDates
        coroutineScope.launch {
            context.dataStore.edit {
                it[stringPreferencesKey(MARKED_DATES_KEY)] = serializeMarkedDates(newMarkedDates)
            }
        }
    }

    // Calculate delivered days and monthly total cost
    val deliveredDaysInCurrentMonth = markedDates.filter { (date, delivered) ->
        delivered && YearMonth.from(date) == currentMonth
    }.count()
    val totalCostCurrentMonth = calculateMonthlyTotalCost(markedDates, currentMonth, dailyQuantity, pricePerLiter)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (showSettings) "Settings" else "Milk Tracker") },
                navigationIcon = {
                    if (showSettings) {
                        IconButton(onClick = { showSettings = false }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (!showSettings) {
                        IconButton(onClick = { showSettings = true }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        // Instead of using fillMaxSize(), we rely on default sizing.
        Column(modifier = Modifier.padding(innerPadding)) {
            if (showSettings) {
                SettingsScreen(
                    dailyQuantity = dailyQuantity,
                    pricePerLiter = pricePerLiter,
                    onQuantityChange = onQuantityChange,
                    onPriceChange = onPriceChange,
                    onBack = { showSettings = false }
                )
            } else {
                CalendarScreen(
                    currentMonth = currentMonth,
                    onMonthChanged = { currentMonth = it },
                    markedDates = markedDates,
                    onDateClick = onDateClick
                )
                // Display delivered days and monthly total cost below the calendar
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Delivered Days: $deliveredDaysInCurrentMonth")
                    Text(text = "Total Cost: ₹$totalCostCurrentMonth")
                }
            }
        }
    }
}

// Helper functions

fun calculateMonthlyTotalCost(
    markedDates: Map<LocalDate, Boolean>,
    currentMonth: YearMonth,
    quantity: Int,
    price: Int
): Int {
    val deliveredDays = markedDates.filter { (date, delivered) ->
        delivered && YearMonth.from(date) == currentMonth
    }.count()
    return deliveredDays * quantity * price
}

fun serializeMarkedDates(markedDates: Map<LocalDate, Boolean>): String {
    return markedDates.map { (date, isDelivered) ->
        "${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}:$isDelivered"
    }.joinToString(",")
}

fun parseMarkedDates(data: String): Map<LocalDate, Boolean> {
    if (data.isEmpty()) {
        return emptyMap()
    }
    return data.split(",").associate { entry ->
        val parts = entry.split(":")
        LocalDate.parse(parts[0], DateTimeFormatter.ISO_LOCAL_DATE) to parts[1].toBoolean()
    }
}
