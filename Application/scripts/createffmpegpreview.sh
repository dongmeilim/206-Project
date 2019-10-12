#!/bin/bash

query=`cat ../tmp/text/query`
haveMusicInVideo=$1 #Can either be 1 or 0.
#1 is true
#0 is false


rm ../tmp/audio/concatenatedAudio.wav #&> /dev/null
rm ../tmp/text/ffmpegAudioFiles #&> /dev/null
rm ../tmp/text/ffmpegImageFiles #&> /dev/null

mkdir -p ../tmp/images/saved/ #Won't show up when directory is listed as it is a directory

rm ../tmp/images/saved/*.jpg &> /dev/null

#Generate formatting for Audio [See Below]
for i in `cat ../tmp/text/audioFilesForVideo`
do
	echo "file '../audio/$i'" >> ../tmp/text/ffmpegAudioFiles
done

echo "20" #Percentage

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

echo "40" #Percentage

#Concatenate the audio into one file
ffmpeg -f concat -safe 0 -i ../tmp/text/ffmpegAudioFiles -c copy -y ../tmp/audio/concatenatedAudio.wav &> /dev/null

echo "60" #Percentage

#Get the duration of all the audio
totalDuration=`soxi -D ../tmp/audio/concatenatedAudio.wav` 
if [ $haveMusicInVideo -eq 1 ]
then
	ffmpeg -i ../assets/jlbrock44_-_Staying_Positive.mp3 -ss 0 -t $totalDuration -y ../tmp/audio/truncatedTrack.wav &> /dev/null
	ffmpeg -i ../tmp/audio/truncatedTrack.wav -filter:a "volume=0.12" -y ../tmp/audio/quietBackground.wav &> /dev/null
	ffmpeg -i ../tmp/audio/concatenatedAudio.wav -i ../tmp/audio/quietBackground.wav -filter_complex amix=inputs=2:duration=longest -y ../tmp/audio/finalAudio.wav &> /dev/null
else
	ffmpeg -i ../tmp/audio/concatenatedAudio.wav -y ../tmp/audio/finalAudio.wav #&>/dev/null
fi

#Get number of images
imageAmount=`cat ../tmp/text/imageFilesForVideo | wc -w`

# duration / number of images = durationperImage
durationPerImage=`echo $totalDuration/$imageAmount | bc -l`

#Framerate = 1/durationperImage
frameRate=`echo 1/$durationPerImage | bc -l`
if [ $imageAmount -eq 1 ] #When there is 1 image
then
	ffmpeg -framerate $frameRate -i ../tmp/images/saved/img001.jpg -i ../tmp/audio/finalAudio.wav -vf "scale=750:500,fps=25" -c:v libx264 -pix_fmt yuv420p -y ../tmp/video/videoNoText.mp4 #&> /dev/null
elif [ $imageAmount -eq 2 ] #When there are 2
then
	bash ../tmp/images/saved/ffmpeg_2images.sh
else
	ffmpeg -framerate $frameRate -i ../tmp/images/saved/img%03d.jpg -i ../tmp/audio/finalAudio.wav -vcodec libx264 -pix_fmt yuv420p -vf "scale=750:500:force_original_aspect_ratio=increase,crop=750:500" -r 25 -max_muxing_queue_size 1024 -y ../tmp/video/videoNoText.mp4 #&> /dev/null
fi

echo "80" #Percentage

#Overlay text
ffmpeg -i ../tmp/video/videoNoText.mp4 -filter_complex "drawtext=text='$query':fontcolor=white:fontsize=72:shadowcolor=black:shadowx=2:shadowy=2:x=(w-text_w)/2:y=(h-text_h)/2" -max_muxing_queue_size 1024 -y ../tmp/video/videoWithText.mp4 &> /dev/null

echo "100" #Percentage
sleep 0.5 #So user sees the 100% percent
#================================================

#Format for files in ffmpegAudioFiles:
: '
file '../audio/duck.wav'
file '../audio/foo.wav'
file '../audio/bar.wav' 
file '../audio/audio.wav'
'
