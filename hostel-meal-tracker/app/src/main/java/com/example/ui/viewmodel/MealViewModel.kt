package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.*
import com.example.data.repository.MealRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MealViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MealRepository
    private val prefs = application.getSharedPreferences("hostel_meal_tracker_prefs", Context.MODE_PRIVATE)

    // Current date string used for "Today" operations
    val currentDateString: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Onboarding status
    private val _onboardingComplete = MutableStateFlow(prefs.getBoolean("onboarding_complete", false))
    val onboardingComplete = _onboardingComplete.asStateFlow()

    // Search query on dashboard
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = MealRepository(database)
    }

    // Reactive streams from Database
    val members: StateFlow<List<Member>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pricingConfig: StateFlow<PricingConfig?> = repository.pricingConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val overrides: StateFlow<List<MemberRateOverride>> = repository.allOverrides
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMealRecords: StateFlow<List<MealRecord>> = repository.allMealRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered members list based on search query
    val filteredMembers: StateFlow<List<Member>> = combine(members, _searchQuery) { list, query ->
        if (query.isBlank()) list else list.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean("onboarding_complete", true).apply()
        _onboardingComplete.value = true
    }

    // Reset onboarding for testing
    fun resetOnboarding() {
        prefs.edit().putBoolean("onboarding_complete", false).apply()
        _onboardingComplete.value = false
    }

    // Initialize or Save global pricing configuration
    fun saveGlobalPricing(
        isThreeRates: Boolean,
        flatRate: Double,
        breakfast: Double,
        lunch: Double,
        dinner: Double,
        snacksEnabled: Boolean,
        morningSnacks: Double,
        afternoonSnacks: Double,
        eveningSnacks: Double
    ) {
        viewModelScope.launch {
            val config = PricingConfig(
                isThreeRates = isThreeRates,
                breakfastRate = if (isThreeRates) breakfast else flatRate,
                lunchRate = if (isThreeRates) lunch else flatRate,
                dinnerRate = if (isThreeRates) dinner else flatRate,
                snacksEnabled = snacksEnabled,
                morningSnacksRate = if (snacksEnabled) morningSnacks else 0.0,
                afternoonSnacksRate = if (snacksEnabled) afternoonSnacks else 0.0,
                eveningSnacksRate = if (snacksEnabled) eveningSnacks else 0.0
            )
            repository.savePricingConfig(config)
        }
    }

    // Member operations
    fun addMember(name: String, onComplete: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            val id = repository.insertMember(name)
            onComplete?.invoke(id)
        }
    }

    fun updateMemberName(memberId: Int, newName: String) {
        viewModelScope.launch {
            repository.updateMember(Member(id = memberId, name = newName))
        }
    }

    fun removeMember(memberId: Int) {
        viewModelScope.launch {
            repository.deleteMember(memberId)
        }
    }

    // Pricing override operations
    fun saveMemberOverride(
        memberId: Int,
        customBreakfast: Double?,
        customLunch: Double?,
        customDinner: Double?,
        customMorningSnacks: Double?,
        customAfternoonSnacks: Double?,
        customEveningSnacks: Double?
    ) {
        viewModelScope.launch {
            val override = MemberRateOverride(
                memberId = memberId,
                customBreakfastRate = customBreakfast,
                customLunchRate = customLunch,
                customDinnerRate = customDinner,
                customMorningSnacksRate = customMorningSnacks,
                customAfternoonSnacksRate = customAfternoonSnacks,
                customEveningSnacksRate = customEveningSnacks
            )
            repository.saveOverride(override)
            // Force recalculate today's logs specifically to align instantly
            recalculateRecordCostsForMember(memberId)
        }
    }

    fun removeMemberOverride(memberId: Int) {
        viewModelScope.launch {
            repository.deleteOverride(memberId)
            recalculateRecordCostsForMember(memberId)
        }
    }

    // Toggle specific Meal Attendance for today
    fun toggleMealDuty(memberId: Int, period: String, isChecked: Boolean) {
        viewModelScope.launch {
            val date = currentDateString
            val currentRecord = repository.getMealRecordSync(memberId, date) ?: MealRecord(
                memberId = memberId,
                dateString = date,
                breakfast = false, lunch = false, dinner = false,
                morningSnacks = false, afternoonSnacks = false, eveningSnacks = false,
                breakfastCost = 0.0, lunchCost = 0.0, dinnerCost = 0.0,
                morningSnacksCost = 0.0, afternoonSnacksCost = 0.0, eveningSnacksCost = 0.0
            )

            val config = repository.getPricingConfigSync() ?: PricingConfig(
                isThreeRates = false, breakfastRate = 0.0, lunchRate = 0.0, dinnerRate = 0.0,
                snacksEnabled = false, morningSnacksRate = 0.0, afternoonSnacksRate = 0.0, eveningSnacksRate = 0.0
            )
            val memberOverride = repository.getOverrideSync(memberId)

            val updatedRecord = when (period.lowercase()) {
                "breakfast" -> {
                    val rate = memberOverride?.customBreakfastRate ?: config.breakfastRate
                    currentRecord.copy(
                        breakfast = isChecked,
                        breakfastCost = if (isChecked) rate else 0.0
                    )
                }
                "lunch" -> {
                    val rate = memberOverride?.customLunchRate ?: config.lunchRate
                    currentRecord.copy(
                        lunch = isChecked,
                        lunchCost = if (isChecked) rate else 0.0
                    )
                }
                "dinner" -> {
                    val rate = memberOverride?.customDinnerRate ?: config.dinnerRate
                    currentRecord.copy(
                        dinner = isChecked,
                        dinnerCost = if (isChecked) rate else 0.0
                    )
                }
                "morningsnacks" -> {
                    val rate = memberOverride?.customMorningSnacksRate ?: config.morningSnacksRate
                    currentRecord.copy(
                        morningSnacks = isChecked,
                        morningSnacksCost = if (isChecked) rate else 0.0
                    )
                }
                "afternoonsnacks" -> {
                    val rate = memberOverride?.customAfternoonSnacksRate ?: config.afternoonSnacksRate
                    currentRecord.copy(
                        afternoonSnacks = isChecked,
                        afternoonSnacksCost = if (isChecked) rate else 0.0
                    )
                }
                "eveningsnacks" -> {
                    val rate = memberOverride?.customEveningSnacksRate ?: config.eveningSnacksRate
                    currentRecord.copy(
                        eveningSnacks = isChecked,
                        eveningSnacksCost = if (isChecked) rate else 0.0
                    )
                }
                else -> currentRecord
            }

            repository.saveMealRecord(updatedRecord)
        }
    }

    // Method to recalculate record fees if config overrides changed on current date
    private suspend fun recalculateRecordCostsForMember(memberId: Int) {
        val date = currentDateString
        val config = repository.getPricingConfigSync() ?: return
        val memberOverride = repository.getOverrideSync(memberId)
        val currentRecord = repository.getMealRecordSync(memberId, date) ?: return

        val updatedRecord = currentRecord.copy(
            breakfastCost = if (currentRecord.breakfast) (memberOverride?.customBreakfastRate ?: config.breakfastRate) else 0.0,
            lunchCost = if (currentRecord.lunch) (memberOverride?.customLunchRate ?: config.lunchRate) else 0.0,
            dinnerCost = if (currentRecord.dinner) (memberOverride?.customDinnerRate ?: config.dinnerRate) else 0.0,
            morningSnacksCost = if (currentRecord.morningSnacks) (memberOverride?.customMorningSnacksRate ?: config.morningSnacksRate) else 0.0,
            afternoonSnacksCost = if (currentRecord.afternoonSnacks) (memberOverride?.customAfternoonSnacksRate ?: config.afternoonSnacksRate) else 0.0,
            eveningSnacksCost = if (currentRecord.eveningSnacks) (memberOverride?.customEveningSnacksRate ?: config.eveningSnacksRate) else 0.0
        )
        repository.saveMealRecord(updatedRecord)
    }

    // Helper functions for direct UI consumption:
    fun getTodayBillForMember(memberId: Int, records: List<MealRecord>): Double {
        val todayRec = records.firstOrNull { it.memberId == memberId && it.dateString == currentDateString }
        return todayRec?.let {
            it.breakfastCost + it.lunchCost + it.dinnerCost +
                    it.morningSnacksCost + it.afternoonSnacksCost + it.eveningSnacksCost
        } ?: 0.0
    }

    fun getTotalBillForMember(memberId: Int, records: List<MealRecord>): Double {
        return records.filter { it.memberId == memberId }.sumOf {
            it.breakfastCost + it.lunchCost + it.dinnerCost +
                    it.morningSnacksCost + it.afternoonSnacksCost + it.eveningSnacksCost
        }
    }

    fun getRecordsForMember(memberId: Int, records: List<MealRecord>): List<MealRecord> {
        return records.filter { it.memberId == memberId }.sortedByDescending { it.dateString }
    }
}
