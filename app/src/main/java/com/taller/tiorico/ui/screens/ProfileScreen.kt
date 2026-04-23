package com.taller.tiorico.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taller.tiorico.ui.components.RicoCard
import com.taller.tiorico.ui.components.SectionHeader
import com.taller.tiorico.ui.viewmodel.GameViewModel

@Composable
fun ProfileScreen(viewModel: GameViewModel) {
    val player by viewModel.playerState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Avatar
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = player.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Nivel: Magnate en potencia",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        SectionHeader("Ajustes de Cuenta")

        ProfileOption("Configuración de Perfil", Icons.Default.Settings)
        ProfileOption("Preferencias de Notificaciones", Icons.Default.Notifications)
        ProfileOption("Modo Oscuro", Icons.Default.DarkMode, hasSwitch = true)
        ProfileOption("Cerrar Sesión", Icons.Default.ExitToApp, color = Color(0xFFE74C3C))

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Tío Rico App v1.0.0",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 24.dp),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun ProfileOption(
    title: String,
    icon: ImageVector,
    hasSwitch: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        onClick = { /* Action */ },
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = color
            )
            if (hasSwitch) {
                Switch(checked = true, onCheckedChange = {})
            } else {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                )
            }
        }
    }
}
