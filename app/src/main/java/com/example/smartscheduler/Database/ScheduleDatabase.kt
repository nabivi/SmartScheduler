package com.example.smartscheduler.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = arrayOf(ScheduleInfo::class), version = 2)
abstract class ScheduleDatabase : RoomDatabase() {
    //데이터베이스를 매번 생성하는건 리소스를 많이 사용하므로 싱글톤 권장
    abstract fun scheduleInfoDao(): ScheduleInfoDao


    companion object{
        /* @Volatile = 접근가능한 변수의 값을 cache를 통해 사용하지 않고
        thread가 직접 main memory에 접근 하게하여 동기화. */
        @Volatile
        private var INSTANCE: ScheduleDatabase? = null

        fun getDatabase(context:Context): ScheduleDatabase {
            /* 데이터베이스를 만들어 Room의 데이터베이스 빌더를 사용하여 ScheduleDatabase 클래스의 애플리케이션 컨텍스트에서
             * ScheduleDatabase 객체를 만들고 이름을 "schedule_database"로 지정 */
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleDatabase::class.java,
                    "schedule_database"
                )
                    .addMigrations(MIGRATION_1_2) //addMigrations 추가
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration
        // https://developer.android.com/training/data-storage/room/migrating-db-versions?hl=ko
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'schedule_database' ADD COLUMN 'alarm_hour' INTEGER")
                database.execSQL("ALTER TABLE 'schedule_database' ADD COLUMN 'alarm_minute' INTEGER")
            }
        }
    }

}
