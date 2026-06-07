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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.viewmodel.MealViewModel
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(
    viewModel: MealViewModel,
    onFinished: () -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }

    // Configuration states
    var sameMealRate by remember { mutableStateOf("No") } // "No" means different, "Yes" means same
    var flatRateInput by remember { mutableStateOf("100") }
    var breakfastRateInput by remember { mutableStateOf("30") }
    var lunchRateInput by remember { mutableStateOf("40") }
    var dinnerRateInput by remember { mutableStateOf("30") }

    var snacksEnabledOption by remember { mutableStateOf("No") } // "Yes" / "No"
    var morningSnacksInput by remember { mutableStateOf("15") }
    var afternoonSnacksInput by remember { mutableStateOf("15") }
    var eveningSnacksInput by remember { mutableStateOf("15") }

    // Members list state for initial setup
    var tempMembersList by remember { mutableStateOf(listOf<String>()) }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var newMemberNameInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CosmicBgStart, CosmicBgEnd)
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        when (currentStep) {
            1 -> {
                // SCREEN 1: SPLASH SCREEN (2 seconds)
                LaunchedEffect(Unit) {
                    delay(2000)
                    currentStep = 2
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = GlassSurface,
                            border = BorderStroke(1.dp, Color(0x33FFFFFF))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Meal App Logo",
                                    tint = NeonAccentSecondary,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "This is Hostel meal app",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sleek Hostels Expense Manager",
                            fontSize = 14.sp,
                            color = GlassTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            2 -> {
                // SCREEN 2: BASE MEAL RATE CONFIGURATION
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "Meal Rate Configuration",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Define your hostel base meal expenses to start automation tracker.",
                            fontSize = 14.sp,
                            color = GlassTextSecondary
                        )
                        Spacer(modifier = Modifier.height(40.dp))

                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Is your meal rate different for breakfast, lunch, and dinner?",
                                color = GlassTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Segmented control: Yes -> Different Rates, No -> Same Rate (Flat)
                            GlassSegmentedButton(
                                options = listOf("Yes", "No"),
                                selectedOption = if (sameMealRate == "Yes") "Yes" else "No",
                                onOptionSelected = { sameMealRate = it }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            if (sameMealRate == "No") {
                                GlassTextField(
                                    value = flatRateInput,
                                    onValueChange = { flatRateInput = it },
                                    label = "Enter Flat Meal Rate (Per Meal)",
                                    keyboardType = KeyboardType.Number
                                )
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        GlassTextField(
                                            value = breakfastRateInput,
                                            onValueChange = { breakfastRateInput = it },
                                            label = "Breakfast Rate",
                                            keyboardType = KeyboardType.Number
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        GlassTextField(
                                            value = lunchRateInput,
                                            onValueChange = { lunchRateInput = it },
                                            label = "Lunch Rate",
                                            keyboardType = KeyboardType.Number
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        GlassTextField(
                                            value = dinnerRateInput,
                                            onValueChange = { dinnerRateInput = it },
                                            label = "Dinner Rate",
                                            keyboardType = KeyboardType.Number
                                        )
                                    }
                                }
                            }
                        }
                    }

                    GlassButton(
                        text = "Next",
                        onClick = { currentStep = 3 },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            3 -> {
                // SCREEN 3: SNACKS CONFIGURATION
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "Snacks Configuration",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Manage snack times and expenses in your hostel timeline.",
                            fontSize = 14.sp,
                            color = GlassTextSecondary
                        )
                        Spacer(modifier = Modifier.height(40.dp))

                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Do you have snacks at afternoon, evening, or morning (10 AM - 11 AM)?",
                                color = GlassTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Segmented control
                            GlassSegmentedButton(
                                options = listOf("Yes", "No"),
                                selectedOption = snacksEnabledOption,
                                onOptionSelected = { snacksEnabledOption = it }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            AnimatedVisibility(
                                visible = snacksEnabledOption == "Yes",
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

                    Row {
                        OutlinedButton(
                            onClick = { currentStep = 2 },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GlassTextPrimary),
                            border = BorderStroke(1.dp, GlassBorderColor),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                        ) {
                            Text("Back")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        GlassButton(
                            text = "Next",
                            onClick = { currentStep = 4 },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            4 -> {
                // SCREEN 4: ADD MEMBERS INITIAL SETUP
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Spacer(modifier = Modifier.height(30.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Add Members",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GlassTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Configure names of hostel borders.",
                                    fontSize = 14.sp,
                                    color = GlassTextSecondary
                                )
                            }

                            // Clean, glowing FAB to Add Member
                            FloatingActionButton(
                                onClick = { showAddMemberDialog = true },
                                containerColor = NeonAccent,
                                contentColor = Color.White,
                                shape = CircleShape,
                                modifier = Modifier.size(52.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Member")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (tempMembersList.isEmpty()) {
                            // Empty state guidance
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "No Members Added Yet",
                                        color = GlassTextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap the '+' button above to add members to your meal tracker records workspace.",
                                        color = GlassTextSecondary,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        } else {
                            // Row containing added members list
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(tempMembersList) { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0x06FFFFFF))
                                            .border(BorderStroke(1.dp, Color(0x0AFFFFFF)), RoundedCornerShape(16.dp))
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item,
                                            color = GlassTextPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove Name",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clickable {
                                                    tempMembersList = tempMembersList.filter { it != item }
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Row {
                            OutlinedButton(
                                onClick = { currentStep = 3 },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GlassTextPrimary),
                                border = BorderStroke(1.dp, GlassBorderColor),
                                shape = RoundedCornerShape(50.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp)
                            ) {
                                Text("Back")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            GlassButton(
                                text = "DONE",
                                enabled = tempMembersList.isNotEmpty(),
                                onClick = {
                                    // SAVE ALL STUFF TO ROOM DATABASE INSTANTLY
                                    val isThreeRates = sameMealRate == "Yes"
                                    val flatRate = flatRateInput.toDoubleOrNull() ?: 100.0
                                    val bRate = breakfastRateInput.toDoubleOrNull() ?: 30.0
                                    val lRate = lunchRateInput.toDoubleOrNull() ?: 40.0
                                    val dRate = dinnerRateInput.toDoubleOrNull() ?: 30.0

                                    val isSnacks = snacksEnabledOption == "Yes"
                                    val mSnack = morningSnacksInput.toDoubleOrNull() ?: 15.0
                                    val aSnack = afternoonSnacksInput.toDoubleOrNull() ?: 15.0
                                    val eSnack = eveningSnacksInput.toDoubleOrNull() ?: 15.0

                                    viewModel.saveGlobalPricing(
                                        isThreeRates = isThreeRates,
                                        flatRate = flatRate,
                                        breakfast = bRate,
                                        lunch = lRate,
                                        dinner = dRate,
                                        snacksEnabled = isSnacks,
                                        morningSnacks = mSnack,
                                        afternoonSnacks = aSnack,
                                        eveningSnacks = eSnack
                                    )

                                    // Add members
                                    tempMembersList.forEach { name ->
                                        viewModel.addMember(name)
                                    }

                                    // Move to congratulations screen
                                    currentStep = 5
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Add member dialog
                if (showAddMemberDialog) {
                    AlertDialog(
                        onDismissRequest = { showAddMemberDialog = false },
                        containerColor = CosmicBgEnd,
                        title = {
                            Text(
                                "Add Hostel Member",
                                color = GlassTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))
                                GlassTextField(
                                    value = newMemberNameInput,
                                    onValueChange = { newMemberNameInput = it },
                                    label = "Enter member's name"
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (newMemberNameInput.isNotBlank()) {
                                        tempMembersList = tempMembersList + newMemberNameInput.trim()
                                        newMemberNameInput = ""
                                        showAddMemberDialog = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonAccent)
                            ) {
                                Text("Add")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddMemberDialog = false }) {
                                Text("Cancel", color = GlassTextSecondary)
                            }
                        }
                    )
                }
            }

            5 -> {
                // SCREEN 5: SUCCESS BANNER (Duration: 2.5 seconds)
                LaunchedEffect(Unit) {
                    delay(2500)
                    viewModel.completeOnboarding()
                    onFinished()
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success tick",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Success!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Well done. You can add or remove members or change the name of members and you can also change the meal rate in the settings later.",
                                fontSize = 15.sp,
                                color = GlassTextPrimary,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
