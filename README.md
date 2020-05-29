# Co-Video: a tool to share videos across the net 
Do you have a high resolution video (say a visualization) that you'd like to share with someone across the world? Does screen sharing with Zoom and Google Hangouts not give you the resolution you need? Well this little tool maybe able to help. 

With Co-video (Co-vid for short :)) a master video controller can stream a video with an arbitrary of viewers. 

## How do I use it? 
One you deploy on a server you will need to convert your video to HLS and host it in the `public` folder. See below for more details. 

To simplify deployment there is a `Dockerfile` to help with packaging.  

Once you have your video files and application deployed (say for eg to `my-server.com`) you can start a session by going to: 
`my-server.com?master`
and supplying the video name. A URL will be generated that you can share with others and once they join they will be seeing the same video at the same location. 

### Deployment with bucket backends 
Although you can directly copy the videos to the container there is support for Google Storage by using the `/gs/` prefix on the movie url.  

## What if I want to build on this tool? 
This is a very simple react/reagent application with a very small webserver thrown in to host the shared state and video files.  

### Development mode
To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```
lein figwheel
```

Figwheel will automatically push cljs changes to the browser.
Once Figwheel starts up, you should be able to open the `public/index.html` page in the browser.

If you start the server (`lein run`) you don't have to limit yourself to port 3449. 
### REPL

The project is setup to start nREPL on port `7002` once Figwheel starts.
Once you connect to the nREPL, run `(cljs)` to switch to the ClojureScript REPL.

### Building for production

```
lein clean
lein package
```

### Running: 
```bash
lein run 
```
will run a server on port 80 (no SSL yet)

### Converting videos to a streaming format: 
you need to install ffmpeg and run: 
```bash 
 ffmpeg -i input.m4v -c:v libx264 -crf 21 -preset veryfast -c:a aac -b:a 128k -ac 2 -f hls -hls_time 12 -hls_playlist_type event output.m3u8
``` 


you want to keep your configs in `config` folder