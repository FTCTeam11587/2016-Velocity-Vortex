#!/bin/bash

ANDROID_FIRST_DIRECTORY=/storage/emulated/0/FIRST

THIS_DIR=$(cd "$(dirname "$0")" ; pwd -P)
TEAM_DIR=$(ls $THIS_DIR|grep -i Team)
OPMODES_DIR=${THIS_DIR}/${TEAM_DIR}/src/main/java/org/firstinspires/ftc
TEAMNUMBER=$(ls -a $OPMODES_DIR|grep -i team[0-9]|sed -e 's/[A-Za-z]//g')
OPMODES_DIR=${OPMODES_DIR}/$(ls -a $OPMODES_DIR|grep -i team[0-9])
OUR_CONFIG=$OPMODES_DIR/${TEAMNUMBER}.xml

CURRENT_DIR=$(pwd)
mkdir -p ${CURRENT_DIR}/.tmp
cd ${CURRENT_DIR}/.tmp/
adb shell "ls ${ANDROID_FIRST_DIRECTORY}/*.xml"|tr -d '\r' > ./LISTING
while read REMOTE_CONFIG; do
	REMOTE_CONFIG=$(echo $REMOTE_CONFIG|tr -d '\r')
	adb pull "$REMOTE_CONFIG"
done <./LISTING
rm -f ./LISTING
cat ./*.xml > ./XMLCOMBO
declare -a TYPE
declare -a SERIAL
declare -a NAME
cat ./XMLCOMBO|grep -i Serial|sort|uniq > ./SerialLines
cat ./SerialLines|awk '{print $1}'|sed -e 's/\<//g' > ./TYPES
cat ./SerialLines|sed -e 's/.*name\=/name\=/g'|sed -e 's/serialNumber.*\>//g'|sed -e 's/name\=//g'|sed -e 's/[aAeEiIoOuU]//g'|sed -e 's/ /_/g'|sed -e 's/\"_//g'|sed -e 's/\"//g'|awk '{print toupper($0)}'|sed -e 's/NTRFC/IF/g'|sed -e 's/DVC/DEV/g'|sed -e 's/MDL/MOD/g' > ./NAMES
cat ./SerialLines|sed -e 's/.*serial/serial/g'|sed -e 's/serialNumber\=//g'|sed -e 's/\>.*//g'|sed -e 's/\"//g' > ./SERIALS
SERIAL=( $(cat ./SERIALS) )
NAME=( $(cat ./NAMES) )
TYPE=( $(cat ./TYPES) )

