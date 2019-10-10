#!/bin/bash

fileName=$1
creation=$fileName.mp4
query=`cat ../tmp/text/query`
imageName=$query.jpg
audioName=$query.wav
videoName=$query.mp4

rm ../tmp/text/censoredAudioFiles &> /dev/null

mkdir -p ../quiz/$fileName &> /dev/null

#CREATION IN CREATION FOLDER
cp ../tmp/video/videoWithText.mp4 ../creations/$creation &> /dev/null
echo "25" #Percentage

#THUMBNAIL AS IMAGE TEST
cp ../tmp/images/saved/img001.jpg ../quiz/$fileName/$imageName &> /dev/null
echo "50" #Percentage

#CENSORED AUDIO AS AUDIO TEST
for censoredFile in `ls ../tmp/audio/censored/ | cat`
do
	echo "file '../audio/censored/$censoredFile'" >> ../tmp/text/censoredAudioFiles
done

#Concatenate the audio into one file
ffmpeg -f concat -safe 0 -i ../tmp/text/censoredAudioFiles -c copy -y ../quiz/$fileName/$audioName &> /dev/null
echo "75" #Percentage

#CENSORED AUDIO + SILENT VIDEO AS VIDEOTEST

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
	ffmpeg -framerate $frameRate -i ../tmp/images/saved/img001.jpg -i ../quiz/$fileName/$audioName -vf "scale=750:500,fps=25" -c:v libx264 -pix_fmt yuv420p -y ../quiz/$fileName/$videoName &> /dev/null
elif [ $imageAmount -eq 2 ] #When there are 2
then
	bash ../tmp/images/saved/ffmpeg_2images.sh $fileName $audioName $videoName #passed as parameters such that the program executes differently
else
	ffmpeg -framerate $frameRate -i ../tmp/images/saved/img%03d.jpg -i ../quiz/$fileName/$audioName -vcodec libx264 -pix_fmt yuv420p -vf scale=750:500 -r 25 -max_muxing_queue_size 1024 -y ../quiz/$fileName/$videoName &> /dev/null
fi
echo "100" #Percentage
