#!/bin/bash

echo "For which user do you want to uninstall gqthres?:"
read username

rm -r /usr/lib/gqthres
rm /usr/bin/gqthres
rm /etc/gqthres.conf
rm /usr/share/applications/gqthres.desktop
rm /usr/share/pixmaps/gqthres.png
rm -r /home/$username/.cache/gqthres
