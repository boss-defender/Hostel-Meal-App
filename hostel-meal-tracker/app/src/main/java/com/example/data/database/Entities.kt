package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "pricing_config")
data class PricingConfig(
    @PrimaryKey val id: Int = 1,
    val isThreeRates: Boolean,
    val breakfastRate: Double,
    val lunchRate: Double,
    val dinnerRate: Double,
    val snacksEnabled: Boolean,
    val morningSnacksRate: Double,
    val afternoonSnacksRate: Double,
    val eveningSnacksRate: Double
)

@Entity(tableName = "member_rate_overrides")
data class MemberRateOverride(
    @PrimaryKey val memberId: Int,
    val customBreakfastRate: Double?,
    val customLunchRate: Double?,
    val customDinnerRate: Double?,
    val customMorningSnacksRate: Double?,
    val customAfternoonSnacksRate: Double?,
    val customEveningSnacksRate: Double?
)

@Entity(tableName = "meal_records", primaryKeys = ["memberId", "dateString"])
data class MealRecord(
    val memberId: Int,
    val dateString: String, // format: "yyyy-MM-dd"
    val breakfast: Boolean,
    val lunch: Boolean,
    val dinner: Boolean,
    val morningSnacks: Boolean,
    val afternoonSnacks: Boolean,
    val eveningSnacks: Boolean,
    val breakfastCost: Double,
    val lunchCost: Double,
    val dinnerCost: Double,
    val morningSnacksCost: Double,
    val afternoonSnacksCost: Double,
    val eveningSnacksCost: Double
)
