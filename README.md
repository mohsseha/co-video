
### Development mode
To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```
lein figwheel
```

Figwheel will automatically push cljs changes to the browser.
Once Figwheel starts up, you should be able to open the `public/index.html` page in the browser.

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