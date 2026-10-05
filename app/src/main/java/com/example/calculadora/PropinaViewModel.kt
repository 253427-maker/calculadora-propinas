package com.example.calculadora

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.ceil

// es el estado inmutable de la pantalla de la calculadora de propinas
data class PropinaUiState(
    val monto: String = "",
    val porcentaje: Float = 15f,
    val personas: Int = 1,
    val redondear: Boolean = false,
    val propina: Double = 0.0,
    val total: Double = 0.0,
    val porPersona: Double = 0.0,
    val hayError: Boolean = false,
)

// ViewModel que contiene toda la lógica y matemática de las propinas
class PropinaViewModel : ViewModel() {

    // backing property para exponer el estado inmutable a la UI
    private val _uiState = MutableStateFlow(PropinaUiState())
    val uiState: StateFlow<PropinaUiState> = _uiState.asStateFlow()

    // act el monto ingresado por el usuario y recalcula los totales
    fun onMontoChange(nuevoMonto: String) {
        _uiState.update { it.copy(monto = nuevoMonto) }
        recalcular()
    }

    // act el porcentaje de propina seleccionado y recalcula
    fun onPorcentajeChange(nuevoPorcentaje: Float) {
        _uiState.update { it.copy(porcentaje = nuevoPorcentaje) }
        recalcular()
    }

    // act el número de personas garantizando un mínimo de 1
    fun onPersonasChange(nuevasPersonas: Int) {
        val personasValidas = nuevasPersonas.coerceAtLeast(1)
        _uiState.update { it.copy(personas = personasValidas) }
        recalcular()
    }

    // activa o desactiva la opción de redondear el total
    fun onRedondearChange(nuevoRedondear: Boolean) {
        _uiState.update { it.copy(redondear = nuevoRedondear) }
        recalcular()
    }

    // reinicia todos los datos al estado inicial
    fun onLimpiarClick() {
        _uiState.update { PropinaUiState() }
    }

    // es para validar el monto e invoca los cálculos de propina y totales
    private fun recalcular() {
        val estado = _uiState.value
        val montoNum = estado.monto.toDoubleOrNull()
        if ((montoNum == null) || (montoNum <= 0.0)) {
            _uiState.update { it.copy(hayError = true, propina = 0.0, total = 0.0, porPersona = 0.0) }
        } else {
            val (prop, tot, porPers) = calcularTotales(montoNum, estado.porcentaje, estado.personas, estado.redondear)
            _uiState.update { it.copy(propina = prop, total = tot, porPersona = porPers, hayError = false) }
        }
    }

    // realiza los cálculos matemáticos de la propina y división por persona
    private fun calcularTotales(monto: Double, porc: Float, pers: Int, red: Boolean): Triple<Double, Double, Double> {
        val rawPropina = monto * (porc / 100.0)
        val totalCalculado = if (red) ceil(monto + rawPropina) else (monto + rawPropina)
        val propinaFinal = if (red) totalCalculado - monto else rawPropina
        val porPersonaCalculado = totalCalculado / pers
        return Triple(propinaFinal, totalCalculado, porPersonaCalculado)
    }
}
