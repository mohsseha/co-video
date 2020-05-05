#!/bin/bash

mkdir $1.hls
mv "$1" "$1".hls/"$1"
cd "$1".hls
ffmpeg -i $1 -c:v libx264 -crf 21 -preset fast -c:a aac -b:a 128k -ac 2 -f hls -hls_time 12 -hls_playlist_type event $1.m3u8 && rm $1
cd .. 
