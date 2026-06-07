package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.MealRecord
import com.example.data.database.Member
import com.example.data.database.PricingConfig
import com.example.ui.components.*
import com.example.ui.viewmodel.MealViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MealViewModel,
    onNavigateToSettings: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredMembers by viewModel.filteredMembers.collectAsState()
    val allMealRecords by viewModel.allMealRecords.collectAsState()
    val pricingConfig by viewModel.pricingConfig.collectAsState()
    val overrides by viewModel.overrides.collectAsState()

    var expandedMemberIds by remember { mutableStateOf(setOf<Int>()) }
    val currencyFormat = remember { DecimalFormat("0.00") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(CosmicBgStart)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Glassmorphic Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Search by member name...", color = GlassTextSecondary) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search icon",
                                tint = GlassTextSecondary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = GlassTextSecondary
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GlassTextPrimary,
                            unfocusedTextColor = GlassTextPrimary,
                            focusedBorderColor = NeonAccent,
                            unfocusedBorderColor = GlassBorderColor,
                            focusedContainerColor = Color(0x0CFFFFFF),
                            unfocusedContainerColor = Color(0x04FFFFFF)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Settings Gear Button
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x0EFFFFFF))
                            .border(BorderStroke(1.dp, GlassBorderColor), RoundedCornerShape(16.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Menu",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        containerColor = CosmicBgEnd,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Dashboard Header Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Borders List",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassTextPrimary
                    )
                    Text(
                        text = "Real-time ledger updates",
                        fontSize = 12.sp,
                        color = GlassTextSecondary
                    )
                }
                // Date Chip Badge
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0x11FFFFFF),
                    border = BorderStroke(1.dp, Color(0x14FFFFFF))
                ) {
                    Text(
                        text = viewModel.currentDateString,
                        fontSize = 12.sp,
                        color = NeonAccentSecondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            if (filteredMembers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = GlassSurface,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "No members",
                                    tint = GlassTextSecondary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No Border Found Match" else "No Members In Record",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Refine your search queries above." else "Proceed to settings to register members details.",
                            fontSize = 14.sp,
                            color = GlassTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredMembers, key = { it.id }) { member ->
                        val isExpanded = expandedMemberIds.contains(member.id)
                        val config = pricingConfig ?: PricingConfig(
                            isThreeRates = false, breakfastRate = 0.0, lunchRate = 0.0, dinnerRate = 0.0,
                            snacksEnabled = false, morningSnacksRate = 0.0, afternoonSnacksRate = 0.0, eveningSnacksRate = 0.0
                        )

                        val todayBill = viewModel.getTodayBillForMember(member.id, allMealRecords)
                        val totalBill = viewModel.getTotalBillForMember(member.id, allMealRecords)

                        // Member list card integration - Immersive UI Theme styling
                        val borderBrush = if (isExpanded) {
                            Brush.linearGradient(colors = listOf(Color(0x3D22D3EE), Color(0x3D6366F1)))
                        } else {
                            Brush.linearGradient(colors = listOf(Color(0x13FFFFFF), Color(0x13FFFFFF)))
                        }
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(if (isExpanded) RoundedCornerShape(32.dp) else RoundedCornerShape(28.dp))
                                .background(
                                    if (isExpanded) {
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0x1B6366F1), // Indigo-500 with subtle opacity
                                                Color(0x1B22D3EE)  // Cyan-400 with subtle opacity
                                            )
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0x0DFFFFFF), // bg-white/5
                                                Color(0x0DFFFFFF)
                                            )
                                        )
                                    }
                                )
                                .border(
                                    BorderStroke(
                                        if (isExpanded) 1.5.dp else 1.dp,
                                        borderBrush
                                    ),
                                    if (isExpanded) RoundedCornerShape(32.dp) else RoundedCornerShape(28.dp)
                                )
                        ) {
                            // Row containing Name, Today billing, Total billing and expansion arrow
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedMemberIds = if (isExpanded) {
                                            expandedMemberIds - member.id
                                        } else {
                                            expandedMemberIds + member.id
                                        }
                                    }
                                    .padding(horizontal = 20.dp, vertical = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Left Side: Member Name
                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text(
                                        text = member.name,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GlassTextPrimary
                                    )
                                    Text(
                                        text = if (isExpanded) "Active Premium" else "Room Details",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isExpanded) NeonAccent else GlassTextSecondary,
                                        letterSpacing = 1.sp
                                    )
                                }

                                // Middle-Right Side: Today's Bill
                                Column(
                                    modifier = Modifier.weight(0.9f),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "$${currencyFormat.format(todayBill)}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonAccent
                                    )
                                    Text(
                                        text = "Today",
                                        fontSize = 11.sp,
                                        color = GlassTextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Far-Right Side: Total Bill
                                Column(
                                    modifier = Modifier.weight(0.9f),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "$${currencyFormat.format(totalBill)}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isExpanded) Color.White else NeonAccentSecondary
                                    )
                                    Text(
                                        text = "Cumulative",
                                        fontSize = 11.sp,
                                        color = GlassTextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Downward icon "V" with animate direction
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand details",
                                    tint = GlassTextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Dynamic Dropdown Expansion Card
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                val currentRecord = allMealRecords.firstOrNull {
                                    it.memberId == member.id && it.dateString == viewModel.currentDateString
                                }

                                val isBreakfastChecked = currentRecord?.breakfast ?: false
                                val isLunchChecked = currentRecord?.lunch ?: false
                                val isDinnerChecked = currentRecord?.dinner ?: false
                                val isMorningSnackChecked = currentRecord?.morningSnacks ?: false
                                val isAfternoonSnackChecked = currentRecord?.afternoonSnacks ?: false
                                val isEveningSnackChecked = currentRecord?.eveningSnacks ?: false

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                                ) {
                                    Divider(color = Color(0x1AFFFFFF), thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

                                    Text(
                                        text = "MEAL SELECTION LOG",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GlassTextSecondary,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    // modern responsive toggle grids matching HTML layout
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            MealToggleGridItem(
                                                label = "Breakfast",
                                                isYes = isBreakfastChecked,
                                                isSnack = false,
                                                onClick = { viewModel.toggleMealDuty(member.id, "breakfast", !isBreakfastChecked) },
                                                modifier = Modifier.weight(1f)
                                            )
                                            MealToggleGridItem(
                                                label = "Lunch",
                                                isYes = isLunchChecked,
                                                isSnack = false,
                                                onClick = { viewModel.toggleMealDuty(member.id, "lunch", !isLunchChecked) },
                                                modifier = Modifier.weight(1f)
                                            )
                                            MealToggleGridItem(
                                                label = "Dinner",
                                                isYes = isDinnerChecked,
                                                isSnack = false,
                                                onClick = { viewModel.toggleMealDuty(member.id, "dinner", !isDinnerChecked) },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        if (config.snacksEnabled) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                MealToggleGridItem(
                                                    label = "Morning",
                                                    isYes = isMorningSnackChecked,
                                                    isSnack = true,
                                                    onClick = { viewModel.toggleMealDuty(member.id, "morningSnacks", !isMorningSnackChecked) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                                MealToggleGridItem(
                                                    label = "Afternoon",
                                                    isYes = isAfternoonSnackChecked,
                                                    isSnack = true,
                                                    onClick = { viewModel.toggleMealDuty(member.id, "afternoonSnacks", !isAfternoonSnackChecked) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                                MealToggleGridItem(
                                                    label = "Evening",
                                                    isYes = isEveningSnackChecked,
                                                    isSnack = true,
                                                    onClick = { viewModel.toggleMealDuty(member.id, "eveningSnacks", !isEveningSnackChecked) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Immersive Summary Strip matching HTML template perfectly
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0x26000000))
                                            .border(BorderStroke(1.dp, Color(0x0DFFFFFF)), RoundedCornerShape(16.dp))
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Today's Consumption Value",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = "$${currencyFormat.format(todayBill)}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Display Custom vs Global price tags in expanded panel
                                    val memberOverride = overrides.firstOrNull { it.memberId == member.id }
                                    if (memberOverride != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(NeonAccent.copy(alpha = 0.1f))
                                                .border(BorderStroke(1.dp, NeonAccent.copy(alpha = 0.2f)), RoundedCornerShape(12.dp))
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Info,
                                                contentDescription = "Info",
                                                tint = NeonAccent,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "Active: Specific Pricing Override Rate",
                                                fontSize = 11.sp,
                                                color = GlassTextPrimary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }

                                    // Historical Timeline Log Section
                                    Text(
                                        text = "CALENDAR HISTORY",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GlassTextSecondary,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    val sortedLogs = viewModel.getRecordsForMember(member.id, allMealRecords)
                                    if (sortedLogs.isEmpty()) {
                                        Text(
                                            text = "No history recorded yet.",
                                            fontSize = 12.sp,
                                            color = GlassTextSecondary,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    } else {
                                        // History Logs Scrollable Panel inside Details Card
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            sortedLogs.take(5).forEach { record ->
                                                HistoricalRecordRow(record, config.snacksEnabled)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealToggleGridItem(
    label: String,
    isYes: Boolean,
    isSnack: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeBgColor = if (isSnack) Color(0x26FB923C) else Color(0x2622D3EE)
    val activeDotColor = if (isSnack) Color(0xFFFB923C) else Color(0xFF22D3EE)
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isYes) activeBgColor else Color(0x0AFFFFFF))
            .border(
                BorderStroke(1.dp, if (isYes) activeDotColor.copy(alpha = 0.5f) else Color(0x13FFFFFF)),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 10.sp,
                color = if (isYes) Color.White.copy(alpha = 0.8f) else GlassTextSecondary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isYes) "YES" else "NO",
                fontSize = 14.sp,
                color = if (isYes) Color.White else GlassTextSecondary.copy(alpha = 0.4f),
                fontWeight = FontWeight.Black
            )
            if (isYes) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(activeDotColor)
                )
            }
        }
    }
}

@Composable
fun HistoricalRecordRow(record: MealRecord, snacksEnabled: Boolean) {
    val tookItems = mutableListOf<String>()
    val missedItems = mutableListOf<String>()

    // Check Breakfast
    if (record.breakfast) tookItems.add("Breakfast") else missedItems.add("Breakfast")
    // Check Lunch
    if (record.lunch) tookItems.add("Lunch") else missedItems.add("Lunch")
    // Check Dinner
    if (record.dinner) tookItems.add("Dinner") else missedItems.add("Dinner")

    if (snacksEnabled) {
        if (record.morningSnacks) tookItems.add("Morning Snacks") else missedItems.add("Morning Snacks")
        if (record.afternoonSnacks) tookItems.add("Afternoon Snacks") else missedItems.add("Afternoon Snacks")
        if (record.eveningSnacks) tookItems.add("Evening Snacks") else missedItems.add("Evening Snacks")
    }

    val tookString = if (tookItems.isEmpty()) "Nothing" else tookItems.joinToString(", ")
    val missedString = if (missedItems.isEmpty()) "Nothing" else missedItems.joinToString(", ")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x04FFFFFF))
            .border(BorderStroke(1.dp, Color(0x06FFFFFF)), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = record.dateString,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonAccentSecondary
            )
            val costVal = record.breakfastCost + record.lunchCost + record.dinnerCost +
                    record.morningSnacksCost + record.afternoonSnacksCost + record.eveningSnacksCost
            Text(
                text = "$${DecimalFormat("0.00").format(costVal)}",
                fontSize = 11.sp,
                color = GlassTextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "Took: $tookString | Missed: $missedString",
            fontSize = 11.sp,
            color = GlassTextSecondary
        )
    }
}
