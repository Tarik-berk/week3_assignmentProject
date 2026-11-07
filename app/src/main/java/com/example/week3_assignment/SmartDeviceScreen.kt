package com.example.week3_assignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SmartDeviceScreen(viewModel: SmartDeviceViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ActivePowerCounterCard(totalPower = uiState.totalActivePower)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cihaz adı ara...") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            RoomFilterRow(
                rooms = uiState.availableRooms,
                selectedRoom = uiState.selectedRoom,
                onRoomSelected = { viewModel.onRoomSelected(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.filteredDevices, key = { it.id }) { device ->
                    DeviceItemCard(
                        device = device,
                        onToggleClick = { viewModel.toggleDeviceStatus(device.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ActivePowerCounterCard(totalPower: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Anlık Toplam Enerji Tüketimi", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "%.1f Watt".format(totalPower),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun RoomFilterRow(rooms: List<String>, selectedRoom: String, onRoomSelected: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(rooms) { room ->
            FilterChip(
                selected = room == selectedRoom,
                onClick = { onRoomSelected(room) },
                label = { Text(room) }
            )
        }
    }
}

@Composable
fun DeviceItemCard(device: SmartDevice, onToggleClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = device.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Oda: ${device.room} | Kategori: ${device.category}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(2.dp))
                val statusText = if (device.isOnline) "Online" else "Offline"
                Text(text = "Durum: $statusText • Tüketim: ${device.powerConsumption} W", style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = onToggleClick,
                enabled = device.isOnline,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (device.isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (device.isActive) "Kapat" else "Aç")
            }
        }
    }
}