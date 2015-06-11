import Tkinter
import Image
import ImageTk
import thread
import socket
import Queue

TEMP_FILENAME = 'file.bmp'
FIRST = 'first'

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(("localhost", 9999))
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
        print 'timer elapsed.'
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


def new_window(name):
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


def show_image(name):
    print "in show image"
    img = ImageTk.PhotoImage(Image.open(TEMP_FILENAME))
    label = windows[name]['label']
    label.configure(image=img)
    label.image = img


def receive_image():
    sc, address = s.accept()
    print 'peer connected'
    print address
    f = open(TEMP_FILENAME, 'wb')  # open in binary
    i = 0
    l = sc.recv(1024)
    while (l):
        f.write(l)
        l = sc.recv(1024)
        i = i+1
    print 'done reading'
    f.close()
    sc.close()
    print 'done'

if __name__ == '__main__':
    thread.start_new_thread(threadmain, ())
    while (1):
        print 'ready to receive next'
        receive_image()
        if FIRST not in windows:
            print 'open new window'
            submit_to_tkinter(new_window, FIRST)
        submit_to_tkinter(show_image, FIRST)
