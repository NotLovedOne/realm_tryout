package com.example.realm_tryout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realm_tryout.models.Course
import com.example.realm_tryout.models.Student
import com.example.realm_tryout.models.Teacher
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainVM: ViewModel() {
    private val realm = MyApp.realm

    val courses = realm.query<Course>().asFlow().map { results ->
        results.list.toList()
    }.stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed()
    )
    var courseDetails: Course? by mutableStateOf(null)

    fun showCourseDetails(course: Course) {
        courseDetails = course
    }

    fun getCourseById(id: String): Course? {
        return realm.query<Course>("_id == oid($id)").first().find()
    }

    fun updateCourseName(id: String, ewName: String) {
        viewModelScope.launch {
            realm.write {
                val course = query<Course>("_id == oid($id)").first().find()
                course!!.name = ewName
            }

        }
    }

    fun hideCourseDetails() {
        courseDetails = null
    }

    fun addCourse(courseName: String, teacher: Teacher, students: List<Student>) {
        viewModelScope.launch {
            realm.write {
                val newCourse = Course().apply {
                    name = courseName
                    this.teacher = teacher
                    this.students.addAll(students)
                }
                copyToRealm(newCourse)
            }
        }
    }



    fun deleteCourse() {
        viewModelScope.launch {
            realm.write {
                val course = courseDetails ?: return@write
                val latestCourse = findLatest(course) ?: return@write
                delete(latestCourse)

                courseDetails = null
            }
        }
    }
}