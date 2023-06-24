from __future__ import print_function, unicode_literals

import array
import sys
import time
import usb
from time import sleep
import string
import subprocess, sys

_B = 'B' if sys.version_info.major == 3 else b'B'


IDS = (0x22d9, 0x2765)

manufacturer = "_"
model = "_"
description = "_"
version = "1"
uri = str((
"_",
"_"
))
serial = "a"

p = subprocess.Popen("pw-record --target my-virtualmic --latency 1ns --channel-map stereo --rate=48000 -", stdout=subprocess.PIPE, shell=True)

def findDevice(vendor, product):
    while True:
        device = usb.core.find(idVendor=vendor, idProduct=product)
        if device is None:
            sleep(0.1)
            continue

        print(f"succesfully found device with {vendor} {product}")
        return device

def enableAccesoryMode(device):
    assert(device.ctrl_transfer(0x40, 53) == 0)

def sendMeta(device):
    buf = device.ctrl_transfer(0xc0, 51, data_or_wLength=2)
    assert(len(buf) == 2 and (buf[0] | buf[1] << 8) == 2)

    for i, data in enumerate((manufacturer, model, description, version, uri, serial)):
        assert(device.ctrl_transfer(0x40, 52, wIndex=i, data_or_wLength=data) == len(data))

def detectEndpoints(device):
    interface = device.get_active_configuration()[(0, 0)]

    def first_out_endpoint(endpoint):
        return (usb.util.endpoint_direction(endpoint.bEndpointAddress) == usb.util.ENDPOINT_OUT)

    def first_in_endpoint(endpoint):
        return (usb.util.endpoint_direction(endpoint.bEndpointAddress) == usb.util.ENDPOINT_IN)

    endpoint_out = usb.util.find_descriptor(interface, custom_match=first_out_endpoint)
    endpoint_in = usb.util.find_descriptor(interface, custom_match=first_in_endpoint)

    assert(endpoint_out and endpoint_in)

    return endpoint_out, endpoint_in

def write(stream, data, timeout=None):
    i = 0

    while i < 3:
        try:
            dOut.write(data, timeout=None)
        except usb.core.USBError as e:
            if e.errno == 110:  # Operation timed out
                print("timeout")
                i += 1
                continue
            else:
                i += 1
                continue
        else:
            return

    dOut.write(data, timeout=None)


def handleConnection(dOut):
    while True:
        out = p.stdout.read(512)
        if out == '' and p.poll() != None:
            break
        if out != '':
            write(dOut, out)


while True:
    print("c")
    device = usb.core.find(idVendor=0x18d1, idProduct=0x2d01)
    if device is not None:
        print("a")
        try:
            dOut, dIn = detectEndpoints(device)
            handleConnection(dOut)
        except Exception:
            ...


    device = usb.core.find(idVendor=IDS[0], idProduct=IDS[1])
    if device is not None:
        print("b")
        try:
            sendMeta(device)
            enableAccesoryMode(device)
            usb.util.dispose_resources(device)
        except Exception:
            ...
    sleep(0.1)