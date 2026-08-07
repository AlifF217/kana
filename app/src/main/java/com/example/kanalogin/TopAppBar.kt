import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset

@Composable
fun TopAppBarWithKebabMenu(onLogout: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopEnd)) {
        // Align the IconButton to the top-right
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Kebab Menu"
            )
        }

        // Ensure that the dropdown menu aligns with the icon button in the top-right corner
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.align(Alignment.TopEnd)
                  // Align to the top-right corner of the parent Box
                // Adjust the vertical position if needed
        ) {
            DropdownMenuItem(
                text = {
                    Box(modifier = Modifier
                        .fillMaxWidth()) {
                        Text(
                            text = "Settings",
                            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                        )
                    }
                },
                onClick = {
                    expanded = false
                    // Handle settings click
                },
                leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenuItem(
                text = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Feedback",
                            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                        )
                    }
                },
                onClick = {
                    expanded = false
                    // Handle feedback click
                },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenuItem(
                text = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Share Progress",
                            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                        )
                    }
                },
                onClick = {
                    expanded = false
                    // Implement sharing progress functionality
                },
                leadingIcon = { Icon(Icons.Outlined.Share, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Logout",
                            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                        )
                    }
                },
                onClick = {
                    onLogout() // Trigger logout action
                    expanded = false
                },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}



@Preview
@Composable
fun TopAppBarPreview() {
    TopAppBarWithKebabMenu(onLogout = { /* Handle logout */ })
}
