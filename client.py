import socket
import sys
from PIL import Image, ImageGrab
import win32gui
import cPickle
import copy_reg
import StringIO

TEMP_FILENAME = 'temp.bmp'
TEMP_FILEFORMAT = "BMP"
PORT = 9999

def capture_image():
    toplist, winlist = [], []
    def enum_cb(hwnd, results):
        winlist.append((hwnd, win32gui.GetWindowText(hwnd)))
    win32gui.EnumWindows(enum_cb, toplist)

    firefox = [(hwnd, title) for hwnd, title in winlist if 'command' in title.lower()]
    # just grab the hwnd for first window matching firefox
    firefox = firefox[0]
    hwnd = firefox[0]

    win32gui.SetForegroundWindow(hwnd)
    bbox = win32gui.GetWindowRect(hwnd)
    img = ImageGrab.grab(bbox)
    img.save(TEMP_FILENAME, TEMP_FILEFORMAT)

def send_image():
    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Connect the socket to the port where the server is listening
    server_address = ('localhost', 9999)
    print >>sys.stderr, 'connecting to %s port %s' % server_address
    sock.connect(server_address)

    inputfile=open (TEMP_FILENAME, "rb") 
    data = inputfile.read(1024)
    while (data):
        sock.send(data)
        data = inputfile.read(1024)
    sock.shutdown(socket.SHUT_RDWR)
    sock.close()

capture_image()
send_image()
print 'done'
