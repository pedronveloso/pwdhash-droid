@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pedronveloso.pwdhashdroid.hash.HashedPassword
import com.pedronveloso.pwdhashdroid.ui.about.AboutScreen
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
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screens.MAIN) {
                        composable(Screens.MAIN) {
                            MainScreen(onNavToAbout = {
                                navController.navigate(
                                    Screens.ABOUT
                                )
                            })
                        }
                        composable(Screens.ABOUT) { AboutScreen(onNavBackPressed = { navController.popBackStack() }) }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(onNavToAbout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
            )
        }, content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainInputWithStateHoisting(onNavToAbout = onNavToAbout)
            }
        })
}

@Composable
fun MainInputWithStateHoisting(hashInitialValue: String = "", onNavToAbout: () -> Unit) {
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
        onNavToAbout = onNavToAbout
    )
}

@Composable
fun MainInputArea(
    siteAddress: String,
    onSiteAddressChange: (String) -> Unit,
    sitePassword: String,
    onSidePasswordChange: (String) -> Unit,
    generatedHash: String,
    onGeneratedHashChange: (String) -> Unit,
    onNavToAbout: () -> Unit
) {
    // TODO: write tests for this Composable.
    Spacer(modifier = Modifier.size(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val focus = LocalFocusManager.current

        WebsiteAddressInput(siteAddress, onSiteAddressChange, focus)

        Spacer(modifier = Modifier.size(8.dp))
        PasswordInput(
            siteAddress,
            sitePassword,
            onSidePasswordChange,
            onGeneratedHashChange,
            focus
        )

        FormButtons(siteAddress, sitePassword, generatedHash, onGeneratedHashChange, focus)

        GeneratedHash(generatedHash)
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
        AboutButton(onNavToAbout)
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@Composable
private fun AboutButton(onNavToAbout: () -> Unit) {
    TextButton(onClick = {
        onNavToAbout()
    }) {
        Text(text = stringResource(R.string.about))
    }
}

@Composable
private fun GeneratedHash(generatedHash: String) {
    Spacer(modifier = Modifier.size(8.dp))
    if (generatedHash.isNotBlank()) {
        Text(text = generatedHash)
    }
}

@Composable
private fun FormButtons(
    siteAddress: String,
    sitePassword: String,
    generatedHash: String,
    onGeneratedHashChange: (String) -> Unit,
    focus: FocusManager
) {
    // Generate button.
    val context = LocalContext.current
    Spacer(modifier = Modifier.size(8.dp))
    Row(horizontalArrangement = Arrangement.Center) {
        Button(onClick = {
            val succeeded = generateHashAction(siteAddress, sitePassword, onGeneratedHashChange)
            if (succeeded) {
                focus.clearFocus()
            }
        }, shape = RoundedCornerShape(8.dp)) {
            Text(text = stringResource(R.string.generate_action))
        }

        // Copy to clipboard button.
        if (generatedHash.isNotBlank()) {
            Spacer(modifier = Modifier.size(16.dp))
            Button(onClick = {
                focus.clearFocus()
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
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PasswordInput(
    siteAddress: String,
    sitePassword: String,
    onSidePasswordChange: (String) -> Unit,
    onGeneratedHashChange: (String) -> Unit,
    focus: FocusManager
) {
    OutlinedTextField(
        value = sitePassword,
        onValueChange = onSidePasswordChange,
        singleLine = true,
        label = { Text(stringResource(R.string.site_password)) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(FocusRequester.Default)
            .onKeyEvent {
                when (it.key) {
                    Key.Enter -> {
                        focus.clearFocus()
                        generateHashAction(siteAddress, sitePassword, onGeneratedHashChange)
                        true
                    }
                    Key.Tab -> {
                        focus.moveFocus(FocusDirection.Next)
                        true
                    }
                    else -> false
                }
            },
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

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun WebsiteAddressInput(
    siteAddress: String,
    onSiteAddressChange: (String) -> Unit,
    focus: FocusManager
) {
    OutlinedTextField(
        value = siteAddress,
        onValueChange = onSiteAddressChange,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(FocusRequester.Default)
            .onKeyEvent {
                when (it.key) {
                    Key.Tab -> {
                        focus.moveFocus(FocusDirection.Next)
                        true
                    }
                    else -> false
                }
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Next,
            autoCorrect = false
        ),
        keyboardActions = KeyboardActions(
            onNext = { focus.moveFocus(FocusDirection.Next) },
        ),
        label = { Text(stringResource(R.string.site_address)) }
    )
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

/**
 * @return True if was able to generate Hash with success, False otherwise.
 */
fun generateHashAction(
    siteAddress: String,
    sitePassword: String,
    onGeneratedHashChange: (String) -> Unit
): Boolean {
    if (siteAddress.isBlank() || sitePassword.isBlank()) {
        return false
    }
    val hashedPassword = HashedPassword.create(sitePassword, siteAddress)
    onGeneratedHashChange(hashedPassword.toString())
    return true
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PwdHashDroidTheme {
        MainInputWithStateHoisting("Hash goes here") {}
    }
}