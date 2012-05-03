# Askeroids

```
   ,---.         ,--.                        ,--.   ,--.        
  /  O  \  ,---. |  |,-. ,---. ,--.--. ,---. `--' ,-|  | ,---.  
  |  .-.  |(  .-' |     /| .-. :|  .--'| .-. |,--.' .-. |(  .-'  
  |  | |  |.-'  `)|  \  \\   --.|  |   ' '-' '|  |\ `-' |.-'  `) 
  `--' `--'`----' `--'`--'`----'`--'    `---' `--' `---' `----'  
```
## Background

Back in the year 2000 I created a variant of the classic game Asteroids for a company I worked for called "Askeroids". I recently discovered the code and tried to "make" it and found, to my suprise, that it actually still worked. It is presented here for posterity. Java is required and preferably a Unix-like environment with "make".

![Screenshot](https://github.com/glynnbird/askeroids/blob/master/askeroids_screen_shot.png?raw=true "Askeroids Screenshot")

## Features

Askeroids isn't like normal Asteroids. Oh no.

* missiles are attracted to asteroids by gravity
* your space ship has one pull-along friend on the first level connected by an elastic string
* an additional pull along friend is added on each level
* a pull along friend is added on each new level
* occasionally bonus objects will appear on the screen
* occasionally a black hole will appear and all the asteroid matter will be attracted towards it
* at some point, your missiles will cause your ship to "recoil"

## Compiling

Clone the repository and type:
```
  make
```

## Running

You can either serve out the compiled version on a website or from the command-line:

```
  ./run.sh
```

which launches the applet in Java's "appletviewer".
