import win32gui
import sys
import re
import json

config_filname = 'config.json'
if len(sys.argv) >= 2:
    config_filename = sys.argv[1]

with open(config_filename) as data_file:
    data = json.load(data_file)

width = data['width'] + 8
height = data['height'] + 27
locx = data['locx']
locy = data['locy']
pattern = data['pattern']

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
