package com.xaarlox.pw01.task04.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xaarlox.pw01.task04.logic.validateLogin
import com.xaarlox.pw01.task04.ui.components.AuthHeader
import com.xaarlox.pw01.task04.ui.components.AuthTextField
import com.xaarlox.pw01.task04.ui.components.ErrorMessage
import com.xaarlox.pw01.task04.ui.components.PrimaryButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Login screen: two fields (login, password) plus a submit button
 * and a link to switch to the registration screen
 *
 * @param onNavigateToRegister called when the user taps "Create account"
 */
@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // fieldErrors holds one message per field key ("login", "password"), shown inline under the corresponding AuthTextField
    var fieldErrors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var generalError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Center content and cap its width
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(modifier = Modifier.widthIn(max = 400.dp)) {
            AuthHeader(title = "Вхід в акаунт")

            AuthTextField(
                value = login,
                onValueChange = {
                    login = it
                    fieldErrors = fieldErrors - "login"
                },
                label = "Логін",
                errorText = fieldErrors["login"]
            )

            Spacer(Modifier.height(12.dp))

            AuthTextField(
                value = password,
                onValueChange = {
                    password = it
                    fieldErrors = fieldErrors - "password"
                },
                label = "Пароль",
                errorText = fieldErrors["password"],
                isPassword = true,
                keyboardType = KeyboardType.Password
            )

            generalError?.let { ErrorMessage(text = it) }

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = "Увійти",
                isLoading = isLoading,
                onClick = {
                    val result = validateLogin(login, password)
                    fieldErrors = result.fieldErrors
                    generalError = null

                    if (result.isValid) {
                        isLoading = true
                        // Simulates a network call
                        coroutineScope.launch {
                            delay(1200.milliseconds)
                            isLoading = false
                            generalError = "Вітаємо, $login!"
                        }
                    }
                }
            )

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("Немає акаунту? Зареєструватися")
            }
        }
    }
}