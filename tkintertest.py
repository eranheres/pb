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

top = None
panel = None
windows = {FIRST: {}}


def submit_to_tkinter(callable, *args, **kwargs):
    windows[FIRST]['request_queue'].put((callable, args, kwargs))
    return windows[FIRST]['result_queue'].get()


def threadmain():
    global windows

    win = windows[FIRST]

    def timertick():
        try:
            callable, args, kwargs = win['request_queue'].get_nowait()
        except Queue.Empty:
            pass
        else:
            print "something in queue"
            retval = callable(*args, **kwargs)
            win['result_queue'].put(retval)
        win['top'].after(500, timertick)

    win['top'] = Tkinter.Tk()

    win['request_queue'] = Queue.Queue()
    win['result_queue'] = Queue.Queue()

    img = ImageTk.PhotoImage(Image.open(TEMP_FILENAME))
    win['panel'] = Tkinter.Label(top, image=img)
    win['panel'].pack(side="bottom", fill="both", expand="yes")

    timertick()
    # Code to add widgets will go here...
    win['top'].mainloop()


def show_image():
    print "in show image"
    windows[FIRST]['top'].title("hello wolrd")
    img2 = ImageTk.PhotoImage(Image.open(TEMP_FILENAME))
    windows[FIRST]['panel'].configure(image=img2)
    windows[FIRST]['panel'].image = img2


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
        print i
    print 'done reading'
    f.close()
    sc.close()
    print 'done'

if __name__ == '__main__':
    thread.start_new_thread(threadmain, ())

    while (1):
        receive_image()
        submit_to_tkinter(show_image)
