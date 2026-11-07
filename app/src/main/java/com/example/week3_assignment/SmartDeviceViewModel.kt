package com.example.week3_assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SmartDeviceUiState(
    val allDevices: List<SmartDevice> = emptyList(),
    val filteredDevices: List<SmartDevice> = emptyList(),
    val searchQuery: String = "",
    val selectedRoom: String = "Tümü",
    val availableRooms: List<String> = emptyList(),
    val totalActivePower: Double = 0.0
)

class SmartDeviceViewModel : ViewModel() {
    private val repository = SmartDeviceRepository()

    private val _uiState = MutableStateFlow(SmartDeviceUiState())
    val uiState: StateFlow<SmartDeviceUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.Default) {
            val devices = repository.getInitialDevices()
            val rooms = listOf("Tümü") + devices.map { it.room }.distinct()

            _uiState.update {
                it.copy(
                    allDevices = devices,
                    filteredDevices = devices,
                    availableRooms = rooms,
                    totalActivePower = calculateTotalPower(devices)
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterAndProcess()
    }

    fun onRoomSelected(room: String) {
        _uiState.update { it.copy(selectedRoom = room) }
        filterAndProcess()
    }

    fun toggleDeviceStatus(deviceId: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            val updatedList = _uiState.value.allDevices.map { device ->
                if (device.id == deviceId) device.copy(isActive = !device.isActive) else device
            }
            _uiState.update { it.copy(allDevices = updatedList) }
            filterAndProcess()
        }
    }

    private fun filterAndProcess() {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _uiState.value
            val query = currentState.searchQuery.trim().lowercase()
            val room = currentState.selectedRoom

            val filtered = currentState.allDevices.filter { device ->
                val matchesSearch = device.name.lowercase().contains(query)
                val matchesRoom = room == "Tümü" || device.room == room
                matchesSearch && matchesRoom
            }

            val activePower = calculateTotalPower(currentState.allDevices)

            _uiState.update {
                it.copy(
                    filteredDevices = filtered,
                    totalActivePower = activePower
                )
            }
        }
    }

    private fun calculateTotalPower(devices: List<SmartDevice>): Double {
        return devices.filter { it.isOnline && it.isActive }.sumOf { it.powerConsumption }
    }
}