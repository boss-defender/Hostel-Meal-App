package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.Flow

class MealRepository(private val db: AppDatabase) {
    private val memberDao = db.memberDao()
    private val pricingConfigDao = db.pricingConfigDao()
    private val memberRateOverrideDao = db.memberRateOverrideDao()
    private val mealRecordDao = db.mealRecordDao()

    val allMembers: Flow<List<Member>> = memberDao.getAllMembers()
    val pricingConfig: Flow<PricingConfig?> = pricingConfigDao.getPricingConfig()
    val allOverrides: Flow<List<MemberRateOverride>> = memberRateOverrideDao.getAllOverrides()
    val allMealRecords: Flow<List<MealRecord>> = mealRecordDao.getAllMealRecords()

    suspend fun insertMember(name: String): Long {
        return memberDao.insertMember(Member(name = name))
    }

    suspend fun updateMember(member: Member) {
        memberDao.updateMember(member)
    }

    suspend fun deleteMember(memberId: Int) {
        memberDao.deleteMemberById(memberId)
        memberRateOverrideDao.deleteOverrideForMember(memberId)
        mealRecordDao.deleteMealsByMemberId(memberId)
    }

    suspend fun getPricingConfigSync(): PricingConfig? {
        return pricingConfigDao.getPricingConfigSync()
    }

    suspend fun savePricingConfig(config: PricingConfig) {
        pricingConfigDao.insertOrUpdatePricingConfig(config)
    }

    suspend fun getOverrideSync(memberId: Int): MemberRateOverride? {
        return memberRateOverrideDao.getOverrideSync(memberId)
    }

    suspend fun saveOverride(override: MemberRateOverride) {
        memberRateOverrideDao.insertOrUpdateOverride(override)
    }

    suspend fun deleteOverride(memberId: Int) {
        memberRateOverrideDao.deleteOverrideForMember(memberId)
    }

    suspend fun getMealRecordSync(memberId: Int, dateString: String): MealRecord? {
        return mealRecordDao.getMealRecordForMemberAndDateSync(memberId, dateString)
    }

    suspend fun saveMealRecord(record: MealRecord) {
        mealRecordDao.insertOrUpdateMealRecord(record)
    }

    fun getMealRecordsForMember(memberId: Int): Flow<List<MealRecord>> {
        return mealRecordDao.getMealRecordsForMember(memberId)
    }
}
