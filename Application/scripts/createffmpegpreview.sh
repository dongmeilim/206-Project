#!/bin/bash

query=`cat ../tmp/text/query`

rm ../tmp/audio/concatenatedAudio.wav &> /dev/null
rm ../tmp/text/ffmpegAudioFiles &> /dev/null
rm ../tmp/text/ffmepgImageFiles &> /dev/null

mkdir -p ../tmp/images/saved/ #Won't show up when directory is listed as it is a directory

rm ../tmp/images/saved/*.jpg &> /dev/null

#Generate formatting for Audio [See Below]
for i in `cat ../tmp/text/audioFilesForVideo`
do
	echo "file '../audio/$i'" >> ../tmp/text/ffmpegAudioFiles
done

echo "25" #Percentage

#Generate formatting for Images { img001.jpg, img 002.jpg, ..., img010.jpg }
counter=1
for i in `cat ../tmp/text/imageFilesForVideo`
do
	echo "file '../images/$i'" >>  ../tmp/text/ffmpegImageFiles
	if [ $counter -eq 10 ]
	then
		cp ../tmp/images/$i ../tmp/images/saved/img0$counter.jpg &> /dev/null
	else
		cp ../tmp/images/$i ../tmp/images/saved/img00$counter.jpg &>/dev/null
	fi
	counter=$((counter+1))
done

echo "50" #Percentage

#Concatenate the audio into one file
ffmpeg -f concat -safe 0 -i ../tmp/text/ffmpegAudioFiles -c copy -y ../tmp/audio/concatenatedAudio.wav &> /dev/null

echo "75" #Percentage

#Get the duration of all the audio
totalDuration=`soxi -D ../tmp/audio/concatenatedAudio.wav`

#Get number of images
imageAmount=`cat ../tmp/text/imageFilesForVideo | wc -w`

# duration / number of images = durationperImage
durationPerImage=`echo $totalDuration/$imageAmount | bc -l`

#Framerate = 1/durationperImage
frameRate=`echo 1/$durationPerImage | bc -l`
if [ $imageAmount -eq 1 ] #When there is 1 image
then
	ffmpeg -framerate $frameRate -i ../tmp/images/saved/img001.jpg -i ../tmp/audio/concatenatedAudio.wav -vf "scale=750:500,fps=25" -c:v libx264 -pix_fmt yuv420p -y ../tmp/video/videoNoText.mp4 &> /dev/null
elif [ $imageAmount -eq 2 ] #When there are 2
then
	bash ../tmp/images/saved/ffmpeg_2images.sh
else
	ffmpeg -framerate $frameRate -i ../tmp/images/saved/img%03d.jpg -i ../tmp/audio/concatenatedAudio.wav -vcodec libx264 -pix_fmt yuv420p -vf scale=750:500 -r 25 -max_muxing_queue_size 1024 -y ../tmp/video/videoNoText.mp4 #&> /dev/null
fi

echo "100" #Percentage

#Overlay text
ffmpeg -i ../tmp/video/videoNoText.mp4 -filter_complex "drawtext=text='$query':fontcolor=white:fontsize=72:shadowcolor=black:shadowx=2:shadowy=2:x=(w-text_w)/2:y=(h-text_h)/2" -max_muxing_queue_size 1024 -y ../tmp/video/videoWithText.mp4 &> /dev/null

#Percentage finishes early so that user can see it


#================================================

#Format for files in ffmpegAudioFiles:
: '
file '../audio/duck.wav'
file '../audio/foo.wav'
file '../audio/bar.wav' 
file '../audio/audio.wav'
'
