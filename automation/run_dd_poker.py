import win32gui
import re
import time
import subprocess
import win32api
import win32con
import os

OPEN_HOLDEM_PATH = "c:\\MyProjects\\OpenHoldem\\OpenHoldem.exe"
BASH_PATH = "c:\\cygwin\\bin\\bash.exe"
OPEN_LOGS = "/cygdrive/c/MyProjects/pb/automation/openlogs.sh"
DD_POCKER_PATH = "c:\\Program Files\\ddpoker3\\ddpoker.exe"


def clean_state():
    print 'killing apps'
    os.system('taskkill /F /IM ddpoker.exe')
    os.system('taskkill /F /IM OpenHoldem.exe')
    os.system('taskkill /F /IM tail.exe')


def click(x, y):
    win32api.SetCursorPos((x, y))
    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTDOWN, x, y, 0, 0)
    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP, x, y, 0, 0)


def open_windows():
    # subprocess.Popen([BASH_PATH, "--login", OPEN_LOGS])
    subprocess.Popen([OPEN_HOLDEM_PATH])
    subprocess.Popen([DD_POCKER_PATH])


clean_state()
print 'started'
open_windows()
print 'loaded ddpoker, waiting for 2 seconds'
time.sleep(2)
print 'relocating screen'

width = 800 + 8
height = 600 + 27
locx = 0
locy = 0
pattern = '.*DD Poker.*'

toplist, winlist = [], []


def enum_cb(hwnd, results):
    winlist.append((hwnd, win32gui.GetWindowText(hwnd)))

win32gui.EnumWindows(enum_cb, toplist)

for win in winlist:
    title = win[1]
    hwnd = win[0]
    if not re.match(pattern, title, re.I):
        continue
    print 'relocating :'+title
    win32gui.MoveWindow(hwnd, locx, locy, width, height, True)

# Wait for load and click on practice
print 'sleeping for 10 seconds to allow app to load'
time.sleep(10)
click(100, 330)
print 'clicking on start'
time.sleep(1)
click(290, 580)
