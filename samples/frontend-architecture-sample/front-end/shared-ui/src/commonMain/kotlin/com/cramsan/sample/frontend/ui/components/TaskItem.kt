package com.cramsan.sample.frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cramsan.sample.frontend.shared.domain.entities.Task
import com.cramsan.sample.frontend.shared.domain.entities.TaskPriority
import com.cramsan.sample.frontend.ui.theme.TaskColors
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * A reusable task item component that displays task information and handles interactions.
 * Demonstrates proper component design with clear props and callbacks.
 */
@Composable
fun TaskItem(
    task: Task,
    onTaskClick: (Task) -> Unit,
    onToggleCompletion: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTaskClick(task) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Completion checkbox
            IconButton(
                onClick = { onToggleCompletion(task) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (task.isCompleted) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = if (task.isCompleted) {
                        "Mark as incomplete"
                    } else {
                        "Mark as complete"
                    },
                    tint = if (task.isCompleted) {
                        TaskColors.CompletedTask
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Task content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textDecoration = if (task.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Description (if not empty)
                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Priority and status indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority indicator
                    PriorityChip(priority = task.priority)

                    // Overdue indicator
                    if (task.isOverdue(Clock.System.now())) {
                        OverdueChip()
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityChip(priority: TaskPriority) {
    val backgroundColor = when (priority) {
        TaskPriority.LOW -> TaskColors.LowPriority
        TaskPriority.MEDIUM -> TaskColors.MediumPriority
        TaskPriority.HIGH -> TaskColors.HighPriority
        TaskPriority.URGENT -> TaskColors.UrgentPriority
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = priority.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = backgroundColor
        )
    }
}

@Composable
private fun OverdueChip() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(TaskColors.OverdueTask.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Overdue",
            style = MaterialTheme.typography.labelSmall,
            color = TaskColors.OverdueTask
        )
    }
}
