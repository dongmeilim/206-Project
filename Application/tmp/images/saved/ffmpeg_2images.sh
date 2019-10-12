#!/bin/bash

totalDuration=`soxi -D ../tmp/audio/concatenatedAudio.wav`
screentime=`echo "$totalDuration/2" | bc -l`
framerate=`echo "1/($screentime)" | bc -l`

fileName=$1
audioName=$2
videoName=$3

if [ $# -eq 3 ]
then
		cat ../tmp/images/saved/*.jpg | ffmpeg -f image2pipe -framerate $framerate -i - -i ../quiz/$fileName/$audioName -c:v libx264 -pix_fmt yuv420p -vf "scale=750:500" -r 25 -max_muxing_queue_size 1024 -y ../quiz/$fileName/$videoName &> /dev/null 
else
	cat ../tmp/images/saved/*.jpg | ffmpeg -f image2pipe -framerate $framerate -i - -i ../tmp/audio/finalAudio.wav -c:v libx264 -pix_fmt yuv420p -vf "scale=750:500" -r 25 -max_muxing_queue_size 1024 -y ../tmp/video/videoNoText.mp4 &> /dev/null 
fi


