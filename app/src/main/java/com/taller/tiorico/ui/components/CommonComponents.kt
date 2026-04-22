package com.taller.tiorico.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taller.tiorico.model.Transaction
import com.taller.tiorico.model.TransactionType
import com.taller.tiorico.ui.theme.GoldDark
import java.text.NumberFormat
import java.util.*

@Composable
fun RicoCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val icon: ImageVector
    val color: Color
    
    when (transaction.type) {
        TransactionType.SAVE -> {
            icon = Icons.Default.Add
            color = Color(0xFF2ECC71)
        }
        TransactionType.INVEST -> {
            if (transaction.amount > 0) {
                icon = Icons.Default.ThumbUp
                color = Color(0xFF2ECC71)
            } else {
                icon = Icons.Default.Warning
                color = Color(0xFFE74C3C)
            }
        }
        TransactionType.SPEND -> {
            icon = Icons.Default.Close
            color = Color(0xFFE74C3C)
        }
        TransactionType.EVENT -> {
            icon = Icons.Default.Favorite
            color = GoldDark
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = transaction.description, fontWeight = FontWeight.Bold)
            Text(
                text = java.text.SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(transaction.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        Text(
            text = "${if (transaction.amount > 0) "+" else ""}${format.format(transaction.amount)}",
            fontWeight = FontWeight.Black,
            color = if (transaction.amount >= 0) Color(0xFF2ECC71) else Color(0xFFE74C3C)
        )
    }
}
