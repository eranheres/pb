import sys
import socket
import sys
from PIL import ImageGrab
import win32gui
import struct
import json
from pprint import pprint


def capture_image(config):
    toplist, winlist = [], []
    pattern = config["client"]["pattern"]
    host = config['client']['host']
    port = config['client']['port']

    def enum_cb(hwnd, results):
        winlist.append((hwnd, win32gui.GetWindowText(hwnd)))
    win32gui.EnumWindows(enum_cb, toplist)

    for win in winlist:
        title = win[1]
        hwnd = win[0]
        if pattern.lower() not in title.lower():
            continue
        # just grab the hwnd for first window matching firefox
        win32gui.SetForegroundWindow(hwnd)
        bbox = win32gui.GetWindowRect(hwnd)
        img = ImageGrab.grab(bbox)
        filename = title + '.tmp.bmp'
        img.save(filename, "BMP")
        send_image(title, host, port)


def send_image(title, host, port):
    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Connect the socket to the port where the server is listening
    server_address = (host, port)
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


def read_config(filename):
    with open(filename) as data_file:
        data = json.load(data_file)
    return data


def main():
    config_filename = 'config.json'
    if len(sys.argv) >= 2:
        config_filename = sys.argv[1]
    print 'configuration received from '+config_filename+':'
    config = read_config(config_filename)
    pprint(config)
    capture_image(config)
    print 'done'


if __name__ == "__main__":
        main()
