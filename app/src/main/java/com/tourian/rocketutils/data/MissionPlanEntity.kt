package com.tourian.rocketutils.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "mission_plans")
data class MissionPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val missionName: String,
    val bodyName: String,
    val initialAltitude: Int,
    val targetAltitude: Int,
    val isKilometers: Boolean,
    val totalDeltaV: String,
    val timestamp: Long = System.currentTimeMillis()
)


@Dao
interface MissionPlanDao {
    @Query("SELECT * FROM mission_plans ORDER BY timestamp DESC")
    fun getAllMissions(): Flow<List<MissionPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: MissionPlanEntity)

    @Delete
    suspend fun deleteMission(mission: MissionPlanEntity)
}

@Database(entities = [MissionPlanEntity::class], version = 1, exportSchema = false)
abstract class RocketDatabase : RoomDatabase() {
    abstract fun missionPlanDao(): MissionPlanDao

    companion object {
        @Volatile
        private var INSTANCE: RocketDatabase? = null

        fun getDatabase(context: Context): RocketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context = context.applicationContext,
                    klass = RocketDatabase::class.java,
                    name = "rocket_utils_database"
                ).fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class MissionRepository(private val dao: MissionPlanDao) {
    val allMissions: Flow<List<MissionPlanEntity>> = dao.getAllMissions()

    suspend fun insert(mission: MissionPlanEntity) {
        dao.insertMission(mission)
    }

    suspend fun delete(mission: MissionPlanEntity) {
        dao.deleteMission(mission)
    }
}
