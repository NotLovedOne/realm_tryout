package com.example.realm_tryout

import android.app.Application
import com.example.realm_tryout.models.Address
import com.example.realm_tryout.models.Course
import com.example.realm_tryout.models.Student
import com.example.realm_tryout.models.Teacher
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MyApp: Application() {
    companion object {
        lateinit var realm: Realm
    }
    override fun onCreate() {
        super.onCreate()
        realm = Realm.open(
            RealmConfiguration.create(
                schema = setOf(
                    Address::class,
                    Teacher::class,
                    Student::class,
                    Course::class
                )
            )
        )
    }
}