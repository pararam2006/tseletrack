package com.pararam2006.tseletrack.ui.student

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.domain.student.StudyField
import com.pararam2006.tseletrack.domain.student.University
import com.pararam2006.tseletrack.ui.common.AppCard
import com.pararam2006.tseletrack.ui.common.AppLoadingScreen
import com.pararam2006.tseletrack.ui.common.AppProgressBar
import com.pararam2006.tseletrack.ui.common.AppScreenHeader
import com.pararam2006.tseletrack.ui.theme.tseletrackBg
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackLine
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackWhite

@Composable
internal fun UniversitiesScreen(
    isLoading: Boolean,
    universities: List<University>,
    searchQuery: String,
    selectedField: StudyField,
    onSearchQueryChange: (String) -> Unit,
    onFieldSelected: (StudyField) -> Unit,
    onChooseUniversity: (String) -> Unit,
) {
    if (isLoading) {
        AppLoadingScreen(title = stringResource(R.string.universities_title))
        return
    }

    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tseletrackBg),
    ) {
        AppScreenHeader(title = stringResource(R.string.universities_title))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                UniversitySearchField(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StudyField.entries.forEach { field ->
                        StudyFieldChip(
                            field = field,
                            isSelected = field == selectedField,
                            onClick = { onFieldSelected(field) },
                        )
                    }
                }
            }
            items(
                items = universities,
                key = University::id,
            ) { university ->
                UniversityCard(
                    university = university,
                    onChoose = { onChooseUniversity(university.id) },
                )
            }
        }
    }
}

@Composable
private fun UniversitySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(tseletrackWhite, RoundedCornerShape(21.dp))
            .border(1.dp, tseletrackLine, RoundedCornerShape(21.dp)),
        textStyle = TextStyle(
            color = tseletrackInk,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        ),
        cursorBrush = SolidColor(tseletrackInk),
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SearchIcon()
                Spacer(Modifier.width(9.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.universities_search),
                            color = tseletrackMut,
                            fontSize = 13.sp,
                        )
                    }
                    innerTextField()
                }
            }
        },
    )
}

@Composable
private fun SearchIcon() {
    val color = tseletrackMut
    Canvas(Modifier.size(16.dp)) {
        drawCircle(
            color = color,
            radius = 4.5.dp.toPx(),
            center = Offset(6.5.dp.toPx(), 6.5.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx()),
        )
        drawLine(
            color = color,
            start = Offset(10.dp.toPx(), 10.dp.toPx()),
            end = Offset(14.dp.toPx(), 14.dp.toPx()),
            strokeWidth = 1.5.dp.toPx(),
        )
    }
}

@Composable
private fun StudyFieldChip(
    field: StudyField,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(28.dp)
            .background(
                color = if (isSelected) tseletrackInk else tseletrackWhite,
                shape = RoundedCornerShape(18.dp),
            )
            .border(1.dp, tseletrackLine, RoundedCornerShape(18.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = field.title,
            color = if (isSelected) tseletrackWhite else tseletrackInk,
        fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun UniversityCard(
    university: University,
    onChoose: () -> Unit,
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 16.dp,
    ) {
        Text(
            text = university.name,
            color = tseletrackInk,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = university.fields,
            color = tseletrackMut,
            fontSize = 12.sp,
            lineHeight = 17.sp,
        )
        Spacer(Modifier.height(14.dp))
        AppProgressBar(percent = university.matchPercent)
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.universities_match, university.matchPercent),
            color = tseletrackMut,
            fontSize = 11.sp,
            lineHeight = 15.sp,
        )
        Spacer(Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .size(width = 92.dp, height = 34.dp)
                .border(1.dp, tseletrackLine, RoundedCornerShape(10.dp))
                .clickable(role = Role.Button, onClick = onChoose),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.universities_choose),
                color = tseletrackInk,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
