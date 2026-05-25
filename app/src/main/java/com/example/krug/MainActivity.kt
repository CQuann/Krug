package com.example.krug

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.krug.di.InviteTokenHolder
import com.example.krug.ui.SetupNavGraph
import com.example.krug.ui.theme.KrugTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var inviteTokenHolder: InviteTokenHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)
        setContent {
            KrugTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SetupNavGraph()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        Log.d("DeepLink", "Intent: $intent, data: ${intent?.data}")
        val data = intent?.data
        if (data != null) {
            Log.d("DeepLink", "scheme: ${data.scheme}, host: ${data.host}, path: ${data.path}")
            if (data.scheme == "https" && data.host == "kruug.netlify.app" && data.path?.startsWith("/invite") == true) {
                val token = data.getQueryParameter("token")
                Log.d("DeepLink", "Token: $token")
                if (!token.isNullOrBlank()) {
                    inviteTokenHolder.setToken(token)
                    Log.d("DeepLink", "Token saved!")
                }
            }
        }
    }
}