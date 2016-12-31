#!/bin/bash

./build_push_stdconfigxml.sh
adb install -r $(ls -a ./Team*/*apk)
