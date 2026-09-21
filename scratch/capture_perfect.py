import os
import subprocess
import time
import xml.etree.ElementTree as ET

ADB = os.path.expandvars(r"$LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe")
OUT_DIR = r"C:\Users\iamas\AIProjects\LIFEOS\screenshots"

def adb(cmd):
    full = f'"{ADB}" {cmd}'
    res = subprocess.run(full, shell=True, capture_output=True, text=True)
    return res.stdout.strip()

def tap(x, y, wait=1.0):
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
    print(f"[OK] Saved {filename} ({os.path.getsize(local_path):,} bytes)")

def find_bounds(pattern):
    adb("shell uiautomator dump /sdcard/temp_dump.xml")
    adb("pull /sdcard/temp_dump.xml temp_dump.xml")
    tree = ET.parse("temp_dump.xml")
    for elem in tree.iter("node"):
        text = elem.attrib.get("text", "")
        desc = elem.attrib.get("content-desc", "")
        bounds = elem.attrib.get("bounds", "")
        if pattern.lower() in text.lower() or pattern.lower() in desc.lower():
            # Parse [x1,y1][x2,y2]
            parts = bounds.replace("][", ",").replace("[", "").replace("]", "").split(",")
            x1, y1, x2, y2 = map(int, parts)
            cx, cy = (x1 + x2) // 2, (y1 + y2) // 2
            return cx, cy
    return None, None

# Bring app to front
adb("shell am start -n com.mrashish18.lifeos/.MainActivity")
time.sleep(1.5)

print("--- 1. Dashboard ---")
tap(100, 2250)
time.sleep(0.8)
screencap("01_dashboard.png")

print("--- 2. Tasks ---")
tap(275, 2250)
time.sleep(0.8)
screencap("02_tasks.png")

print("--- 3. New Task Bottom Sheet ---")
cx, cy = find_bounds("New Task")
if not cx:
    cx, cy = 886, 2055
tap(cx, cy)
time.sleep(1.2)
screencap("03_new_task.png")
# Dismiss: tap top area
tap(500, 400)
time.sleep(0.8)

print("--- 4. Goals ---")
tap(450, 2250)
time.sleep(0.8)
screencap("04_goals.png")

print("--- 5. Intelligence ---")
tap(630, 2250)
time.sleep(0.8)
screencap("05_intelligence.png")

print("--- 6. Truth Inquiry ---")
tap(800, 2250)
time.sleep(0.8)
# If on result, tap back
cx, cy = find_bounds("Investigation Result")
if cx:
    tap(90, 200) # back arrow
    time.sleep(0.8)
screencap("06_truth_input.png")

print("--- 7. Truth Result ---")
cx, cy = find_bounds("Earth orbits")
if cx:
    tap(cx, cy)
    time.sleep(0.8)
cx, cy = find_bounds("Investigate Claim")
if cx:
    tap(cx, cy)
    time.sleep(2.5)
screencap("07_truth_result.png")
# Return to inquiry screen
tap(90, 200)
time.sleep(0.8)

print("--- 8. RescueMesh Network ---")
tap(980, 2250)
time.sleep(0.8)
screencap("08_rescuemesh.png")

print("--- 9. Emergency Dispatch Modal ---")
cx, cy = find_bounds("Dispatch Emergency")
if cx:
    tap(cx, cy)
    time.sleep(1.0)
    screencap("09_emergency_message.png")
    # Dismiss: tap Close or outside
    tap(980, 480) # top-right close button of dialog or back
    time.sleep(0.8)

print("--- 10. Message Queue ---")
# Scroll down on Mesh screen
swipe(540, 1800, 540, 600, 400)
time.sleep(1.0)
screencap("10_message_queue.png")
swipe(540, 600, 540, 1800, 400)
time.sleep(0.8)

print("--- 11. Notification Center ---")
tap(100, 2250) # Home
time.sleep(0.8)
cx, cy = find_bounds("Notifications")
if cx:
    tap(cx, cy)
    time.sleep(1.0)
    screencap("11_notifications.png")
    # Dismiss
    tap(980, 480)
    time.sleep(0.8)

print("--- 12. Navigation Drawer ---")
cx, cy = find_bounds("navigation drawer")
if cx:
    tap(cx, cy)
    time.sleep(1.0)
    screencap("12_navigation_drawer.png")
    # Tap outside to dismiss
    tap(900, 1200)
    time.sleep(0.8)

print("COMPLETE!")
