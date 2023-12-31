package com.cramsan.stranded.cardmanager.nightcards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.cramsan.stranded.cardmanager.base.StatementList
import com.cramsan.stranded.cardmanager.base.TabFrame

/**
 * Render the content inside the Night Cards tab. The state will be provided by the [viewModel].
 */
@Suppress("LongMethod")
@Composable
fun NightCardsTab(
    viewModel: NightCardManagerViewModel,
) {
    val title = viewModel.cardTitle.collectAsState()
    val selectedIndex = viewModel.selectedCardIndex.collectAsState()
    val selectedStatementIndex = viewModel.selectedStatementIndex.collectAsState()
    val quantity = viewModel.cardQuantity.collectAsState()
    val cardDeck = viewModel.deck.collectAsState()
    val statement = viewModel.statement.collectAsState()
    val statementList = viewModel.statementList.collectAsState()
    val argument1Label = viewModel.argument1Label.collectAsState()
    val argument1Field = viewModel.argument1Field.collectAsState()
    val argument2Label = viewModel.argument2Label.collectAsState()
    val argument2Field = viewModel.argument2Field.collectAsState()

    TabFrame(selectedIndex.value, cardDeck.value, viewModel) {
        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = title.value,
                label = { Text(text = "Card title") },
                onValueChange = { viewModel.onTitleFieldUpdated(it) },
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = quantity.value.toString(),
                label = { Text(text = "Card quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { viewModel.onQuantityTitleUpdated(it) },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Button(
                        onClick = { viewModel.onAddStatementSelected() },
                    ) {
                        Text("Add")
                    }
                    Button(
                        onClick = { viewModel.onRemoveStatementSelected() },
                    ) {
                        Text("Remove")
                    }
                }
                StatementList(
                    modifier = Modifier.weight(2f),
                    selectedIndex = selectedStatementIndex.value,
                    changeList = statementList.value,
                    onChangeSelected = { index -> viewModel.onStatementAtIndexSelected(index) },
                )
                Column(
                    modifier = Modifier.weight(2f),
                ) {
                    Text("Statement type")
                    var expanded by remember { mutableStateOf(false) }
                    val dropDownEnabled = selectedStatementIndex.value >= 0

                    if (dropDownEnabled) {
                        Text(
                            statement.value?.javaClass?.simpleName ?: "",
                            modifier = Modifier.fillMaxWidth()
                                .background(Color.Gray)
                                .clickable { expanded = true },
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            NightCardManagerViewModel.STATEMENT_TYPES.forEachIndexed { index, type ->
                                DropdownMenuItem(onClick = {
                                    viewModel.onStatementTypeIndexSelected(index)
                                    expanded = false
                                },) {
                                    Text(text = type)
                                }
                            }
                        }
                    }
                    val label1 = argument1Label.value
                    if (label1 != null) {
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = argument1Field.value,
                            label = { Text(text = label1) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            onValueChange = { viewModel.onArgument1FieldUpdated(it) },
                        )
                    }
                    val label2 = argument2Label.value
                    if (label2 != null) {
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = argument2Field.value,
                            label = { Text(text = label2) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            onValueChange = { viewModel.onArgument2FieldUpdated(it) },
                        )
                    }
                }
            }
        }
    }
}
