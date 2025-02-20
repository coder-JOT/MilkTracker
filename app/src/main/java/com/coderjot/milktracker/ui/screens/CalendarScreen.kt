package com.coderjot.milktracker.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(
    initialMonth: YearMonth = YearMonth.now(),
    markedDates: Map<LocalDate, Boolean>, // true for delivered, false for not
    onDateClick: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(initialMonth) }

    Column(modifier = Modifier.fillMaxSize()) {
        MonthSelector(currentMonth = currentMonth, onMonthChanged = { currentMonth = it })
        CalendarGrid(currentMonth = currentMonth, markedDates = markedDates, onDateClick = onDateClick)
    }
}

@Composable
fun MonthSelector(currentMonth: YearMonth, onMonthChanged: (YearMonth) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM ಅವರನ್ನು")),
            // Add style here later
        )
        Row {
            androidx.compose.material3.IconButton(onClick = { onMonthChanged(currentMonth.minusMonths(1)) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            androidx.compose.material3.IconButton(onClick = { onMonthChanged(currentMonth.plusMonths(1)) }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    markedDates: Map<LocalDate, Boolean>,
    onDateClick: (LocalDate) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Adjust to start week on Monday (1)

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun") //Or start with Sun

    Column {
        // Display days of the week headers
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(daysOfWeek) { day ->
                Text(
                    text = day,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                    // Add style here later
                )
            }
        }

        // Display calendar days
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
        )
        {

            // Add empty cells for days before the first day of the month
            items(firstDayOfWeek) {
                Box(Modifier.size(40.dp)) {} // Empty cells
            }
            // Add days of month
            items((1..daysInMonth).toList()) { day ->
                val date = currentMonth.atDay(day)
                val isDelivered = markedDates[date] ?: false // Check if the date is marked

                CalendarDay(
                    day = day,
                    isDelivered = isDelivered,
                    onDateClick = { onDateClick(date) }
                )
            }
        }
    }
}

@Composable
fun CalendarDay(day: Int, isDelivered: Boolean, onDateClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp)
            .clip(CircleShape)
            .background(if (isDelivered) Color.Green else Color.Transparent) // Mark delivered days
            .clickable { onDateClick() }, // Handle date clicks
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.toString())
        if (isDelivered) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Delivered",
                tint = Color.White, // White checkmark for contrast
                modifier = Modifier.size(20.dp)
            )
        }
    }
}