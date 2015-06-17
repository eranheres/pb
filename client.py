import socket
import sys
from PIL import ImageGrab
import win32gui
import struct

PORT = 9999
PATTERN = 'command'


def capture_image():
    toplist, winlist = [], []

    def enum_cb(hwnd, results):
        winlist.append((hwnd, win32gui.GetWindowText(hwnd)))
    win32gui.EnumWindows(enum_cb, toplist)

    for win in winlist:
        title = win[1]
        hwnd = win[0]
        if PATTERN.lower() not in title.lower():
            continue
        # just grab the hwnd for first window matching firefox
        win32gui.SetForegroundWindow(hwnd)
        bbox = win32gui.GetWindowRect(hwnd)
        img = ImageGrab.grab(bbox)
        filename = title + '.tmp.bmp'
        img.save(filename, "BMP")
        send_image(title)


def send_image(title):
    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Connect the socket to the port where the server is listening
    server_address = ('localhost', 9999)
    print >>sys.stderr, 'connecting to %s port %s' % server_address
    sock.connect(server_address)

    filename = title + '.tmp.bmp'
    length = len(title)
    sock.send(struct.pack("I", length))
    sock.send(title)
    inputfile = open(filename, "rb")
    data = inputfile.read(1024)
    while (data):
        sock.send(data)
        data = inputfile.read(1024)
    sock.shutdown(socket.SHUT_RDWR)
    sock.close()

capture_image()
print 'done'
