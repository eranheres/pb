import win32gui
import re

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
