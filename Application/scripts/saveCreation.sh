#!/bin/bash

fileName=$1
videoName=$fileName.mp4
query=`cat ../tmp/text/query`
imageName=$query.jpg
cp ../tmp/video/videoWithText.mp4 ../creations/$videoName &> /dev/null
echo "33" #Percentage

cp ../tmp/images/saved/img001.jpg ../quiz/imageTests/$imageName &> /dev/null
echo "66" #Percentage

echo "100" #Percentage
