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

### References
1. [AOSP website](https://source.android.com/)
2. [Raspberry Pi Vanilla Git (Konsta T)](https://github.com/raspberry-vanilla)
3. [Linux Handbook](https://linuxhandbook.com/)

---

## II) CCTV Setup

### Features
- Streams live CCTV footage from Raspberry Pi 4.

---

## III) App

- Built an app for Android Automotive 14 which streams live CCTV footage from anywhere when motion is detected.

---

