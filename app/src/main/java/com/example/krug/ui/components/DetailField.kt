package com.example.krug.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.ui.theme.KrugTheme

@Composable
fun DetailField(
    label: String,
    value: String?,
    icon: ImageVector? = null
) {
    val displayValue = value?.ifBlank { null } ?: "Не выбрано"
    val isMissing = value.isNullOrBlank()

    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (isMissing) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = displayValue,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isMissing) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "DetailField – заполненное")
@Composable
fun DetailFieldFilledPreview() {
    KrugTheme {
        DetailField("Местоположение", "ЦПКиО", Icons.Default.LocationOn)
    }
}

@Preview(showBackground = true, name = "DetailField – пустое")
@Composable
fun DetailFieldEmptyPreview() {
    KrugTheme {
        DetailField("Описание", null, Icons.Default.Description)
    }
}

@Preview(showBackground = true, name = "DetailField – без иконки")
@Composable
fun DetailFieldNoIconPreview() {
    KrugTheme {
        DetailField("Заметка", "Без иконки")
    }
}