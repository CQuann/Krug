package com.example.krug.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.EventColors
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerField(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Цвет"
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = EventColors.colors.find { it.hex == selectedColor }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected?.name ?: selectedColor,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(selectedColor.toColorInt()), shape = MaterialTheme.shapes.small)
                )
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            EventColors.colors.forEach { color ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(color.hex.toColorInt()), shape = MaterialTheme.shapes.small)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(color.name)
                        }
                    },
                    onClick = {
                        onColorSelected(color.hex)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true, name = "ColorPicker – выбран цвет")
@Composable
fun ColorPickerFieldPreviewSelected() {
    KrugTheme {
        ColorPickerField(
            selectedColor = "#3498DB",
            onColorSelected = {}
        )
    }
}