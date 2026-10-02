package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculadora.ui.theme.CalculadoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: PropinaViewModel = viewModel()
                    val state by viewModel.uiState.collectAsStateWithLifecycle()
                    PantallaPropinas(
                        state = state,
                        onMontoChange = viewModel::onMontoChange,
                        onPorcentajeChange = viewModel::onPorcentajeChange,
                        onPersonasChange = viewModel::onPersonasChange,
                        onRedondearChange = viewModel::onRedondearChange,
                        onLimpiarClick = viewModel::onLimpiarClick,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

// composable principal, para mostrar toda la estructura de la pantalla
@Composable
fun PantallaPropinas(
    state: PropinaUiState,
    onMontoChange: (String) -> Unit,
    onPorcentajeChange: (Float) -> Unit,
    onPersonasChange: (Int) -> Unit,
    onRedondearChange: (Boolean) -> Unit,
    onLimpiarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.titulo_app),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        CampoMonto(
            monto = state.monto,
            hayError = state.hayError,
            onMontoChange = onMontoChange,
        )

        SelectorPorcentaje(
            porcentaje = state.porcentaje,
            onPorcentajeChange = onPorcentajeChange,
        )

        SelectorPersonas(
            personas = state.personas,
            onPersonasChange = onPersonasChange,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.label_redondear),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            Switch(
                checked = state.redondear,
                onCheckedChange = onRedondearChange,
            )
        }

        Resultados(state = state)

        Button(
            onClick = onLimpiarClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.btn_limpiar))
        }
    }
}

// composable para la entrada del monto (de la cuenta)
@Composable
fun CampoMonto(
    monto: String,
    hayError: Boolean,
    onMontoChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = monto,
        onValueChange = onMontoChange,
        label = { Text(text = stringResource(R.string.label_monto)) },
        leadingIcon = { Text(text = stringResource(R.string.simbolo_moneda)) },
        isError = hayError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.fillMaxWidth(),
    )
}

// composable para seleccionar el porcentaje con el slider y filterChips
@Composable
fun SelectorPorcentaje(
    porcentaje: Float,
    onPorcentajeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.label_porcentaje, porcentaje.toInt()),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
        Slider(
            value = porcentaje,
            onValueChange = onPorcentajeChange,
            valueRange = 0f..30f,
            steps = 29,
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
        ) {
            FilterChip(
                selected = (porcentaje == 10f),
                onClick = { onPorcentajeChange(10f) },
                label = { Text(text = stringResource(R.string.chip_10)) },
            )
            FilterChip(
                selected = (porcentaje == 15f),
                onClick = { onPorcentajeChange(15f) },
                label = { Text(text = stringResource(R.string.chip_15)) },
            )
            FilterChip(
                selected = (porcentaje == 20f),
                onClick = { onPorcentajeChange(20f) },
                label = { Text(text = stringResource(R.string.chip_20)) },
            )
        }
    }
}

// composable para seleccionar el núm de personas
@Composable
fun SelectorPersonas(
    personas: Int,
    onPersonasChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.label_personas),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onPersonasChange(personas - 1) },
                enabled = (personas > 1),
            ) {
                Text(text = stringResource(R.string.btn_menos), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = personas.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
            IconButton(
                onClick = { onPersonasChange(personas + 1) },
            ) {
                Text(text = stringResource(R.string.btn_mas), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// composable que muestra el desglose de resultados (en una Card dinámica) >.<
@Composable
fun Resultados(
    state: PropinaUiState,
    modifier: Modifier = Modifier,
) {
    val (fondoColor, textoColor) = when {
        state.hayError -> Pair(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
        state.porcentaje < 10f -> Pair(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        state.porcentaje <= 20f -> Pair(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        else -> Pair(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = fondoColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.titulo_resultados),
                style = MaterialTheme.typography.titleMedium,
                color = textoColor,
                fontWeight = FontWeight.Bold,
            )
            if (state.hayError) {
                Text(
                    text = stringResource(R.string.error_monto_invalido),
                    color = textoColor,
                    style = MaterialTheme.typography.bodyLarge,
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.label_propina_total), color = textoColor)
                    Text(
                        text = stringResource(R.string.formato_moneda, state.propina),
                        color = textoColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.label_total_cuenta), color = textoColor)
                    Text(
                        text = stringResource(R.string.formato_moneda, state.total),
                        color = textoColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.label_por_persona), color = textoColor)
                    Text(
                        text = stringResource(R.string.formato_moneda, state.porPersona),
                        color = textoColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}