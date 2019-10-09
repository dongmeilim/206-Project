#!/bin/bash

fileName=$1
videoName=$fileName.mp4
query=`cat ../tmp/text/query`
imageName=$query.jpg

mkdir -p ../quiz/$fileName &> /dev/null

cp ../tmp/video/videoWithText.mp4 ../creations/$videoName &> /dev/null
echo "33" #Percentage

cp ../tmp/images/saved/img001.jpg ../quiz/$fileName/$imageName &> /dev/null
echo "66" #Percentage

echo "100" #Percentage
