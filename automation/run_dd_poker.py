import win32gui
import re
import time
import subprocess
import win32api
import win32con
import os


def clean_state():
    print 'killing apps'
    os.system('taskkill /F /IM ddpoker.exe')
    os.system('taskkill /F /IM OpenHoldem.exe')


def click(x, y):
    win32api.SetCursorPos((x, y))
    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTDOWN, x, y, 0, 0)
    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP, x, y, 0, 0)


clean_state()
print 'started'
subprocess.Popen(["c:\\MyProjects\\OpenHoldem_7.7.3\OpenHoldem.exe"])
subprocess.Popen(["c:\\Program Files\\ddpoker3\\ddpoker.exe"])
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
