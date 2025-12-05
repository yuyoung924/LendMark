package com.example.lendmark.ui.main

import android.app.Application
import androidx.room.Room
import com.kakao.vectormap.KakaoMapSdk
import com.example.lendmark.R
import com.example.lendmark.data.local.LendMarkDatabase

class MyApp : Application() {

    companion object {
        lateinit var database: LendMarkDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()

        KakaoMapSdk.init(this, getString(R.string.kakao_native_app_key))

        // Room DB 싱글톤 초기화 --> 최근 본 강의실
        database = Room.databaseBuilder(
            applicationContext,
            LendMarkDatabase::class.java,
            "lendmark.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
