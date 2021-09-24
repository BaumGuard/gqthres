# gqthres
Resolver for locators, callsigns and coordinates<br />
Click [here](https://github.com/BaumGuard/qthres) to get to the former command line tool `qthres`.
<br />
## Functions<br />
- Conversion between Maidenhead locators and coordinates
- Retrieving the position and nationality of a callsign from [qrzcq.com](https://qrz.com)
- Calculating the distance (km or miles) between you and the callsign/locator
- Displaying the direct distance on [luftlinie.org](https://luftlinie.org)
- Opening the web instance of the callsign on [qrz.com](https://qrz.com) or [qrzcq.com](https://qrzcq.com)
## Dependencies<br />
- Java (JRE)
- Firefox/Chromium (Only Firefox on Windows)
## Installation<br />
Go to your home directory:<br />
`cd ~`<br />
Download `gqthres` as a zip archive or use `git` to download it:<br />
`git clone https://www.github.com/BaumGuard/gqthres`
<br />
## Configuration<br />
Before you start `gqthres` for the first time you have to enter your coordinates in the config file `gqthres.properties`<br />
You can also change the other settings according to your preferences.
## How to use<br />
You can either start `gqthres` by doubleclicking `gqthres.jar` or by starting it from the command line:<br />
`java -jar gqthres.jar`
1. Choose in the drop-down menu whether you want to enter a locator, a callsign or coordinates
2. Enter the locator/callsign/coordinates in the text field
3. Press `OK`

Now you should see some output data in the three text fields. You can now click the buttons on the bottom to show the distance on a map or open the web instances of the callsign.<br />
**Resolving callsigns only works with an internet connection!**
## Start it from anywhere<br />
You can add this line in `~/.bashrc` to start `gqthres` just by running "gqthres" in the command line:<br />
`alias gqthres='java -jar ~/gqthres/gqthres.jar'`<br />
After having added the line and saved the file, you have to execute this command to be able to run "gqthres":<br />
`exec bash`
## Troubleshooting<br />
If you have started `gqthres` by doubleclicking it you won't see any error messages, but if you have started it from the command line you will see a stacktrace there. A sign for an error are the three empty output text fields after you have entered a value and clicked `OK`.<br />
Always enter the right value that corresponds to the item you have selected in the drop-down menu.<br />
Some callsigns aren't present on [qrzcq.com](https://qrzcq.com) and consequently the locator can not be retrieved.<br />
Please also make sure that all items in `gqthres.properties` have the format `key=value` and pay attention to the lower and upper cases.
