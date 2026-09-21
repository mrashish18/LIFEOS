import os
import subprocess
import time

ADB = os.path.expandvars(r"$LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe")
TARGET_DIR = r"C:\Users\iamas\AIProjects\LIFEOS\screenshots"

def adb(cmd):
    full = f'"{ADB}" {cmd}'
    res = subprocess.run(full, shell=True, capture_output=True, text=True)
    return res.stdout.strip()

def tap(x, y, wait=1.0):
    adb(f"shell input tap {x} {y}")
    time.sleep(wait)

def keyevent(code, wait=0.8):
    adb(f"shell input keyevent {code}")
    time.sleep(wait)

def swipe(x1, y1, x2, y2, dur=300, wait=1.0):
    adb(f"shell input swipe {x1} {y1} {x2} {y2} {dur}")
    time.sleep(wait)

def capture(filename):
    device_path = f"/sdcard/{filename}"
    local_path = os.path.join(TARGET_DIR, filename)
    adb(f"shell screencap -p {device_path}")
    adb(f"pull {device_path} \"{local_path}\"")
    print(f"Captured: {filename} ({os.path.getsize(local_path)} bytes)")

# Ensure app is active
adb("shell am start -n com.mrashish18.lifeos/.MainActivity")
time.sleep(1.5)

# Reset state: press BACK a couple of times to dismiss any sheets/dialogs
keyevent(4)
time.sleep(0.5)
keyevent(4)
time.sleep(0.5)

print("--- Step 1: Dashboard ---")
tap(100, 2250) # Tab Home
time.sleep(1.0)
capture("01_dashboard.png")

print("--- Step 2: Tasks ---")
tap(275, 2250) # Tab Tasks
time.sleep(1.0)
capture("02_tasks.png")

print("--- Step 3: New Task Sheet ---")
tap(960, 2058) # FAB 'New Task'
time.sleep(1.2)
capture("03_new_task.png")
# Dismiss bottom sheet
keyevent(4) # BACK dismisses sheet
time.sleep(1.0)

print("--- Step 4: Goals ---")
tap(450, 2250) # Tab Goals
time.sleep(1.0)
capture("04_goals.png")

print("--- Step 5: Intelligence ---")
tap(630, 2250) # Tab Intel
time.sleep(1.0)
capture("05_intelligence.png")

print("--- Step 6: Truth Inquiry ---")
tap(800, 2250) # Tab Truth
time.sleep(1.0)
# If currently showing result, tap back
# Let's tap top back button at (90, 200) just in case
tap(90, 200)
time.sleep(0.8)
capture("06_truth_input.png")

print("--- Step 7: Truth Result ---")
# Tap the sample claim chip "Earth orbits the Sun in 365 days?"
tap(250, 930) 
time.sleep(0.6)
# Tap "Investigate Claim"
tap(540, 830)
time.sleep(2.0)
capture("07_truth_result.png")

# Back to main Truth
keyevent(4)
time.sleep(0.8)

print("--- Step 8: RescueMesh Center ---")
tap(980, 2250) # Tab Mesh
time.sleep(1.0)
capture("08_rescuemesh.png")

print("--- Step 9: Emergency SOS Modal ---")
tap(574, 1420) # Dispatch Emergency Message
time.sleep(1.2)
capture("09_emergency_message.png")
# Dismiss modal
keyevent(4)
time.sleep(0.8)

print("--- Step 10: Message Queue ---")
# On Mesh screen, scroll down to see message queue
swipe(540, 1800, 540, 700, 400)
time.sleep(1.0)
capture("10_message_queue.png")
# Scroll back up
swipe(540, 700, 540, 1800, 400)
time.sleep(0.8)

print("--- Step 11: Notification Center ---")
tap(100, 2250) # Go to Home
time.sleep(0.8)
tap(988, 215) # Bell icon
time.sleep(1.2)
capture("11_notifications.png")
# Dismiss notifications
keyevent(4)
time.sleep(0.8)

print("--- Step 12: Navigation Drawer ---")
tap(92, 215) # Hamburger menu
time.sleep(1.2)
capture("12_navigation_drawer.png")

print("--- Step 13: Appearance Modal ---")
# Inside drawer, tap 'Appearance & Theme'
# Let's find its position or tap near y=1200
# First tap outside or tap Appearance if visible
# Let's swipe drawer a bit or tap Appearance
# In drawer, items are Profile, Navigation, Appearance & Theme
# Let's tap Appearance item: (200, 1220)
tap(200, 1220)
time.sleep(1.2)
capture("13_appearance.png")
# Dismiss
keyevent(4)
time.sleep(0.8)
keyevent(4)
time.sleep(0.8)

print("Done capturing pristine screenshots!")
