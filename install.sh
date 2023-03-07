#!/bin/bash

mkdir -p /usr/lib/gqthres

echo "For which user do you want to install gqthres?:"
read $username

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

mkdir -p /home/$username/.cache/gqthres
scp gqthres.log /home/$username/.cache/gqthres
chmod a+rw /home/$username/.cache/gqthres/gqthres.log

echo Installation completed
echo Please edit the config file /etc/gqthres.conf
