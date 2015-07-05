import Tkinter
import Image
import ImageTk
import thread
import socket
import Queue
import struct
from OpenSSL import SSL

context = SSL.Context(SSL.SSLv23_METHOD)
context.use_privatekey_file('key')
context.use_certificate_file('cert')
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s = SSL.Connection(context, s)
s.bind(("0.0.0.0", 9999))
s.listen(10)

root = None
panel = None
windows = {}

request_queue = Queue.Queue()
result_queue = Queue.Queue()


def submit_to_tkinter(callable, *args, **kwargs):
    request_queue.put((callable, args, kwargs))
    return result_queue.get()


def threadmain():
    global windows
    global root

    def timertick():
        try:
            callable, args, kwargs = request_queue.get_nowait()
        except Queue.Empty:
            pass
        else:
            print "something in queue"
            retval = callable(*args, **kwargs)
            result_queue.put(retval)
        root.after(500, timertick)

    print 'initializing tk'
    root = Tkinter.Tk()
    timertick()
    root.mainloop()

title_number = 0


def new_window(name):
    global title_number
    print 'putting new window'
    top = Tkinter.Toplevel(root)
    frame = Tkinter.Frame(top)
    top.title(name)
    label = Tkinter.Label(frame)
    label.pack(side="bottom", fill="both", expand="yes")
    frame.pack()
    windows[name] = {}
    windows[name]['frame'] = frame
    windows[name]['label'] = label


def show_image(name, temp_filename):
    print "in show image"
    img = ImageTk.PhotoImage(Image.open(temp_filename))
    label = windows[name]['label']
    label.configure(image=img)
    label.image = img


def receive_image(temp_filename):
    sc, address = s.accept()
    print 'peer connected'
    print address
    i = 0
    size = struct.unpack("I", sc.recv(4))
    print 'received size'
    title = sc.recv(size[0])
    print 'received name ' + title
    l = sc.recv(1024)
    f = open(temp_filename, 'wb')  # open in binary
    while (l):
        f.write(l)
        if len(l) != 1024:
            break
        l = sc.recv(1024)
        i = i+1
    print 'done reading'
    f.close()
    sc.close()
    print 'done'
    return title

if __name__ == '__main__':
    thread.start_new_thread(threadmain, ())
    count = 0
    while (1):
        print 'ready to receive next'
        count = (count + 1) % 10
        temp_filename = str(count) + '.bmp'
        title = receive_image(temp_filename)
        if title not in windows:
            print 'open new window'
            submit_to_tkinter(new_window, title)
        submit_to_tkinter(show_image, title, temp_filename)
