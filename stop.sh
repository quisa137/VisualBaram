#!/bin/bash
kill $( jps -m | grep 'WebServerLauncher' | awk '{print $1}')
