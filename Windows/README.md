# PenFerry Windows Application 🖊️🚢

The Windows component of PenFerry acts as the receiving end, processing stylus interactions sent from the Android device and reflecting them on your Windows machine. Dive into the world where your Android stylus becomes an integral part of your Windows experience!

## Overview 🌐

PenFerry for Windows is engineered to seamlessly interpret and simulate the stylus actions received from its Android counterpart. With real-time processing, it ensures that every doodle, note, or gesture you make on your Android device is mirrored on your Windows screen.

## Features 🌟

- **PenPacket Processing**: Understands various stylus events, including hovering, contact movements, and supplemental actions.
- **UDP Server**: Listens for incoming pen packets, ensuring a robust and real-time connection with the Android app.
- **Synthetic Pointer Integration**: Utilizes the [`SynthPointer.dll`](https://github.com/Sett17/SynthPointer.dll) to simulate the received stylus actions on Windows, offering a natural and smooth experience.

### About SynthPointer.dll 🧩

[`SynthPointer.dll`](https://github.com/Sett17/SynthPointer.dll) is a crucial component of the PenFerry Windows application. It's responsible for simulating the stylus actions on the Windows side, ensuring that the interactions captured on the Android device are accurately reflected. For a deeper dive into its workings and functionalities, feel free to check out its [repository](https://github.com/Sett17/SynthPointer.dll).

## Setup 🛠️

### Requirements:

- Ensure `SynthPointer.dll` is present in the application directory.

### Option 1: Building from Source:

1. Ensure you have [Go](https://golang.org/) installed on your machine.
2. Clone the PenFerry repository.
3. Navigate to the Windows directory.
4. Run the command `go build` to compile the source code.
5. Upon successful compilation, you'll find the executable `penferry.exe` in the directory.

### Option 2: Direct Download:

For a quick setup, download the latest `penferry.exe` executable from the 'Releases' section of the repository. Simply run the executable to launch the application.

### Running the Application:

1. Launch the PenFerry Windows application.
2. Input the desired port number (default is `17420`).
3. Click "Start" to initiate the server. Once running, it will listen for incoming packets from the Android app.
4. To stop the server, simply click "Stop".

## Interactions 🎨

- **Hover Movements**: Reflects the hovering action of the stylus on the Android device.
- **Contact Movements**: Simulates the drawing or writing actions.
- **Supplemental Action**: This special action, triggered by a two-finger tap on the Android side, cycles through all the screens on the Windows machine.
