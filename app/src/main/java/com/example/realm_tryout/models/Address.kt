package com.example.realm_tryout.models

import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

//teacher - address
//teacher 1-to many Course
//Students many- to many Course

class Address: EmbeddedRealmObject {
    var fullName: String = ""
    var street: String = ""
    var houseNumber: Int = 0
    var zipCode: Int = 0
    var city: String = ""
    var teacher: Teacher? = null
}

class Teacher: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var address: Address? = null
    var courses: RealmList<Course> = realmListOf()
}

class Course: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var name: String = ""
    var teacher: Teacher? = null
    var students: RealmList<Student> = realmListOf()
}

class Student: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var name: String = ""
    val courses: RealmResults<Course> by backlinks(Course::students)
}
