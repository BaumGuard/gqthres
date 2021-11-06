#!/bin/bash

cd src
javac -cp jsoup-1.13.1.jar gqthres.java
jar cfm gqthres.jar manifest *.class
cp gqthres.jar /usr/lib/gqthres

cd..

mkdir -p /usr/lib/gqthres


cp gqthres /usr/bin/gqthres
cp gqthres.conf /etc
cp gqthres.desktop /usr/share/applications
cp gqthres.png /usr/share/pixmaps

chmod +x /usr/bin/gqthres

echo Installation completed
echo Please edit the config file /etc/gqthres.conf
