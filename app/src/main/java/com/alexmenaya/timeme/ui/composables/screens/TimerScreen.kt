package com.alexmenaya.timeme.ui.composables.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexmenaya.timeme.data.Task
import com.alexmenaya.timeme.timer.TimerStateCalculator
import com.alexmenaya.timeme.timer.TimerStateHolder
import com.alexmenaya.timeme.ui.theme.TimeMeTheme

@Composable
fun TimerScreen(
    timerValue: String,
    activeTask: Task?,
    startTimer: () -> Unit,
    stopTimer: () -> Unit,
    setTaskName: (String) -> Unit,
    listOfTasks: List<Task>,
    listOfFavorites: List<Task>,
    testing: () -> Unit
) {
    val isTimerActive = activeTask != null
    var favoritesExpanded by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isTimerActive) {
            TimerDisplay(
                activeTask = activeTask!!,
                timerValue = timerValue,
                stopTimer = stopTimer
            )
        } else {
            InputField(
                startTimer = startTimer,
                setTaskName = setTaskName
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (listOfFavorites.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .clickable { favoritesExpanded = !favoritesExpanded }
            ) {
                Icon(
                    imageVector = if (favoritesExpanded) {Icons.Filled.ArrowDropDown} else {Icons.Filled.ArrowDropUp},
                    contentDescription = null
                )
                Text(
                    text = "Favorite tasks"
                )
            }
            if (favoritesExpanded) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(listOfFavorites.size){
                        val task = listOfFavorites[it]
                        //TimeEntry(task = task)
                        Text("Here comes the favorites tasks")
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp)
        ) {
            Box(modifier = Modifier.size(24.dp))
            Text(text = "History")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(listOfTasks.size){
                val task = listOfTasks[it]
                TimeEntry(task = task)
            }
        }
        Button(
            onClick = {testing() },
            modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Delete DB")
        }
    }
}

@Composable
fun TimerDisplay(
    activeTask: Task,
    timerValue: String,
    stopTimer: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(text = activeTask.task_name)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = activeTask.id_project)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = timerValue,
                fontSize = 48.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = stopTimer
            ) {
                Icon(
                    imageVector = Icons.Filled.Stop,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
fun InputField(
    startTimer: () -> Unit,
    setTaskName: (String) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ProjectIcon()
        var content by remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            value = content,
            onValueChange = {
                content = it
            },
            label = { Text(text = "Next task!") },
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {
                if (content.text.isNotBlank()) {
                    setTaskName(content.text)
                    startTimer()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun TimeEntry(
    task: Task
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Circle,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = task.task_name, modifier = Modifier.weight(1f))
        val taskTime = TimerStateCalculator.format((task.time_ended - task.time_started)*1000)
        Text(text = taskTime)
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
        }
    }

    Column() {
        Text(text = "${task.task_name}\n Start: ${task.time_started}\n End: ${task.time_ended}")
        Text(text = (System.currentTimeMillis()/1000).toString())
    }
}

@Composable
fun ProjectIcon(
    color: Color = Color.Black
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.clickable { expanded = !expanded}
    ) {
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            Icon(
                imageVector = Icons.Filled.Circle,
                contentDescription = null,
                tint = color
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                       Text(text = "Set up new project")
                },
                onClick = { /* Create a project dialog? */ }
            )
            val listOfProjects = listOf("Project1", "Project2", "Project3")
            listOfProjects.forEach { project ->
                DropdownMenuItem(
                    text = { Text(text = project) },
                    onClick = { expanded = false }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    val now = System.currentTimeMillis()/1000
    val activeTask = Task("test", date_created = now,
        date_updated = now,
        is_deleted = false,
        id_owner = "Alex",
        task_name = "TestTask",
        id_project = "None",
        time_started = now,
        time_ended = now + 1000
    )
    TimeMeTheme {
        TimerScreen("00:00:00", null, {},{}, {}, listOf(activeTask), listOf(), {})
    }
}

@Preview
@Composable
fun EntryPreview(){
    val now = System.currentTimeMillis()/1000
    val activeTask = Task("test", date_created = now,
        date_updated = now,
        is_deleted = false,
        id_owner = "Alex",
        task_name = "TestTask",
        id_project = "None",
        time_started = now,
        time_ended = 0
    )
    TimeMeTheme {
        TimeEntry(activeTask)
    }
}