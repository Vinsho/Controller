import os
import socket, keyboard

def eval(data):
    print(data)
    if data=="shutdown":
        os.system('cmd /c shutdown -s')   
    elif data=="volumeUp":
        keyboard.press_and_release('volume up')  
    elif data=="volumeDown":
        keyboard.press_and_release('volume down')  
    elif data=="pause":
        keyboard.press_and_release('k')     
    elif data=="next":
        keyboard.press_and_release('shift+n')  
    elif data=="prev":
        keyboard.press_and_release('shift+p') 
    elif data=="fullscreen":
        keyboard.press_and_release('f')   
    elif data=="forward":
        keyboard.press_and_release('right')    
    elif data=="backward":
        keyboard.press_and_release('left') 
    elif data=="tSwitch":
        keyboard.press_and_release('ctrl+tab')     
    elif data=="wSwitch":
        keyboard.press('ctrl+alt')
        keyboard.press_and_release('tab')  
    elif data=="enter":
        keyboard.press_and_release('enter')    
        keyboard.release("ctrl")
        keyboard.release("alt")    
    return True  

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.connect(("8.8.8.8", 80))
ip = s.getsockname()[0]
print("Connect to:" + ip)
s.close()

while True:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((ip,9999))
        s.listen()
        conn, addr = s.accept()
        with conn:
            while True:
                data = conn.recv(1024)
                data = data.decode("utf-8")
                if eval(data):
                    break

# print(keyboard.read_hotkey())
