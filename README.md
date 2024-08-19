# CIE-IoT-IVI

## Outcomes
1. Successful build and stable boot of Android Automotive 14 on Raspberry Pi 5.
2. Developed a CCTV camera setup using Raspberry Pi 4.
3. Built an app for Android Automotive 14 which streams live CCTV footage from anywhere when motion is detected.

---

## I) Android Automotive 14 Build for Raspberry Pi 5

### Requirements
1. A 64-bit x86 system, preferably Ubuntu 18 and above. Multiple core CPU results in faster builds.
2. At least 400 GB of free disk space to check out and build the code (250 GB to check out + 150 GB to build).
3. Minimum 32GB RAM. Increase the swap size to 32GB on Ubuntu using [this guide](https://linuxhandbook.com/increase-swap-ubuntu/).
4. Stable, unrestricted, and fast internet.
5. Raspberry Pi 5 and SD card of minimum 16GB in size.

### How to Build
1. Establish Android build environment and install repo. Follow instructions [here](https://source.android.com/docs/setup/start/requirements).

2. Install additional packages:
    ```bash
    sudo apt-get install bc coreutils dosfstools e2fsprogs fdisk kpartx mtools ninja-build pkg-config python3-pip
    sudo pip3 install meson mako jinja2 ply pyyaml dataclasses
    ```

3. Initialize repo:
    ```bash
    mkdir WORKING_DIRECTORY
    cd WORKING_DIRECTORY
    repo init -u https://android.googlesource.com/platform/manifest -b android-14.0.0_r22
    curl -o .repo/local_manifests/manifest_brcm_rpi.xml -L https://raw.githubusercontent.com/raspberry-vanilla/android_local_manifest/android-14.0/manifest_brcm_rpi.xml --create-dirs
    ```
    Chose `android-14.0.0_r22` because it was the most stable amongst the rest.

4. Sync source code (i.e., download):
    ```bash
    repo sync -j4
    ```
    Uses 4 cores to download; adjust accordingly.

5. Setup Android build environment:
    ```bash
    . build/envsetup.sh
    ```

6. Select the device:
    ```bash
    lunch aosp_rpi5_car-ap2a-userdebug
    ```

7. Compile:
    ```bash
    make bootimage systemimage vendorimage -j$(nproc)
    ```
    By default, uses all cores. Adjust accordingly; more cores = faster build.

8. Make flashable image for the device:
    ```bash
    ./rpi5-mkimg.sh
    ```
    This should create a bootable image file of size 7GB.

    You can find my build image of the above here: ---
9. Download the Raspberry Pi [imager](https://www.raspberrypi.com/software/) and follow the [instructions](https://www.raspberrypi.com/documentation/computers/getting-started.html#install-using-imager) to flash the image onto the sdcard. Use the sd card to boot up the Pi5.

You can find full guide for other builds like android, android tv, android automotive for both rpi4 and rpi5 [here](https://github.com/raspberry-vanilla/android_local_manifest)
### References
1. [AOSP Android Open Source Project](https://source.android.com/)
2. [Raspberry Pi Vanilla Git (Konsta T)](https://github.com/raspberry-vanilla)
3. [Linux Handbook](https://linuxhandbook.com/)

---
## II) App for Android Automotive:

### Functionalities
1. UI provides a buttton to toggle between D(Drive) or P(Park) mode.
2. The app receives the URL from the CCTV whenever its hosted and notifies about it.
3. Driver can stream the url with a stream button if vehicle is in park mode.
4. Whenever motion is detected on the CCTV, a notification is popped with 2 options, ignore and stream.
5. To stream the URL the toggle needs to be in P mode otherwise D mode alert is given via notification.

### Requirements
1. Android Studio with required SDKs installed.
2. Pushy API account.

### How to Set Up
1. Use Android Studio and set it up for development, create a new project.

2. Setup API:
    - Go to the Pushy API [dashboard](https://pushy.me/docs/api) and create an app.
    - Name it same as the project/app name in Android Studio.
    - Enter the package name wherever prompted.
    - Make a note of secret api key from the application section.

3. Setup API in the app and develop the app:
    - All these steps are done and you can find the codes above.
    - Copy all the files in your project directory accordingly.
    - Change the package name to your app name wherever needed(thirdeyecar here).
    - Follow Pushy [docs](https://pushy.me/docs/android/create-app) to setup the client.(done above)

4. Connect the Raspberry Pi 5 with ADB wirelessly to deploy apps:
    - Make sure your development machine/PC having Android Studio and Raspberry Pi 5 are on the same network.
    - Open terminal and type `adb connect <ip_address_of_pi5>`
    - You can find ipaddress of pi5 from the WiFi settings in android settings of the device itself.
    - Android Studio will automatically detect the device, and you are now ready to deploy apps.

6. Run the app.
    - This will generate a device token. Take note of this since it is used for communication with the CCTV. It changes if you uninstall and reinstall the app. It does not change for every run in the studio.
    - For the first run you will get a device token , take a note of this. This can also be found in the adb logcat in Android Studio.
   
---
      
## III) CCTV Setup
This section outlines the steps to setup a CCTV system using a Raspberry Pi 4, allowing it to stream live footage, detect motion, and integrate with an Android Automotive 14 app.
### Features
- Streams live CCTV footage from Raspberry Pi 4.
- Detects motion and sends notifications to the Android Automotive app.
- Allows remote access and streaming via Ngrok.
- Integrates with the app using Pushy API for secure communication.
### Requirements
1. Hardware:
   - Raspberry Pi 4 with Raspbian OS installed.
   - USB Webcam.
   - MicroSD Card (at least 16GB) with Raspbian OS.
   - Power Supply for Raspberry Pi 4.
   - Stable internet connection.
   - Monitor, Keyboard, Mouse.
2. Software:
   - Motion software for video streaming and motion detection.
   - Ngrok for secure remote access.
   - Pushy API for notificaton management.
### Step-by-Step-Setup
#### 1. Install and Configure Motion
Motion is the key software used for turning the Raspberry Pi into a surveillance camera.
1. Update and Upgrade Raspberry Pi:
   ```bash
   sudo apt-get update
   sudo apt-get upgrade
   ```
2. Install Motion:
   ```bash
   sudo apt-get install motion
   ```
3. Configure Motion:
     - Open the Motion configuration file:
   ```bash
   sudo nano /etc/motion/motion.conf
   ```
    -Key configurations to update:
   ```bash
   daemon on
   stream_localhost off
   stream_port 8081
   videodevice /dev/video0
   on_motion_detected curl -X POST "https://api.pushy.me/push?api_key=your_secret_api_key" -H "Content-Type: application/json" -d '{"to":"your_device_token","data":{"message":"Motion Detected!"}}'
    ```
    Replace "your_secret_api_key" with your actual Pushy API secret key and "your_device_token" with the device token from your Android Automotive app. For further Motion config file setup refer [here](https://motion-project.github.io/motion_config.html#movie_output).
         
4. Start Motion Service:
   ```bash
   sudo systemctl start motion
   ```
   To check motion log
   ```bash
   sudo systemctl status motion
   ```
#### 2. Set Up Ngrok for Remote Access
Ngrok allows remote access to your CCTV stream from anywhere.
-Should be done in a new terminal.
1. Install Ngrok:
   - Download and install:
   ```bash
   wget https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-arm.zip
   unzip ngrok-stable-linux-arm.zip
   sudo mv ngrok /usr/local/bin/
   ```
3. Authenticate Ngrok:
   - Obtain your authentication token from [Ngrok's website](https://ngrok.com/) and run:
   ```bash
   ngrok authtoken <your_auth_token>
   ```
4. Start the Ngrok Tunnel:
```bash
ngrok http 8081
```
-This will provide a public URL for remote access.

---
## IV) Testing
