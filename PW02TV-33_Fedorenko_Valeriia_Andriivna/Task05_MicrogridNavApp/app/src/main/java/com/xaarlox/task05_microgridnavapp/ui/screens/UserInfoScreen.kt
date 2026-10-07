package com.xaarlox.task05_microgridnavapp.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xaarlox.task05_microgridnavapp.R
import com.xaarlox.task05_microgridnavapp.data.UserProfile
import com.xaarlox.task05_microgridnavapp.ui.components.AdaptiveLayout
import com.xaarlox.task05_microgridnavapp.ui.components.AppButton
import com.xaarlox.task05_microgridnavapp.ui.components.AppHeader
import com.xaarlox.task05_microgridnavapp.ui.components.ScreenContainer

/**
 * Profile settings screen: name and email with validation
 * Entered data survives screen rotation (rememberSaveable)
 * The "Save" button is disabled as long as the email is invalid
 *
 * @param initialProfile current profile data for initial field values
 * @param onSaveAndBack save (name, email) and navigate back
 */
@Composable
fun UserInfoScreen(
    initialProfile: UserProfile,
    onSaveAndBack: (name: String, email: String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(initialProfile.name) }
    var email by rememberSaveable { mutableStateOf(initialProfile.email) }

    val isEmailValid = email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    ScreenContainer {
        AdaptiveLayout(
            compact = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppHeader(text = stringResource(R.string.profile_title))
                    Spacer(Modifier.height(24.dp))
                    ProfileForm(
                        name = name,
                        onNameChange = { name = it },
                        email = email,
                        onEmailChange = { email = it },
                        isEmailValid = isEmailValid,
                        onSave = { onSaveAndBack(name, email) }
                    )
                }
            },
            wide = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppHeader(
                        text = stringResource(R.string.profile_title),
                        modifier = Modifier.weight(1f)
                    )
                    ProfileForm(
                        name = name,
                        onNameChange = { name = it },
                        email = email,
                        onEmailChange = { email = it },
                        isEmailValid = isEmailValid,
                        onSave = { onSaveAndBack(name, email) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        )
    }
}

/**
 * Profile form: name and email fields, and a save button
 *
 * @param name current name
 * @param onNameChange name change action
 * @param email current email
 * @param onEmailChange email change action
 * @param isEmailValid whether the email is valid (controls the error and button state)
 * @param onSave "Save and go back" click action
 * @param modifier external modifier
 */
@Composable
private fun ProfileForm(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    isEmailValid: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.name_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.email_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = !isEmailValid,
            supportingText = { if (!isEmailValid) Text(stringResource(R.string.email_error)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        AppButton(
            text = stringResource(R.string.save_and_back),
            onClick = onSave,
            enabled = isEmailValid
        )
    }
}