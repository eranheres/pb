import socket
import sys
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(("localhost",9999))
s.listen(10)

while True:
    sc, address = s.accept()
    print 'peer connected'
    print address
    f = open('file.jpg','wb') #open in binary
    i=0
    l = sc.recv(1024)
    while (l):
        f.write(l)
        l = sc.recv(1024)
        i=i+1
        print i
    print 'done reading'
    f.close()
    sc.close()
    print 'done'
s.close()
