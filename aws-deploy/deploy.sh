#!/bin/bash
MODULE=$1
VERSION=$2
TARGET=./$MODULE/target/aws-bundle
WORKING_DIR=$(pwd)
echo "Working directory is: " $WORKING_DIR
echo "module is: " $MODULE
echo "Version is: " $VERSION

mkdir $TARGET
echo "copying ./"$MODULE"/target/"$MODULE-$VERSION".jar to app.jar"
cp ./$MODULE/target/$MODULE-$VERSION.jar $TARGET/app.jar

echo "create Procfile"
printf "web: java -jar app.jar server /tmp/config.yml" > $TARGET/Procfile

echo "create .ebextensions folder with config file"
mkdir $TARGET/.ebextensions
cp ./aws-deploy/$MODULE-storage.config $TARGET/.ebextensions/storage.config

echo "put app.jar and Procfile and .ebextensions into aws-bundle.zip"
#zip -j ./$WORKING_DIR/target/bundle.zip ./$WORKING_DIR/target/app.jar ./$WORKING_DIR/target/Procfile ./$WORKING_DIR/target/.ebextensions
cd $TARGET
zip -r ../aws-bundle * .ebextensions/*
cd $WORKING_DIR
echo "Finished! Current directory is: " $WORKING_DIR
