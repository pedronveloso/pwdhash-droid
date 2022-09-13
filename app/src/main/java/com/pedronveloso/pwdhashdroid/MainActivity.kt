package com.pedronveloso.pwdhashdroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedronveloso.pwdhashdroid.hash.HashedPassword
import com.pedronveloso.pwdhashdroid.ui.theme.PwdHashDroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PwdHashDroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MainInputWithStateHoisting()
                }
            }
        }
    }
}

@Composable
fun MainInputWithStateHoisting() {
    var siteAddress by remember { mutableStateOf("") }
    var sitePassword by remember { mutableStateOf("") }
    var generatedHash by remember { mutableStateOf("") }

    MainInputArea(
        siteAddress = siteAddress,
        onSiteAddressChange = { siteAddress = it },
        sitePassword = sitePassword,
        onSidePasswordChange = { sitePassword = it },
        generatedHash = generatedHash,
        onGeneratedHashChange = { generatedHash = it },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainInputArea(
    siteAddress: String,
    onSiteAddressChange: (String) -> Unit,
    sitePassword: String,
    onSidePasswordChange: (String) -> Unit,
    generatedHash: String,
    onGeneratedHashChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(),
        horizontalAlignment  =  Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center) {
        val focus = LocalFocusManager.current
        // Site Address.
        OutlinedTextField(
            value = siteAddress,
            onValueChange = onSiteAddressChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next, autoCorrect = false),
            keyboardActions = KeyboardActions(
                onNext = { focus.moveFocus(FocusDirection.Next) }
            ),
            label = { Text(stringResource(R.string.site_address)) }
        )

        // Site Password.
        OutlinedTextField(
            value = sitePassword,
            onValueChange = onSidePasswordChange,
            singleLine = true,
            label = { Text(stringResource(R.string.site_password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
        )
        Button(onClick = {
            val hashedPassword = HashedPassword.create(sitePassword, siteAddress)
            onGeneratedHashChange(hashedPassword.toString())
        }, shape = RoundedCornerShape(8.dp)) {
            Text(text = stringResource(R.string.generate_action))
        }

        if (generatedHash.isNotBlank()){
            Text(text = generatedHash)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PwdHashDroidTheme {
        MainInputWithStateHoisting()
    }
}