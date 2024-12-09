package io.github.taalaydev.drivesafe.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.taalaydev.drivesafe.domain.model.LessonImportance

@Composable
fun ImportanceBadge(importance: LessonImportance) {
    val (text, color) = when (importance) {
        LessonImportance.ESSENTIAL -> "Обязательно" to Color(0xFFE53935)
        LessonImportance.IMPORTANT -> "Важно" to Color(0xFFFF9800)
        LessonImportance.BASIC -> "Базовый" to Color(0xFF4CAF50)
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}