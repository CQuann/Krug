package com.example.krug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.krug.data.repository.RetrofitAuthRepository
import com.example.krug.di.NetworkModule
import com.example.krug.ui.SetupNavGraph
import com.example.krug.ui.theme.KrugTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        lifecycleScope.launch {
//            val authApi = NetworkModule.provideAuthApi(NetworkModule.provideRetrofit(NetworkModule.provideOkHttpClient()))
//            val repo = RetrofitAuthRepository(authApi)
//            val result = repo.requestCode("p9083718@gmail.com")
//            println("TEST RESULT: $result")
//        }

        setContent {
            KrugTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SetupNavGraph()
                }
            }
        }
    }
}