package com.example.week3_assignment

enum class DeviceCategory {
    LIGHT, CAMERA, THERMOSTAT, VACUUM
}

data class SmartDevice(
    val id: Int,
    val name: String,
    val category: DeviceCategory,
    val room: String,
    val isOnline: Boolean,
    val isActive: Boolean,
    val powerConsumption: Double
)