import os
import subprocess
import time

ADB = os.path.expandvars(r"$LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe")
OUT_DIR = r"C:\Users\iamas\AIProjects\LIFEOS\screenshots"

def adb(cmd):
    full = f'"{ADB}" {cmd}'
    res = subprocess.run(full, shell=True, capture_output=True, text=True)
    return res.stdout.strip()

def tap(x, y, wait=1.2):
    adb(f"shell input tap {x} {y}")
    time.sleep(wait)

def swipe(x1, y1, x2, y2, dur=300, wait=1.0):
    adb(f"shell input swipe {x1} {y1} {x2} {y2} {dur}")
    time.sleep(wait)

def screencap(filename):
    device_path = f"/sdcard/{filename}"
    local_path = os.path.join(OUT_DIR, filename)
    adb(f"shell screencap -p {device_path}")
    adb(f"pull {device_path} \"{local_path}\"")
    print(f"Captured {filename}: {os.path.getsize(local_path)} bytes")

def restart_app():
    adb("shell am force-stop com.mrashish18.lifeos")
    time.sleep(0.5)
    adb("shell am start -n com.mrashish18.lifeos/.MainActivity")
    time.sleep(2.0)

# 1. Start App -> 01_dashboard.png
restart_app()
screencap("01_dashboard.png")

# 2. Drawer -> 12_navigation_drawer.png
tap(92, 215) # Hamburger menu
time.sleep(1.0)
screencap("12_navigation_drawer.png")

# 3. Appearance Modal -> 13_appearance.png
# Inside drawer: scroll down slightly to ensure Appearance & Theme is in view
swipe(500, 1600, 500, 1000, 300)
time.sleep(0.8)
# Look for Appearance & Theme or tap it
tap(350, 1550) # Appearance & Theme position in drawer
time.sleep(1.2)
screencap("13_appearance.png")

# 4. Restart app -> Notification Center -> 11_notifications.png
restart_app()
tap(988, 215) # Notification Bell
time.sleep(1.0)
screencap("11_notifications.png")

# 5. Restart app -> Tasks -> 02_tasks.png
restart_app()
tap(275, 2250) # Tasks tab
time.sleep(1.2)
screencap("02_tasks.png")

# 6. Open New Task bottom sheet -> 03_new_task.png
tap(960, 2058) # FAB New Task
time.sleep(1.2)
screencap("03_new_task.png")

# 7. Restart app -> Goals -> 04_goals.png
restart_app()
tap(450, 2250) # Goals tab
time.sleep(1.2)
screencap("04_goals.png")

# 8. Intelligence -> 05_intelligence.png
tap(630, 2250) # Intel tab
time.sleep(1.2)
screencap("05_intelligence.png")

# 9. RealityCheck / Truth -> 06_truth_input.png
tap(800, 2250) # Truth tab
time.sleep(1.5)
screencap("06_truth_input.png")

# 10. Investigate Claim -> 07_truth_result.png
# Click the sample chip "Earth orbits the Sun in 365 days?"
tap(320, 930)
time.sleep(0.8)
# Click "Investigate Claim"
tap(540, 830)
time.sleep(2.5) # Allow computation / display
screencap("07_truth_result.png")

# 11. Restart app -> RescueMesh -> 08_rescuemesh.png
restart_app()
tap(980, 2250) # Mesh tab
time.sleep(1.5)
screencap("08_rescuemesh.png")

# 12. Dispatch Emergency Message Modal -> 09_emergency_message.png
tap(574, 1420) # Dispatch Emergency Message button
time.sleep(1.2)
screencap("09_emergency_message.png")

# 13. Message Queue -> 10_message_queue.png
# Restart to mesh, then scroll down to message queue
restart_app()
tap(980, 2250) # Mesh tab
time.sleep(1.2)
swipe(540, 1800, 540, 600, 400)
time.sleep(1.0)
screencap("10_message_queue.png")

print("All screenshots captured cleanly!")
