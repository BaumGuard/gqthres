#!/bin/bash

mkdir -p /usr/lib/gqthres

cd src
javac -cp jsoup-1.13.1.jar gqthres.java
jar cfm gqthres.jar manifest *.class
scp gqthres.jar /usr/lib/gqthres
scp jsoup-1.13.1.jar /usr/lib/gqthres

cd ..

scp gqthres /usr/bin/gqthres
scp gqthres.conf /etc
scp gqthres.desktop /usr/share/applications
scp gqthres.png /usr/share/pixmaps

chmod +x /usr/bin/gqthres

echo Installation completed
echo Please edit the config file /etc/gqthres.conf
