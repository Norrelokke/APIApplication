package se.novembercode.apiapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.text.TextStyle

import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

import se.novembercode.apiapplication.ui.theme.APIApplicationTheme




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            APIApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TwopartJoke()
                }
            }
        }
    }
}

@Composable
fun TwopartJoke(modifier: Modifier = Modifier) {

    val client = OkHttpClient()

    var thesetup by remember { mutableStateOf("setup") }
    var thedelivery by remember { mutableStateOf("delivery") }
    var showDelivery by remember { mutableStateOf(false) }

    var started by remember { mutableStateOf(false) }

    fun loadjoke() {

        val req = Request.Builder().url("https://v2.jokeapi.dev/joke/Any?type=twopart").build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("APIDEBUG", "HÄMTNING INTE OK")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    var responseString = response.body!!.string()

                    Log.i("APIDEBUG", responseString)

                    val jokedata = Json{ ignoreUnknownKeys = true}.decodeFromString<TwopartJoke>(responseString)

                    thesetup = jokedata.setup
                    thedelivery = jokedata.delivery

                }
            }
        })
    }

    LaunchedEffect(started) {
        loadjoke()

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = thesetup,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            ),
            textAlign = TextAlign.Center
        )
        if (showDelivery) {
            Text(
                text = "-" + thedelivery,
                modifier = modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center
            )
        }
        val Moonstone= Color(0xFFD7B9D5)

        Button(
            onClick = { showDelivery = true },
            modifier = Modifier
                .fillMaxWidth() // Fill the max width to allow centering
                .wrapContentWidth(Alignment.CenterHorizontally) // Center the button horizontally
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Moonstone)
        ) {
            Text(
                text = "Reveal joke delivery",
                style = TextStyle(
                    fontSize = 18.sp, // Set the text size inside the button
                    fontWeight = FontWeight.Bold // Make the text bold if desired
                )
            )
        }
        // This is a common mint green color; adjust the hex value as needed for your design

        Button(
            onClick = {
                loadjoke()
                showDelivery = false // Reset the delivery visibility when loading a new joke
            },
            modifier = Modifier
                .fillMaxWidth() // Fill the max width to allow centering
                .wrapContentWidth(Alignment.CenterHorizontally) // Center the button horizontally
                .padding(16.dp), // Add padding around the button
            colors = ButtonDefaults.buttonColors(containerColor = Moonstone) // Use mint green color for the button
        ) {
            Text(
                text = "Load new Joke",
                style = TextStyle(
                    fontSize = 18.sp, // Set the text size inside the button
                    fontWeight = FontWeight.Bold // Make the text bold if desired
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    APIApplicationTheme {
        TwopartJoke()
    }
}

@Serializable
data class TwopartJoke(val category : String, val setup : String, val delivery : String)