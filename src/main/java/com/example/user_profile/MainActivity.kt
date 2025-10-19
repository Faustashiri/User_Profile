package com.example.user_profile

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.user_profile.ui.theme.User_ProfileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var darkTheme by rememberSaveable { mutableStateOf(false) } // <-- переключатель темы
            User_ProfileTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UserForm(
                        darkTheme = darkTheme,
                        onThemeToggle = { darkTheme = it }
                    )
                }
            }
        }
    }
}

@Composable
fun UserForm(
    darkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf(18f) }
    var gender by rememberSaveable { mutableStateOf("Мужской") }
    var subscribed by rememberSaveable { mutableStateOf(false) }
    var showSummary by rememberSaveable { mutableStateOf(false) }
    var imageUri by rememberSaveable { mutableStateOf<String?>(null) }

    // Лаунчер для выбора изображения из галереи
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        imageUri = uri?.toString()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Переключатель темы
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (darkTheme) "Тёмная тема" else "Светлая тема")
            Switch(
                checked = darkTheme,
                onCheckedChange = onThemeToggle,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Аватар
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                val bitmap = remember(imageUri) {
                    val stream = context.contentResolver.openInputStream(imageUri!!.toUri())
                    BitmapFactory.decodeStream(stream)
                }
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = R.drawable.images),
                    contentDescription = "Default avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }

        // Кнопка для выбора изображения
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Выбрать аватар")
        }

        // Имя
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.enter_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Возраст
        Text(text = stringResource(R.string.age_label, age.toInt()))
        Slider(
            value = age,
            onValueChange = { age = it },
            valueRange = 1f..100f,
            steps = 99,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Пол
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "${stringResource(R.string.gender_label)} ", modifier = Modifier.padding(end = 8.dp))

            RadioButton(
                selected = gender == "Мужской",
                onClick = { gender = "Мужской" }
            )
            Text(text = stringResource(R.string.male), modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = gender == "Женский",
                onClick = { gender = "Женский" }
            )
            Text(text = stringResource(R.string.female))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Подписка
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = subscribed,
                onCheckedChange = { subscribed = it }
            )
            Text(stringResource(R.string.subscribe))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка отправки
        Button(
            onClick = { showSummary = true },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.send_button))
        }

        if (name.isBlank()) {
            Text(
                text = stringResource(R.string.name_empty_error),
                color = MaterialTheme.colorScheme.error
            )
        }

        if (showSummary) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(stringResource(R.string.summary_title))
            Text("Имя: $name")
            Text("Возраст: ${age.toInt()}")
            Text("Пол: $gender")
            Text("Подписка: ${if (subscribed) "Да ✅" else "Нет ❌"}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLight() {
    User_ProfileTheme(darkTheme = false) {
        UserForm(darkTheme = false, onThemeToggle = {})
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDark() {
    User_ProfileTheme(darkTheme = true) {
        UserForm(darkTheme = true, onThemeToggle = {})
    }
}
