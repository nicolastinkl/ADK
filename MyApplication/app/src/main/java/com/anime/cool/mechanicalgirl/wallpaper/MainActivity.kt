package com.anime.cool.mechanicalgirl.wallpaper

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anime.cool.mechanicalgirl.wallpaper.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        try {

            Log.d("破解",ewoaaa.wewoaO0("4bojC/2BhAuYugU=\n", "qoN0TLnOyH0=\n"))
            Log.i("破解", ewoaaa.wewoaO0("hHWN/DWPyieDc57lON6G\n", "8B3kj1/kpkY=\n"));
//            System.loadLibrary(ewoaaa.wewoaO0("4bojC/2BhAuYugU=\n", "qoN0TLnOyH0=\n"))
        } catch (unused: Exception) {
        }

        ewoaaa.eOw0w0aOb(this,"startJob")

        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}