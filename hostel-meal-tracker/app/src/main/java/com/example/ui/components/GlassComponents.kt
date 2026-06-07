package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color definitions for Immersive UI Theme
val CosmicBgStart = Color(0xFF0B0E14)
val CosmicBgEnd = Color(0xFF0B0E14)
val NeonAccent = Color(0xFF22D3EE)          // Cyan-400
val NeonAccentSecondary = Color(0xFF6366F1) // Indigo-500
val NeonOrange = Color(0xFFFB923C)          // Orange-400 for snacks
val GlassSurface = Color(0x0DFFFFFF)        // bg-white/5
val GlassBorderColor = Color(0x19FFFFFF)    // border-white/10
val GlassTextPrimary = Color(0xFFF1F5F9)     // Slate-100
val GlassTextSecondary = Color(0xFF94A3B8)   // Slate-400

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0x0DFFFFFF))
            .border(
                BorderStroke(1.dp, Color(0x1AFFFFFF)),
                RoundedCornerShape(28.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun GlassSegmentedButton(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0x0DFFFFFF))
            .border(BorderStroke(1.dp, Color(0x12FFFFFF)), RoundedCornerShape(50.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50.dp))
                    .then(
                        if (isSelected) {
                            Modifier.background(brush = Brush.horizontalGradient(colors = listOf(NeonAccent, NeonAccentSecondary)))
                        } else {
                            Modifier.background(color = Color.Transparent)
                        }
                    )
                    .clickable { onOptionSelected(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color.White else GlassTextSecondary,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = GlassTextSecondary) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = GlassTextPrimary,
            unfocusedTextColor = GlassTextPrimary,
            focusedContainerColor = Color(0x0AFFFFFF),
            unfocusedContainerColor = Color(0x04FFFFFF),
            focusedBorderColor = NeonAccent,
            unfocusedBorderColor = GlassBorderColor,
            cursorColor = NeonAccentSecondary
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color(0x11FFFFFF)
        ),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(50.dp),
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(50.dp))
            .border(
                if (enabled) BorderStroke(0.dp, Color.Transparent)
                else BorderStroke(1.dp, Color(0x12FFFFFF)),
                RoundedCornerShape(50.dp)
            )
    ) {
        val bgBrush = if (enabled) {
            Brush.horizontalGradient(colors = listOf(NeonAccent, NeonAccentSecondary))
        } else {
            Brush.horizontalGradient(colors = listOf(Color(0x11FFFFFF), Color(0x11FFFFFF)))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = bgBrush),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (enabled) Color.White else GlassTextSecondary.copy(alpha = 0.5f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GlassToggleRow(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x08FFFFFF))
            .border(BorderStroke(1.dp, Color(0x0AFFFFFF)), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = GlassTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NeonAccent,
                uncheckedThumbColor = GlassTextSecondary,
                uncheckedTrackColor = Color(0x1AFFFFFF)
            )
        )
    }
}
