package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getMemberById(id: Int): Member?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    @Query("DELETE FROM members WHERE id = :id")
    suspend fun deleteMemberById(id: Int)
}

@Dao
interface PricingConfigDao {
    @Query("SELECT * FROM pricing_config WHERE id = 1")
    fun getPricingConfig(): Flow<PricingConfig?>

    @Query("SELECT * FROM pricing_config WHERE id = 1")
    suspend fun getPricingConfigSync(): PricingConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePricingConfig(config: PricingConfig)
}

@Dao
interface MemberRateOverrideDao {
    @Query("SELECT * FROM member_rate_overrides")
    fun getAllOverrides(): Flow<List<MemberRateOverride>>

    @Query("SELECT * FROM member_rate_overrides WHERE memberId = :memberId")
    fun getOverridesForMember(memberId: Int): Flow<MemberRateOverride?>

    @Query("SELECT * FROM member_rate_overrides WHERE memberId = :memberId")
    suspend fun getOverrideSync(memberId: Int): MemberRateOverride?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateOverride(override: MemberRateOverride)

    @Query("DELETE FROM member_rate_overrides WHERE memberId = :memberId")
    suspend fun deleteOverrideForMember(memberId: Int)
}

@Dao
interface MealRecordDao {
    @Query("SELECT * FROM meal_records WHERE memberId = :memberId ORDER BY dateString DESC")
    fun getMealRecordsForMember(memberId: Int): Flow<List<MealRecord>>

    @Query("SELECT * FROM meal_records WHERE memberId = :memberId AND dateString = :dateString")
    fun getMealRecordForMemberAndDate(memberId: Int, dateString: String): Flow<MealRecord?>

    @Query("SELECT * FROM meal_records WHERE memberId = :memberId AND dateString = :dateString")
    suspend fun getMealRecordForMemberAndDateSync(memberId: Int, dateString: String): MealRecord?

    @Query("SELECT * FROM meal_records ORDER BY dateString DESC")
    fun getAllMealRecords(): Flow<List<MealRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMealRecord(record: MealRecord)

    @Query("DELETE FROM meal_records WHERE memberId = :memberId")
    suspend fun deleteMealsByMemberId(memberId: Int)
}
