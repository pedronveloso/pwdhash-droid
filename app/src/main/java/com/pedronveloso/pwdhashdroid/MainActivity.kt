package com.pedronveloso.pwdhashdroid

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
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
fun MainInputWithStateHoisting(hashInitialValue: String = "") {
    var siteAddress by remember { mutableStateOf("") }
    var sitePassword by remember { mutableStateOf("") }
    var generatedHash by remember { mutableStateOf(hashInitialValue) }

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
    // TODO: write tests for this Composable.
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
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            value = sitePassword,
            onValueChange = onSidePasswordChange,
            singleLine = true,
            label = { Text(stringResource(R.string.site_password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focus.clearFocus()
                    generateHashAction(siteAddress, sitePassword, onGeneratedHashChange)
                }
            )
        )

        // Generate button.
        val context = LocalContext.current
        Spacer(modifier = Modifier.size(8.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            Button(onClick = {
                generateHashAction(siteAddress, sitePassword, onGeneratedHashChange)
            }, shape = RoundedCornerShape(8.dp)) {
                Text(text = stringResource(R.string.generate_action))
            }

            if (generatedHash.isNotBlank()) {
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {
                    copyToClipboard(context, generatedHash)
                }, shape = RoundedCornerShape(8.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_copy),
                        contentDescription = stringResource(R.string.copy_to_clipboard),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(R.string.copy_to_clipboard))
                }
            }
        }

        // Generated Hash.
        Spacer(modifier = Modifier.size(8.dp))
        if (generatedHash.isNotBlank()) {
            Text(text = generatedHash)
        }
    }
}

fun copyToClipboard(context: Context, hash: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("pwd", hash)

    // Flag content as sensitive on supported Android versions.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val extras = PersistableBundle()
        extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
        clipData.description.extras = extras
    }

    clipboard.setPrimaryClip(clipData)
}

fun generateHashAction(
    siteAddress: String,
    sitePassword: String,
    onGeneratedHashChange: (String) -> Unit
) {
    // TODO: Shouldn't execute if either of the parameters is blank.
    val hashedPassword = HashedPassword.create(sitePassword, siteAddress)
    onGeneratedHashChange(hashedPassword.toString())
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PwdHashDroidTheme {
        MainInputWithStateHoisting("Hash goes here")
    }
}