# Reads and Fixes Types listed in our Core Device Name file, if types dont match what the phone populated
# the config file with, for each Serial Number
for (( i=0; i < ${#SERIAL[@]}; i++)); do
	KNOWN_SERIAL=$(cat ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt|grep ${SERIAL[i]}|awk '{print $3}')
	KNOWN_TYPE=$(cat ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt|grep ${SERIAL[i]}|awk '{print $1}')
	if [ "${KNOWN_TYPE}" == "${TYPE[i]}" ]; then
		echo ''>/dev/null
	else
		KNOWN_NAME=$(cat ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt|grep ${SERIAL[i]}|awk '{print $2}')
		KNOWN_TYPE=$(cat ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt|grep ${SERIAL[i]}|awk '{print $1}')
		cat ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt|grep -v ${SERIAL[i]} > ./CDN.tmp
		echo "${TYPE[i]}			${KNOWN_NAME}	${SERIAL[i]}" >> ./CDN.tmp
		mv ./CDN.tmp ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt
	fi
done

# Shift Core Device Numbers to lowest, if not already
cat ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt|grep "#" > ./HEADING
cat ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt|grep -i MotorController|sort|awk '{print $2,$3,$1}'|sort|cat -n > ./MOTORCONTROLLERS
cat ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt|grep -i ServoController|sort|awk '{print $2,$3,$1}'|sort|cat -n > ./SERVOCONTROLLERS
cat ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt|grep -i DeviceInterfaceModule|sort|awk '{print $2,$3,$1}'|sort|cat -n > ./DEVIFMODS
cat ./HEADING > ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt
cat {./MOTORCONTROLLERS,./SERVOCONTROLLERS,./DEVIFMODS}|sed -e 's/_[0-9]/_/g'|awk '{print $4,$2,$1,$3}'|sed -e 's/_ /_/g'|awk '{print $1"\t\t\t"$2"\t\t"$3}' >> ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt

# And because this is a pretty long string, we'll remove the extra two tabs so it looks pretty!
sed -i '' -e 's/DeviceInterfaceModule			/DeviceInterfaceModule	/g' ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt
cat ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt|awk '{print $1,$2,$3}'|grep -v "#"

cd $CURRENT_DIR
rm -rf ./.tmp

cat $OPMODES_DIR/*.java|grep "// build config profile"|grep -vi "//.*="|sed -e 's/hardwareMap\.//g'|sed -e 's/\.get//g' \
	> $OPMODES_DIR/PORTNAMES.tmp

echo -e "\n"
cat $OPMODES_DIR/PORTNAMES.tmp|awk '{print $3}'|sed -e 's/(/ /g'|sed -e 's/)//g'|sed -e 's/;//g'|sed -e 's/\"//g' > ./VARIABLES
sed -i '' -e 's/dcMotor/Motor/g' ./VARIABLES
sed -i '' -e 's/servo/Servo/g' ./VARIABLES
sed -i '' -e 's/digitalChannel/DigitalDevice/g' ./VARIABLES
sed -i '' -e 's/PWMOutput/PulseWidthDevice/g' ./VARIABLES
cat ./VARIABLES
cat ./VARIABLES|awk '{print $1}'>./TYPES
cat ./VARIABLES|awk '{print $2}'>./NAMES
declare -a TYPE_insrc
declare -a NAME_insrc
TYPE_insrc=( $(cat ./TYPES) );
NAMES_insrc=( $(cat ./NAMES) );
#unset i
rm -f ./xml.tmp
for (( i=0; i < ${#TYPE_insrc[@]}; i++)); do
	#echo ${NAMES_insrc[i]}	${TYPE_insrc[i]}
	LINE=$(grep ${NAMES_insrc[i]} ${OPMODES_DIR}/DEVICES.txt|grep ${TYPE_insrc[i]})
	if [ ! -z "$LINE" ]; then
		PARENT_NAME=$(echo $LINE|awk '{print $4}')
		PARENT_TYPE=$(grep ${PARENT_NAME} ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt|awk '{print $1}')
		PARENT_SERIAL=$(grep ${PARENT_NAME} ${OPMODES_DIR}/CORE_DEVICE_NAMES.txt|awk '{print $3}')
		DEVICE_NAME=${NAMES_insrc[i]}
		DEVICE_TYPE=${TYPE_insrc[i]}
		DEVICE_PORT_NUM=$(grep ${DEVICE_NAME} ${OPMODES_DIR}/DEVICES.txt|grep ${DEVICE_TYPE}|awk '{print $3}')
		echo -e "$PARENT_TYPE	$PARENT_NAME	$PARENT_SERIAL	$DEVICE_TYPE	$DEVICE_NAME	$DEVICE_PORT_NUM" >> ./xml.tmp
	fi
done
cat ./xml.tmp

declare -a PARENT_NAME
PARENT_NAME=( $(cat ./xml.tmp|awk '{print $2}'|sort|uniq) )
echo -e "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>" > $OUR_CONFIG
echo -e "<Robot type=\"FirstInspires-FTC\">" >> $OUR_CONFIG
for (( i=0; i<${#PARENT_NAME[@]}; i++)); do
	PARENT_SERIAL=$(grep ${PARENT_NAME[i]} ./xml.tmp|awk '{print $3}'|sort|uniq)
	PARENT_TYPE=$(grep ${PARENT_NAME[i]} ./xml.tmp|grep $PARENT_SERIAL|sort|uniq|awk '{print $1}'|sort|uniq)
	PARENT_LINE_HDR_STR=$(echo -e "<${PARENT_TYPE} name=\"${PARENT_NAME[i]}\" serialNumber=\"${PARENT_SERIAL}\">")
	PARENT_LINE_FTR_STR=$(echo -e "</${PARENT_TYPE}>")
	echo "    "$PARENT_LINE_HDR_STR  >> $OUR_CONFIG
	declare -a DEVICE_NAME
	DEVICE_NAME=( $(cat ./xml.tmp|grep "${PARENT_SERIAL}"|grep "${PARENT_TYPE}"|awk '{print $5}'|sort|uniq) )
	for (( ii=0; ii<${#DEVICE_NAME[@]}; ii++));do
		DEVICE_TYPE=$(cat ./xml.tmp|grep "${DEVICE_NAME[ii]}"|grep "${PARENT_SERIAL}"|grep "${PARENT_TYPE}"|sort|uniq|awk '{print $4}')
		DEVICE_PORT_NUM=$(cat ./xml.tmp|grep "${DEVICE_NAME[ii]}"|grep "${PARENT_SERIAL}"|grep "${PARENT_TYPE}"|sort|uniq|awk '{print $6}')
		DEVICE_LINE_STR=$(echo -e "<${DEVICE_TYPE} name=\"${DEVICE_NAME[ii]}\" port=\"${DEVICE_PORT_NUM}\" />")
		echo "        "$DEVICE_LINE_STR >> $OUR_CONFIG
	done

	echo "    "$PARENT_LINE_FTR_STR >> $OUR_CONFIG
done
echo -e "</Robot>" >> $OUR_CONFIG

OUR_CONFIG=$(echo $OUR_CONFIG|tr -d '\r')
OUR_REMOTE_CONFIG=${ANDROID_FIRST_DIRECTORY}/STANDARD.xml
OUR_REMOTE_CONFG=$(echo ${OUR_REMOTE_CONFG}|tr -d '\r')
echo adb push $OUR_CONFIG $OUR_REMOTE_CONFIG
adb push "$OUR_CONFIG" "$OUR_REMOTE_CONFIG"

rm -f ./xml.tmp
rm -f ./TYPES
rm -f ./NAMES
rm -f ${OPMODES_DIR}/PORTNAMES.tmp
rm -f ./VARIABLES

