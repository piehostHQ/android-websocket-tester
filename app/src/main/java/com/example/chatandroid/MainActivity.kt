package com.example.piesockettester

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piesocket.channels.Channel
import com.piesocket.channels.PieSocket
import com.piesocket.channels.misc.PieSocketEvent
import com.piesocket.channels.misc.PieSocketEventListener
import com.piesocket.channels.misc.PieSocketOptions
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private var piesocket: PieSocket? = null
    private var channel: Channel? = null
    private var isConnected by mutableStateOf(false)
    private var receivedMessage by mutableStateOf("")
    private var sentMessages = mutableStateListOf<String>()
    private var errorMessage by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebSocketTesterApp(
                connectToPieSocket = { apiKey, channelName -> connectToPieSocket(apiKey, channelName) },
                sendMessage = { message -> sendMessage(message) },
                isConnected = { isConnected },
                receivedMessage = { receivedMessage },
                sentMessages = { sentMessages },
                errorMessage = { errorMessage }
            )
        }
    }

    private fun connectToPieSocket(apiKey: String, channelName: String) {
        if (apiKey.isBlank() || channelName.isBlank()) {
            errorMessage = "API Key or Channel Name cannot be empty"
            return
        }

        // Configure PieSocket options
        val options = PieSocketOptions().apply {
            this.apiKey = apiKey
        }

        piesocket = PieSocket(options)
        channel = piesocket?.join(channelName) // Connect to the specified channel

        // Handle connection events
        channel?.listen("system:connected", object : PieSocketEventListener() {
            override fun handleEvent(event: PieSocketEvent) {
                isConnected = true
                receivedMessage = "Connected to PieSocket channel"
                errorMessage = "" // Clear any previous errors
            }
        })

        // Handle messages
        channel?.listen("message", object : PieSocketEventListener() {
            override fun handleEvent(event: PieSocketEvent) {
                val message = event.data?.toString() ?: ""
                receivedMessage = "Received: $message"
            }
        })

        // Handle errors
        channel?.listen("system:error", object : PieSocketEventListener() {
            override fun handleEvent(event: PieSocketEvent) {
                receivedMessage = "Error: ${event.data?.toString()}"
            }
        })
    }

    private fun sendMessage(message: String) {
        if (isConnected) {
            val event = PieSocketEvent("message").apply {
                data = message
            }
            channel?.publish(event)
            sentMessages.add("Sent: $message")
        } else {
            errorMessage = "Not connected to the WebSocket"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        channel?.disconnect()
    }
}

@Composable
fun WebSocketTesterApp(
    connectToPieSocket: (String, String) -> Unit,
    sendMessage: (String) -> Unit,
    isConnected: () -> Boolean,
    receivedMessage: () -> String,
    sentMessages: () -> List<String>,
    errorMessage: () -> String
) {
    var message by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var channelName by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "PieSocket WebSocket Tester",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp),
            fontSize = 24.sp
        )

        // Connection Status
        if (isConnected()) {
            Text(
                text = "Connected",
                color = Color.Green,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
            Text(
                text = "Not Connected",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Display error message
        if (errorMessage().isNotEmpty()) {
            Text(
                text = errorMessage(),
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Input fields for API Key and Channel Name
        TextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("API Key") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        TextField(
            value = channelName,
            onValueChange = { channelName = it },
            label = { Text("Channel Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                connectToPieSocket(apiKey, channelName)
                response = "Connecting to PieSocket: $channelName"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Connect to PieSocket")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Input field for message and send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Button(
                onClick = {
                    if (isConnected()) {
                        sendMessage(message)
                        response = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Send")
            }
        }

        // Display status message
        Text(text = response, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

        Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)

        // Display received message
        if (receivedMessage().isNotEmpty()) {
            Text(text = "Received Message:", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = receivedMessage(),
                    modifier = Modifier.padding(8.dp),
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display sent messages
        if (sentMessages().isNotEmpty()) {
            Text(text = "Sent Messages:", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                sentMessages().forEach { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = msg,
                            modifier = Modifier.padding(8.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WebSocketTesterApp(
        connectToPieSocket = { _, _ -> },
        sendMessage = {},
        isConnected = { false },
        receivedMessage = { "" },
        sentMessages = { emptyList() },
        errorMessage = { "" }
    )
}
