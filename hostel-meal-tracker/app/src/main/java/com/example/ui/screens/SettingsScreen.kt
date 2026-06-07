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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.Member
import com.example.data.database.PricingConfig
import com.example.data.database.MemberRateOverride
import com.example.ui.components.*
import com.example.ui.viewmodel.MealViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MealViewModel,
    onNavigateBack: () -> Unit
) {
    val members by viewModel.members.collectAsState()
    val pricingConfig by viewModel.pricingConfig.collectAsState()
    val overrides by viewModel.overrides.collectAsState()

    // Active sub-section state (to avoid clutter, let's use tabs or beautifully expandable sections)
    var selectedSection by remember { mutableStateOf(1) } // 1: Border Management, 2: Global Configuration, 3: Custom Overrides

    // 1. Members state
    var quickAddName by remember { mutableStateOf("") }
    var editingMemberId by remember { mutableStateOf<Int?>(null) }
    var editingMemberName by remember { mutableStateOf("") }

    // 2. Global rate state
    var isThreeRates by remember { mutableStateOf(false) }
    var flatRateInput by remember { mutableStateOf("100.0") }
    var breakfastRateInput by remember { mutableStateOf("30.0") }
    var lunchRateInput by remember { mutableStateOf("40.0") }
    var dinnerRateInput by remember { mutableStateOf("30.0") }

    var snacksEnabledOption by remember { mutableStateOf(false) }
    var morningSnacksInput by remember { mutableStateOf("15.0") }
    var afternoonSnacksInput by remember { mutableStateOf("15.0") }
    var eveningSnacksInput by remember { mutableStateOf("15.0") }

    // Synchronize global states with DB when page loads
    LaunchedEffect(pricingConfig) {
        pricingConfig?.let {
            isThreeRates = it.isThreeRates
            flatRateInput = it.breakfastRate.toString() // default flat
            breakfastRateInput = it.breakfastRate.toString()
            lunchRateInput = it.lunchRate.toString()
            dinnerRateInput = it.dinnerRate.toString()
            snacksEnabledOption = it.snacksEnabled
            morningSnacksInput = it.morningSnacksRate.toString()
            afternoonSnacksInput = it.afternoonSnacksRate.toString()
            eveningSnacksInput = it.eveningSnacksRate.toString()
        }
    }

    // 3. Custom pricing overrides state
    var selectedOverrideMemberId by remember { mutableStateOf<Int?>(null) }
    var showMemberSearchDialog by remember { mutableStateOf(false) }
    var memberSearchQuery by remember { mutableStateOf("") }
    val selectedMember = members.find { it.id == selectedOverrideMemberId }
    val memberOverride = overrides.find { it.memberId == selectedOverrideMemberId }

    var overrideBreakfastActive by remember { mutableStateOf(false) }
    var overrideBreakfastVal by remember { mutableStateOf("0.0") }
    var overrideLunchActive by remember { mutableStateOf(false) }
    var overrideLunchVal by remember { mutableStateOf("0.0") }
    var overrideDinnerActive by remember { mutableStateOf(false) }
    var overrideDinnerVal by remember { mutableStateOf("0.0") }

    var overrideMorningSnackActive by remember { mutableStateOf(false) }
    var overrideMorningSnackVal by remember { mutableStateOf("0.0") }
    var overrideAfternoonSnackActive by remember { mutableStateOf(false) }
    var overrideAfternoonSnackVal by remember { mutableStateOf("0.0") }
    var overrideEveningSnackActive by remember { mutableStateOf(false) }
    var overrideEveningSnackVal by remember { mutableStateOf("0.0") }

    // Synchronize override values when selected override member changes
    LaunchedEffect(selectedOverrideMemberId, overrides) {
        val ov = overrides.find { it.memberId == selectedOverrideMemberId }
        if (ov != null) {
            overrideBreakfastActive = ov.customBreakfastRate != null
            overrideBreakfastVal = (ov.customBreakfastRate ?: 0.0).toString()
            overrideLunchActive = ov.customLunchRate != null
            overrideLunchVal = (ov.customLunchRate ?: 0.0).toString()
            overrideDinnerActive = ov.customDinnerRate != null
            overrideDinnerVal = (ov.customDinnerRate ?: 0.0).toString()

            overrideMorningSnackActive = ov.customMorningSnacksRate != null
            overrideMorningSnackVal = (ov.customMorningSnacksRate ?: 0.0).toString()
            overrideAfternoonSnackActive = ov.customAfternoonSnacksRate != null
            overrideAfternoonSnackVal = (ov.customAfternoonSnacksRate ?: 0.0).toString()
            overrideEveningSnackActive = ov.customEveningSnacksRate != null
            overrideEveningSnackVal = (ov.customEveningSnacksRate ?: 0.0).toString()
        } else {
            overrideBreakfastActive = false
            overrideBreakfastVal = "0.0"
            overrideLunchActive = false
            overrideLunchVal = "0.0"
            overrideDinnerActive = false
            overrideDinnerVal = "0.0"

            overrideMorningSnackActive = false
            overrideMorningSnackVal = "0.0"
            overrideAfternoonSnackActive = false
            overrideAfternoonSnackVal = "0.0"
            overrideEveningSnackActive = false
            overrideEveningSnackVal = "0.0"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("App Control Settings", color = GlassTextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = CosmicBgStart
                )
            )
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
            // Horizontal Segmented Tabs navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x0AFFFFFF))
                    .padding(4.dp)
            ) {
                listOf(
                    Triple(1, "Borders", Icons.Default.Person),
                    Triple(2, "Default Rates", Icons.Default.Build),
                    Triple(3, "Custom Rates", Icons.Default.Edit)
                ).forEach { (id, title, icon) ->
                    val isSelected = selectedSection == id
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) NeonAccent else Color.Transparent)
                            .clickable { selectedSection = id }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (isSelected) Color.White else GlassTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else GlassTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // RENDER SECTION MODULE:
            when (selectedSection) {
                1 -> {
                    // SECTION 1: MEMBER / BORDERS MANAGEMENT
                    Column(modifier = Modifier.fillMaxSize()) {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Quick Add Member",
                                color = GlassTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = quickAddName,
                                    onValueChange = { quickAddName = it },
                                    placeholder = { Text("Full Name...", color = GlassTextSecondary) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = GlassTextPrimary,
                                        unfocusedTextColor = GlassTextPrimary,
                                        focusedBorderColor = NeonAccentSecondary,
                                        unfocusedBorderColor = GlassBorderColor,
                                        focusedContainerColor = Color(0x0CFFFFFF),
                                        unfocusedContainerColor = Color(0x04FFFFFF)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        if (quickAddName.isNotBlank()) {
                                            viewModel.addMember(quickAddName.trim())
                                            quickAddName = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(NeonAccentSecondary)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Scrollable List of active borders
                        Text(
                            text = "ACTIVE BORDERS REGISTERED (${members.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextSecondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (members.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No members configured.", color = GlassTextSecondary)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(members, key = { it.id }) { member ->
                                    val isEditing = editingMemberId == member.id
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0x09FFFFFF))
                                            .border(BorderStroke(1.dp, Color(0x0CFFFFFF)), RoundedCornerShape(16.dp))
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isEditing) {
                                            OutlinedTextField(
                                                value = editingMemberName,
                                                onValueChange = { editingMemberName = it },
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedTextColor = GlassTextPrimary,
                                                    focusedBorderColor = NeonAccent,
                                                    unfocusedBorderColor = GlassBorderColor
                                                ),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            IconButton(onClick = {
                                                if (editingMemberName.isNotBlank()) {
                                                    viewModel.updateMemberName(member.id, editingMemberName.trim())
                                                    editingMemberId = null
                                                }
                                            }) {
                                                Icon(Icons.Default.Check, contentDescription = "Save edit", tint = Color.Green)
                                            }
                                        } else {
                                            Text(
                                                text = member.name,
                                                color = GlassTextPrimary,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(onClick = {
                                                editingMemberId = member.id
                                                editingMemberName = member.name
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit name", tint = GlassTextSecondary, modifier = Modifier.size(18.dp))
                                            }
                                            IconButton(onClick = {
                                                viewModel.removeMember(member.id)
                                                if (selectedOverrideMemberId == member.id) {
                                                    selectedOverrideMemberId = null
                                                }
                                            }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete border", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // SECTION 2: GLOBAL MEAL & SNACK RATE MODIFIERS
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Default Hostel Meal Rates",
                                        color = GlassTextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    GlassToggleRow(
                                        label = "Different rates for Breakfast/Lunch/Dinner?",
                                        isChecked = isThreeRates,
                                        onCheckedChange = { isThreeRates = it }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    if (!isThreeRates) {
                                        GlassTextField(
                                            value = flatRateInput,
                                            onValueChange = { flatRateInput = it },
                                            label = "Flat Rate Per Meal",
                                            keyboardType = KeyboardType.Number
                                        )
                                    } else {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                GlassTextField(
                                                    value = breakfastRateInput,
                                                    onValueChange = { breakfastRateInput = it },
                                                    label = "Breakfast",
                                                    keyboardType = KeyboardType.Number
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                GlassTextField(
                                                    value = lunchRateInput,
                                                    onValueChange = { lunchRateInput = it },
                                                    label = "Lunch",
                                                    keyboardType = KeyboardType.Number
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                GlassTextField(
                                                    value = dinnerRateInput,
                                                    onValueChange = { dinnerRateInput = it },
                                                    label = "Dinner",
                                                    keyboardType = KeyboardType.Number
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Default Hostel Snack Expenses",
                                        color = GlassTextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    GlassToggleRow(
                                        label = "Enable snacks timings globally?",
                                        isChecked = snacksEnabledOption,
                                        onCheckedChange = { snacksEnabledOption = it }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    AnimatedVisibility(
                                        visible = snacksEnabledOption,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut()
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                GlassTextField(
                                                    value = morningSnacksInput,
                                                    onValueChange = { morningSnacksInput = it },
                                                    label = "Morning",
                                                    keyboardType = KeyboardType.Number
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                GlassTextField(
                                                    value = afternoonSnacksInput,
                                                    onValueChange = { afternoonSnacksInput = it },
                                                    label = "Afternoon",
                                                    keyboardType = KeyboardType.Number
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                GlassTextField(
                                                    value = eveningSnacksInput,
                                                    onValueChange = { eveningSnacksInput = it },
                                                    label = "Evening",
                                                    keyboardType = KeyboardType.Number
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Save Global modifier button
                        GlassButton(
                            text = "Save Pricing Rates",
                            onClick = {
                                val flatVal = flatRateInput.toDoubleOrNull() ?: 0.0
                                val bVal = breakfastRateInput.toDoubleOrNull() ?: 0.0
                                val lVal = lunchRateInput.toDoubleOrNull() ?: 0.0
                                val dVal = dinnerRateInput.toDoubleOrNull() ?: 0.0

                                val mSnacks = morningSnacksInput.toDoubleOrNull() ?: 0.0
                                val aSnacks = afternoonSnacksInput.toDoubleOrNull() ?: 0.0
                                val eSnacks = eveningSnacksInput.toDoubleOrNull() ?: 0.0

                                viewModel.saveGlobalPricing(
                                    isThreeRates = isThreeRates,
                                    flatRate = flatVal,
                                    breakfast = bVal,
                                    lunch = lVal,
                                    dinner = dVal,
                                    snacksEnabled = snacksEnabledOption,
                                    morningSnacks = mSnacks,
                                    afternoonSnacks = aSnacks,
                                    eveningSnacks = eSnacks
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                    }
                }

                3 -> {
                    // SECTION 3: SPECIFIC MEMBER PRICING OVERRIDES
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Select Border & Override Pricing",
                                        color = GlassTextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    if (members.isEmpty()) {
                                        Text("Register borders first.", color = GlassTextSecondary)
                                    } else {
                                        // Members chooser list - Searchable Dialog Trigger
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0x0EFFFFFF))
                                                .border(BorderStroke(1.dp, GlassBorderColor), RoundedCornerShape(12.dp))
                                                .clickable { 
                                                    memberSearchQuery = ""
                                                    showMemberSearchDialog = true 
                                                }
                                                .padding(horizontal = 16.dp, vertical = 14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = selectedMember?.name ?: "Tap to choose a member...",
                                                color = if (selectedMember != null) GlassTextPrimary else GlassTextSecondary,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Search member",
                                                tint = NeonAccent
                                            )
                                        }

                                        // Searchable Dialog Modal
                                        if (showMemberSearchDialog) {
                                            AlertDialog(
                                                onDismissRequest = { showMemberSearchDialog = false },
                                                containerColor = CosmicBgEnd,
                                                shape = RoundedCornerShape(24.dp),
                                                modifier = Modifier.border(BorderStroke(1.dp, Color(0x1AFFFFFF)), RoundedCornerShape(24.dp)),
                                                title = {
                                                    Text(
                                                        "Select Hostel Border",
                                                        color = GlassTextPrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 20.sp
                                                    )
                                                },
                                                text = {
                                                    Column {
                                                        OutlinedTextField(
                                                            value = memberSearchQuery,
                                                            onValueChange = { memberSearchQuery = it },
                                                            placeholder = { Text("Search by name...", color = GlassTextSecondary) },
                                                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GlassTextSecondary) },
                                                            trailingIcon = {
                                                                if (memberSearchQuery.isNotEmpty()) {
                                                                    IconButton(onClick = { memberSearchQuery = "" }) {
                                                                        Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = GlassTextSecondary)
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
                                                            shape = RoundedCornerShape(12.dp),
                                                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                                        )

                                                        val filteredMembers = members.filter { it.name.contains(memberSearchQuery, ignoreCase = true) }
                                                        
                                                        if (filteredMembers.isEmpty()) {
                                                            Box(
                                                                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text("No borders found.", color = GlassTextSecondary)
                                                            }
                                                        } else {
                                                            LazyColumn(
                                                                modifier = Modifier.heightIn(max = 280.dp),
                                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                                            ) {
                                                                items(filteredMembers) { m ->
                                                                    Row(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .clip(RoundedCornerShape(12.dp))
                                                                            .background(if (selectedOverrideMemberId == m.id) NeonAccent.copy(alpha = 0.15f) else Color(0x06FFFFFF))
                                                                            .border(
                                                                                BorderStroke(1.dp, if (selectedOverrideMemberId == m.id) NeonAccent else Color.Transparent),
                                                                                RoundedCornerShape(12.dp)
                                                                            )
                                                                            .clickable {
                                                                                selectedOverrideMemberId = m.id
                                                                                showMemberSearchDialog = false
                                                                            }
                                                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                    ) {
                                                                        Icon(
                                                                            imageVector = Icons.Default.Person,
                                                                            contentDescription = null,
                                                                            tint = if (selectedOverrideMemberId == m.id) NeonAccent else GlassTextSecondary,
                                                                            modifier = Modifier.size(20.dp)
                                                                        )
                                                                        Spacer(modifier = Modifier.width(12.dp))
                                                                        Text(
                                                                            text = m.name,
                                                                            color = GlassTextPrimary,
                                                                            fontSize = 18.sp,
                                                                            fontWeight = FontWeight.SemiBold
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                },
                                                confirmButton = {},
                                                dismissButton = {
                                                    TextButton(onClick = { showMemberSearchDialog = false }) {
                                                        Text("Close", color = NeonAccent)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            if (selectedMember != null) {
                                item {
                                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "Pricing Overrides: ${selectedMember.name}",
                                            color = GlassTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(bottom = 12.dp)
                                        )

                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            // Breakfast Override
                                            OverrideInputRow(
                                                label = "Custom Breakfast Rate",
                                                isActive = overrideBreakfastActive,
                                                onActiveChange = { overrideBreakfastActive = it },
                                                value = overrideBreakfastVal,
                                                onValueChange = { overrideBreakfastVal = it }
                                            )

                                            // Lunch Override
                                            OverrideInputRow(
                                                label = "Custom Lunch Rate",
                                                isActive = overrideLunchActive,
                                                onActiveChange = { overrideLunchActive = it },
                                                value = overrideLunchVal,
                                                onValueChange = { overrideLunchVal = it }
                                            )

                                            // Dinner Override
                                            OverrideInputRow(
                                                label = "Custom Dinner Rate",
                                                isActive = overrideDinnerActive,
                                                onActiveChange = { overrideDinnerActive = it },
                                                value = overrideDinnerVal,
                                                onValueChange = { overrideDinnerVal = it }
                                            )

                                            // Snack Overrides (If snacks enabled globally)
                                            val snacks = pricingConfig?.snacksEnabled ?: false
                                            if (snacks) {
                                                OverrideInputRow(
                                                    label = "Custom Morning Snack",
                                                    isActive = overrideMorningSnackActive,
                                                    onActiveChange = { overrideMorningSnackActive = it },
                                                    value = overrideMorningSnackVal,
                                                    onValueChange = { overrideMorningSnackVal = it }
                                                )

                                                OverrideInputRow(
                                                    label = "Custom Afternoon Snack",
                                                    isActive = overrideAfternoonSnackActive,
                                                    onActiveChange = { overrideAfternoonSnackActive = it },
                                                    value = overrideAfternoonSnackVal,
                                                    onValueChange = { overrideAfternoonSnackVal = it }
                                                )

                                                OverrideInputRow(
                                                    label = "Custom Evening Snack",
                                                    isActive = overrideEveningSnackActive,
                                                    onActiveChange = { overrideEveningSnackActive = it },
                                                    value = overrideEveningSnackVal,
                                                    onValueChange = { overrideEveningSnackVal = it }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (selectedMember != null) {
                            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                                GlassButton(
                                    text = "Apply Custome Rates Override",
                                    onClick = {
                                        val cBreakfast = if (overrideBreakfastActive) overrideBreakfastVal.toDoubleOrNull() ?: 0.0 else null
                                        val cLunch = if (overrideLunchActive) overrideLunchVal.toDoubleOrNull() ?: 0.0 else null
                                        val cDinner = if (overrideDinnerActive) overrideDinnerVal.toDoubleOrNull() ?: 0.0 else null

                                        val cMorning = if (overrideMorningSnackActive) overrideMorningSnackVal.toDoubleOrNull() ?: 0.0 else null
                                        val cAfternoon = if (overrideAfternoonSnackActive) overrideAfternoonSnackVal.toDoubleOrNull() ?: 0.0 else null
                                        val cEvening = if (overrideEveningSnackActive) overrideEveningSnackVal.toDoubleOrNull() ?: 0.0 else null

                                        viewModel.saveMemberOverride(
                                            memberId = selectedMember.id,
                                            customBreakfast = cBreakfast,
                                            customLunch = cLunch,
                                            customDinner = cDinner,
                                            customMorningSnacks = cMorning,
                                            customAfternoonSnacks = cAfternoon,
                                            customEveningSnacks = cEvening
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        viewModel.removeMemberOverride(selectedMember.id)
                                        overrideBreakfastActive = false
                                        overrideLunchActive = false
                                        overrideDinnerActive = false
                                        overrideMorningSnackActive = false
                                        overrideAfternoonSnackActive = false
                                        overrideEveningSnackActive = false
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(50.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                ) {
                                    Text("Clear All Overrides For This Member")
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
fun OverrideInputRow(
    label: String,
    isActive: Boolean,
    onActiveChange: (Boolean) -> Unit,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x06FFFFFF))
            .border(BorderStroke(1.dp, Color(0x0DFFFFFF)), RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isActive,
            onCheckedChange = onActiveChange,
            colors = CheckboxDefaults.colors(
                checkedColor = NeonAccent,
                uncheckedColor = GlassTextSecondary
            )
        )

        Spacer(modifier = Modifier.width(4.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = GlassTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(
                text = if (isActive) "Assigned custom cost override" else "Using default global price",
                color = if (isActive) NeonAccentSecondary else GlassTextSecondary,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier.width(100.dp)) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = isActive,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = GlassTextPrimary,
                    unfocusedTextColor = GlassTextPrimary,
                    disabledTextColor = GlassTextSecondary.copy(alpha = 0.5f),
                    focusedBorderColor = NeonAccent,
                    unfocusedBorderColor = GlassBorderColor
                ),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 13.sp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
