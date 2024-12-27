# PieSocket WebSocket Tester
This is a simple Android application built with Jetpack Compose that allows you to test WebSocket connections using PieSocket. The app enables users to connect to a PieSocket WebSocket channel, send messages, and view received messages in real time.

### Features
Connect to a PieSocket WebSocket channel using your API key.
Send messages to the WebSocket channel.
View received messages in real-time.
View a log of sent messages.
Real-time connection status and error notifications.
### Prerequisites
Before running this application, ensure you have the following:

Android Studio installed on your system.
A valid PieSocket API key from PieSocket.
Setup and Installation
Clone the repository

```bash
git clone https://github.com/piehostHQ/piesocket-websocket-tester.git
cd piesocket-websocket-tester
```
Open the project in Android Studio.

Add dependencies
Ensure that the PieSocket SDK is added to your build.gradle file.

```gradle
implementation 'com.piesocket:android-sdk:1.0.0' // Replace with the latest version
```
### Run the app
Click on the "Run" button in Android Studio to install the app on your device or emulator.

### Usage
Enter your API Key
On the main screen, input your PieSocket API key in the API Key field.

### Connect to PieSocket
Click the "Connect to PieSocket" button to establish a connection.

### Send a Message

Enter your message in the Message field.
Click the "Send" button.
View Received Messages
Messages sent to the WebSocket channel will appear under the "Received Message" section.

### View Sent Messages
A log of all sent messages is displayed under the "Sent Messages" section.

### Project Structure
MainActivity.kt
Contains the core functionality for WebSocket connection, message sending, and receiving.

### WebSocketTesterApp.kt
A composable function that defines the UI for the WebSocket Tester app.

### Technologies Used
Kotlin
Jetpack Compose
PieSocket SDK
