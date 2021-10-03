#!/bin/bash

mkdir -p /usr/lib/gqthres

cp gqthres.jar /usr/lib/gqthres
cp gqthres /usr/bin/gqthres
cp gqthres.conf /etc
cp gqthres.desktop /usr/share/applications
cp gqthres.png /usr/share/pixmaps

chmod +x /usr/bin/gqthres

echo Installation completed
echo Please edit the config file /etc/gqthres.conf
