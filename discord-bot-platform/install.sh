#!/bin/bash

ARTIFACT_FILE="discord-bot-platform-all.jar"
ARTIFACT_FOLDER="build/libs"

INSTALLATION_FOLDER="$HOME/.local/dbp"

PROPERTIES_FILE="override.conf"
SYSTEMD_FILE="discord_bot_platform.service"
SYSTEMD_FOLDER="$HOME/.config/systemd/user"

echo "Building Server Executable"
sleep 2
../gradlew :discord-bot-platform:buildFatJar

if [ $? -ne 0 ]; then
    echo "Build was unsuccesful"
    exit $?
fi

echo "Creating $INSTALLATION_FOLDER"
mkdir -p $INSTALLATION_FOLDER
if [ $? -ne 0 ]; then
    echo "Could not create installation folder $INSTALLATION_FOLDER"
    exit $?
fi

touch $INSTALLATION_FOLDER/$PROPERTIES_FILE
if [ $? -ne 0 ]; then
    echo "Could not create configuration file $INSTALLATION_FOLDER/$PROPERTIES_FILE"
    exit $?
fi

export XDG_RUNTIME_DIR=/run/user/$(id -u $USER)
export DBUS_SESSION_BUS_ADDRESS=/run/user/$(id -u $USER)/bus

echo "Stopping server..."
systemctl --user stop $SYSTEMD_FILE
if [ $? -ne 0 ]; then
    echo "Could not stop service $SYSTEMD_FILE"
fi

echo "Copying file $ARTIFACT_FOLDER/$ARTIFACT_FILE to $INSTALLATION_FOLDER/$ARTIFACT_FILE"
cp $ARTIFACT_FOLDER/$ARTIFACT_FILE $INSTALLATION_FOLDER/$ARTIFACT_FILE
if [ $? -ne 0 ]; then
    echo "Could not copy artifact"
    exit $?
fi

echo "Installing systemd unit $SYSTEMD_FILE in $SYSTEMD_FOLDER/$SYSTEMD_FILE"
mkdir -p $SYSTEMD_FOLDER
cp -f $SYSTEMD_FILE $SYSTEMD_FOLDER/$SYSTEMD_FILE
if [ $? -ne 0 ]; then
    echo "Could not install service $SYSTEMD_FILE"
    exit $?
fi

echo "Starting server"
systemctl --user daemon-reload
systemctl --user restart $SYSTEMD_FILE
systemctl --user enable $SYSTEMD_FILE

echo "Some useful commands:"
echo "To view the latest logs"
echo "journalctl -e --user -u $SYSTEMD_FILE"
echo "To manage the service"
echo "systemctl --user stop/start $SYSTEMD_FILE"
