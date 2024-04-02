package com.example.realm_tryout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.realm_tryout.models.Course
import com.example.realm_tryout.ui.theme.Realm_tryoutTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Realm_tryoutTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "start") {
                    composable("start") { CourseList(navController = navController) }
                    composable("edit/{courseId}") { backStackEntry ->
                        val courseId = backStackEntry.arguments.let {
                            it?.getString("courseId")
                        }

                        println("YERARARARARARARA   $courseId")
                        if(courseId != null) {
                            EditCourse(
                                navController = navController,
                                courseId = courseId,
                            )
                        }

                    }
                }
            }

        }
    }
}


@Composable
fun EditCourse(
    navController: NavController,
    viewModel: MainVM = MainVM(),
    courseId: String
) {

    val course = viewModel.getCourseById(courseId)
    
    val courseName = remember { mutableStateOf(course?.name) }

    
    
    Text(text = "Edit Course")
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = courseName.value!!,
            onValueChange = { newValue ->
                courseName.value = newValue
            },
            label = { Text(text = "Course name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )


        Button(onClick = {
            viewModel.updateCourseName(courseId,courseName.value!!)
            navController.navigate("start")
        }, modifier = Modifier.padding(16.dp)) {
            Text(text = "Save")
        }
    }
}



@Composable
fun CourseList(
    navController: NavController,
    viewModel: MainVM = MainVM(),
) {
    val courses by viewModel.courses.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(courses) { course ->
                CourseItem(
                    course = course,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            viewModel.showCourseDetails(course)
                        }
                )
            }
        }

        if(viewModel.courseDetails != null) {
            Dialog(onDismissRequest = viewModel::hideCourseDetails) {
                Column(
                    modifier = Modifier
                        .widthIn(200.dp, 300.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    viewModel.courseDetails?.teacher?.address?.let { address ->
                        Text(text = address.fullName)
                        Text(text = address.street + " " + address.houseNumber)
                        Text(text = address.zipCode.toString() + " " + address.city)
                    }
                    Button(onClick = {
                        navController.navigate("edit/${viewModel.courseDetails?._id?.toHexString()}")

                    }) {
                        Text(text = "Edit")
                    }
                    Button(
                        onClick = viewModel::deleteCourse,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }
}



@Composable
fun CourseItem(
    course: Course,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = course._id.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize
        )

        Text(
            text = course.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize
        )

        Text(
            text = "Held by ${course.teacher?.address?.fullName}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Light,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enrolled: ${course.students.joinToString { it.name }}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Light,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        )
    }
}