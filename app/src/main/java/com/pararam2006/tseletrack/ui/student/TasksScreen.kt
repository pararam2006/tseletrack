package com.pararam2006.tseletrack.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.domain.student.StudentTask
import com.pararam2006.tseletrack.ui.common.AppCard
import com.pararam2006.tseletrack.ui.common.AppLoadingScreen
import com.pararam2006.tseletrack.ui.common.AppScreenHeader
import com.pararam2006.tseletrack.ui.theme.tseletrackBg
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackLine
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackOk
import com.pararam2006.tseletrack.ui.theme.tseletrackRed
import com.pararam2006.tseletrack.ui.theme.tseletrackWhite

@Composable
internal fun TasksScreen(
    isLoading: Boolean,
    tasks: List<StudentTask>,
    onTaskClick: (String) -> Unit,
    onAddTask: () -> Unit,
) {
    if (isLoading) {
        AppLoadingScreen(title = stringResource(R.string.tasks_title))
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tseletrackBg),
    ) {
        AppScreenHeader(title = stringResource(R.string.tasks_title))
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                tasks.forEach { task ->
                    TaskCard(
                        task = task,
                        onClick = { onTaskClick(task.id) },
                    )
                }
            }
            AddTaskButton(
                onClick = onAddTask,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: StudentTask,
    onClick: () -> Unit,
) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .toggleable(
                value = task.isCompleted,
                role = Role.Checkbox,
                onValueChange = { onClick() },
            ),
        contentPadding = 14.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            TaskCheckbox(isChecked = task.isCompleted)
            Text(
                text = task.title,
                color = if (task.isCompleted) tseletrackMut else tseletrackInk,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun TaskCheckbox(isChecked: Boolean) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .then(
                if (isChecked) {
                    Modifier.background(tseletrackOk, RoundedCornerShape(5.dp))
                } else {
                    Modifier.border(1.dp, tseletrackMut, RoundedCornerShape(5.dp))
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isChecked) {
            Text(
                text = "✓",
                color = tseletrackWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun AddTaskButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(R.string.tasks_add)
    Box(
        modifier = modifier
            .size(58.dp)
            .background(tseletrackRed, RoundedCornerShape(17.dp))
            .semantics { contentDescription = label }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+",
            color = tseletrackWhite,
            fontSize = 30.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Light,
        )
    }
}
