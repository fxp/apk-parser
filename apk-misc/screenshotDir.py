from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
import os,sys
from os.path import join, getsize
device = MonkeyRunner.waitForConnection()

rootdir =sys.argv[1]
for root, dirs, files in os.walk(rootdir):
	print(files)