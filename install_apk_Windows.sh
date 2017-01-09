#!/bin/bash

function find_adb_path() {
	ADB_PATH=$(grep -i "sdk.dir\|sdkdir" ./*|sed -e 's/sdk.dir=/\t/g'|awk '{print $2}')
	echo ${ADB_PATH}\\platform-tools\\adb|sed -e 's/grep//g'|sed -e 's/$(//g'|sed -e 's/ADB_PATH=//g'|sed -e 's/\\\:/\:/g'
}

function get_adb_device_str() {
        GOODSTR=ZX1D52R96G
        if [ -f ./CURRENT_DEVICE ]; then
                . ./CURRENT_DEVICE
                adb connect $(echo $DEVICE|tr -d '\r') > /dev/null
        fi
        declare -a DEVICE_Array
        adb devices|grep -vi attached|awk '{print $1}' > /tmp/adb.devices
        DEVICE_Array=( $(cat /tmp/adb.devices|grep -v attached|awk '{print $1}') )
        DEVICE_grepd=$(cat /tmp/adb.devices|grep "${GOODSTR}")
        rm -f /tmp/adb.devices > /dev/null
        if [ -z "$DEVICE_grepd" ]; then
                for (( i=0; i < ${#DEVICE_Array[@]}; i++)); do
                        DEVICE=${DEVICE_Array[i]}
                        adb -s ${DEVICE} shell "cat /sys/class/android_usb/f_accessory/device/iSerial;exit" > /tmp/DEV.SER
                        DEVSERIAL=$(cat /tmp/DEV.SER)
                        if [ "$DEVSERIAL" == "$GOODSTR" ]; then
                                RETURN_STR=${DEVICE_Array[i]}
                        fi
                        rm -f /tmp/DEV.SER > /dev/null
                done
        else
                RETURNSTR=$GOODSTR
        fi
        echo $RETURNSTR
}

function is_DEVICE_an_IP() {
	STRING=$1
	STRING=$(echo $STRING|grep -wo "[0-9]*\.[0-9]*\.[0-9]*\.[0-9]*")
	echo ${#STRING[@]}
}

echo *************************************************************************************************************************
echo Keep in mind that this isn\'t a complete port of the original OSX command line version.  It\'ll also try to run the config
echo file generator, and then try to push that file to the phone, but that script doesn\'t know where your adb command is.
echo ''
echo First things first, be sure to put, in the root of the project, a file entitled \'local.properties\', which will contain
echo a line that looks like:
echo "		sdk.dir=S:\\SDKs"
echo For me, that is a remote share directory, where I keep my SDK libraries, because I\'m only running on 50GB of windows 
echo partioned space
echo ''
echo ''
read -n 1 -s -p "Press any key to continue"
echo **************************************************************************************************************************
echo ''
echo ''
echo ''
echo 'Locating path to adb:	'
ADB_PATH=$(find_adb_path)
DEVICE=$(get_adb_device_str)

./build_push_stdconfigxml.sh

if [ -z "$DEVICE" ]; then
	. ./CURRENT_DEVICE
fi

${ADB_PATH} -s "$DEVICE" install -r $(ls -a ./Team*/*apk)

if [ "$(is_DEVICE_an_IP $DEVICE)" == "1" ]; then
        echo Device is an IP address
	IF=$(networksetup -listnetworkserviceorder|grep Bluetooth|grep PAN|grep Device|sed -e 's/(Hardware Port\://g'|sed -e 's/Bluetooth PAN,//g'|grep Device:|sed -e 's/Device\://g'|sed -e 's/ //g'|sed -e 's/[\(\)]//g'|grep en)
	$DEV_IP
	echo interface: $IF
else
	DEV_IP=$(${ADB_PATH} shell 'ifconfig bt-pan|grep "net addr"')
	DEV_IP=$(echo $DEV_IP|awk '{print $2}'|sed -e 's/[a-zA-Z\:]//g')
	DEV_IP=$(echo $DEV_IP|tr -d '\r');

	DEV_BCST=$(${ADB_PATH} shell 'ifconfig bt-pan|grep "net addr"')
	DEV_BCST=$(echo $DEV_BCST|awk '{print $3}'|sed -e 's/a-zA-Z\://g')

	# My IP_addr should be
	# Perhaps the better solution would be from the methods used on Beagle Bone for the McQ Dynamic DNS workaround
	# 	Or maybe a 'Two Client only' IP scheme would be faster
	# This will do for now, assuming CIDR/24
	MY_IP_SB=$(echo $DEV_BCST|sed -e 's/255/1/g'|tr -d '\r')
	
	IF=$(networksetup -listnetworkserviceorder|grep Bluetooth|grep PAN|grep Device|sed -e 's/(Hardware Port\://g'|sed -e 's/Bluetooth PAN,//g'|grep Device:|sed -e 's/Device\://g'|sed -e 's/ //g'|sed -e 's/[\(\)]//g'|grep en)
	IP_ALREADY_SET=$(ifconfig "${IF}"|grep "${MY_IP_SB}")
	if [ "${#IP_ALREADY_SET}" == "0" ]; then
		echo Awe Crap, We werent setup.  Youre gonna need to enter your administrative password!
		sudo ifconfig "${IF}" "${MY_IP_SB}" up
	fi
		
	
	${ADB_PATH} -s "ZX1D52R96G" shell 'echo "setprop service.adb.tcp.port 5555;stop adbd;start adbd;exit"|su - root'
	adb kill-server
	adb tcpip 5555
	adb connect "$DEV_IP":5555

	echo DEVICE="${DEV_IP}:5555" > ./CURRENT_DEVICE
fi
