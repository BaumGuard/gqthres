import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jsoup.Jsoup;

public class gqthresWin extends Frame implements ActionListener {

	// Characters for the Maidenhead locators
	static String[] Capitals = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
			"R", "S", "T", "U", "V", "W", "X" };
	static double latComp;
	static double longComp;
	static String coordinates;
	static double distance;

	// Downloading the callsign's page from qrzcq.com
	public static String[] GetCoordinates(String callsign) {

		String url = "https://www.qrzcq.com/call/" + callsign;

		String html = null;

		int csindex;

		try {
			html = Jsoup.connect(url).get().html();

		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

		csindex = html.indexOf("Latitude");
		String latqrzcq = html.substring(csindex + 45, csindex + 54);
		csindex = html.indexOf("Longitude");
		String lonqrzcq = html.substring(csindex + 46, csindex + 55);
		
		csindex = html.indexOf("</b><br");
		String extCountry = html.substring(csindex + 10, csindex + 80);
		
		if (extCountry.indexOf("<br>") != extCountry.lastIndexOf("<br>")) {
			extCountry=extCountry.replaceFirst("<br>", "");
		}
		
		String country = extract(extCountry,"<br>","</p>");
		country = country.replace("amp;", "");
		country = country.replace(",", " -");
		
		
	       	String regex = ".*[a-zA-Z].*";  // regex to check if string contains any letters
	        Pattern letterPattern = Pattern.compile(regex);  // compiles the regex
	       
	        Matcher matcherLat = letterPattern.matcher(latqrzcq);
	        Boolean latMatches = matcherLat.matches();
	        
	        Matcher matcherLon = letterPattern.matcher(lonqrzcq);
	        Boolean lonMatches = matcherLat.matches();

	        Pattern numPattern = Pattern.compile("\\d+.\\d+");
	        
	        
	        
	        if (latMatches=true) {
			
			Matcher matchLat = numPattern.matcher(latqrzcq);
	        while(matchLat.find()) {
	            latqrzcq=matchLat.group();
	        }
	        }
	        
	        if (lonMatches==true) {
	        
			Matcher matchLon = numPattern.matcher(lonqrzcq);
	        while(matchLon.find()) {
	            lonqrzcq=matchLon.group();
	        }
	        }
		
	        if (Double.parseDouble(latqrzcq) < -90 || Double.parseDouble(latqrzcq) > 90) {
	        	String swapLatLon=latqrzcq;
	        	latqrzcq=lonqrzcq;
	        	lonqrzcq=swapLatLon;
	        }
		
		String [] outputArray = {latqrzcq,lonqrzcq,country};

		return outputArray;
	}


	// Convert locator to coordinates
	public static String Convert(String inputStr) {

		// Split the entered locator into the separate characters
		String[] split = inputStr.split("");

		// Assigning the index for upcoming operations
		double longitudeIndex = Arrays.binarySearch(Capitals, split[0].toUpperCase());
		double latitudeIndex = Arrays.binarySearch(Capitals, split[1].toUpperCase());

		// Declaration of indexes
		double longSmall;
		double latSmall;
		double longSmallIndex;
		double latSmallIndex;
		double longNumber;
		double latNumber;

		// Converting to degrees
		if (split.length <= 5) {
			longSmallIndex = 0;
			latSmallIndex = 0;
		} else {
			longSmallIndex = Arrays.binarySearch(Capitals, split[4].toUpperCase());
			latSmallIndex = Arrays.binarySearch(Capitals, split[5].toUpperCase());
		}

		double longCap = 20 * longitudeIndex;
		double latCap = 10 * latitudeIndex;

		if (split.length <= 3) {
			longNumber = 0;
			latNumber = 0;
		} else {
			longNumber = Float.parseFloat(split[2]) * 2;
			latNumber = Float.parseFloat(split[3]);
		}
		;

		longSmall = longSmallIndex * (1 / 12.0);
		latSmall = latSmallIndex * (1 / 24.0);

		latComp = latCap + latNumber + latSmall - 90;
		longComp = longCap + longNumber + longSmall - 180;
		latComp = Math.round(latComp * 10000) / 10000.0;
		longComp = Math.round(longComp * 10000) / 10000.0;

		coordinates = latComp + "," + longComp;
		// The output of the method "Convert" is a String with the format
		// latitude,longitude
		return coordinates;

	}

	// Convert coordinats to locator
	public static String CoordsToLoc(double lat, double lon) {

		lon = lon + 180;
		lat = lat + 90;

		String Lon1Letter = Capitals[(int) Math.floor(lon / 20)];
		String Lat1Letter = Capitals[(int) Math.floor(lat / 10)];
		int LonNumber = (int) Math.floor((lon / 2) % 10);
		int LatNumber = (int) Math.floor(lat % 10);
		String Lon2Letter = Capitals[(int) Math.floor(((lon % 2) / 2) * 24)];
		String Lat2Letter = Capitals[(int) Math.floor(((lat % 1) * 24))];
		String loc = Lon1Letter + Lat1Letter + LonNumber + LatNumber + Lon2Letter + Lat2Letter;

		return loc;

	}

	// Calculate the distance form home to the locator/coordinates/callsign
	public static double Distance(double qthlat, double qthlon, double latComp, double longComp) {

		double latCompRad = Math.toRadians(latComp);
		double longCompRad = Math.toRadians(longComp);
		double qthlatRad = Math.toRadians(qthlat);
		double qthlonRad = Math.toRadians(qthlon);

		distance = (Math
				.acos(Math.sin(latCompRad) * Math.sin(qthlatRad)
						+ Math.cos(latCompRad) * Math.cos(qthlatRad) * Math.cos(qthlonRad - longCompRad))
				/ (2 * Math.PI)) * 40030;
		distance = Math.round(distance * 100) / 100.0;

		return distance;
	}
	
	
	public static String extract (String input, String char1, String char2) {
		
		int ind1 = input.indexOf(char1);
		int ind2 = input.indexOf(char2);
		int l1 = char1.length();
		
		String output = input.substring(ind1+l1, ind2);
		
		return output;
	}

	// GUI elements

	JFrame frame = new JFrame();
	JPanel panel = new JPanel();

	// Labels
	JLabel locOutLabel;
	JLabel coordOutLabel;
	JLabel distLabel;
	JLabel countryLabel = new JLabel("Country");

	// Buttons
	JButton startButton = new JButton("OK");
	JButton mapButton = new JButton("Show on map");
	JButton qrzButton = new JButton("Show on qrz.com");
	JButton qrzcqButton = new JButton("Show on qrzcq.com");

	// Textfields
	JTextField inputField = new JTextField();
	JTextField locOutField = new JTextField();
	JTextField coordOutField = new JTextField();
	JTextField distOutField = new JTextField();
	JTextField countryField = new JTextField();

	// Dropdown menu
	String inputArray[] = { "Locator", "Callsign", "Coordinates" };
	String unitArray[] = { "km", "miles" };
	JComboBox<String> InputBox = new JComboBox<>(inputArray);
	JComboBox<String> unitBox = new JComboBox<>(unitArray);

	public gqthresWin() {

		// Initializing the GUI elements
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);

		panel.setLayout(new GridBagLayout());

		locOutLabel = new JLabel("Locator");
		coordOutLabel = new JLabel("Coordinates");
		distLabel = new JLabel("Distance");

		mapButton = new JButton("Show on map");
		qrzButton = new JButton("Show on qrz.com");
		qrzcqButton = new JButton("Show on qrzcq.com");

		// Aligning the elements in the window
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(InputBox, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(inputField, gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		panel.add(startButton, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(countryLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		panel.add(countryField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(locOutLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		panel.add(coordOutLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		panel.add(distLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		panel.add(locOutField, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		panel.add(coordOutField, gbc);

		gbc.gridx = 1;
		gbc.gridy = 4;
		panel.add(distOutField, gbc);

		gbc.gridx = 2;
		gbc.gridy = 4;
		panel.add(unitBox, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		panel.add(mapButton, gbc);

		gbc.gridx = 1;
		gbc.gridy = 5;
		panel.add(qrzButton, gbc);

		gbc.gridx = 2;
		gbc.gridy = 5;
		panel.add(qrzcqButton, gbc);

		frame.add(panel);
		frame.setTitle("Gqthres");
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		String homePath = System.getProperty("user.home");
		ImageIcon icon = new ImageIcon(homePath+"/\\gqthres/\\gqthres.png");
		frame.setIconImage(icon.getImage());

		// Preparing the GUI with the user's preferences (gqthres.properties)
		FileInputStream fis = null;
		Properties prop = new Properties();

		try {
			fis = new FileInputStream(homePath+"/\\gqthres/\\gqthres.conf");
			prop.load(fis);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double latitude = Double.parseDouble(prop.getProperty("latitude"));
		double longitude = Double.parseDouble(prop.getProperty("longitude"));
		String valueType = prop.getProperty("valueType");
		String unitType = prop.getProperty("unitType");
		String browser = prop.getProperty("browser");
		String logging = prop.getProperty("logging");
		
		if (latitude==0 && longitude==0) {
	        JDialog warnDialog = new JDialog();
	        warnDialog.setTitle("First start");
	        warnDialog.setSize(400,50);
	        warnDialog.setModal(true);
	        warnDialog.add(new JLabel("Please enter your coordinates in /etc/gqthres.conf."));
	        warnDialog.setLocationRelativeTo(null);
	        warnDialog.setVisible(true);}

		InputBox.setSelectedItem(valueType);
		unitBox.setSelectedItem(unitType);

		mapButton.setEnabled(false);
		qrzButton.setEnabled(false);
		qrzcqButton.setEnabled(false);
		
		inputField.requestFocusInWindow();
		
		AbstractAction action = new AbstractAction()
		{
		    @Override
		    public void actionPerformed(ActionEvent e)
		    {

				// String dateLog;
				String locatorLog;
				String coordsLog;

				LocalDate date = LocalDate.now();
				LocalTime time = LocalTime.now();

				// Defining actions for the buttons

				locOutField.setText("");
				coordOutField.setText("");
				distOutField.setText("");
				countryField.setText("");
				locOutField.setEnabled(true);
				coordOutField.setEnabled(true);
				distOutField.setEnabled(true);
				countryField.setEnabled(true);

				if (InputBox.getSelectedItem().equals("Locator")) {

					String target[] = Convert(inputField.getText()).split(",");
					double targetLat = Double.parseDouble(target[0]);
					double targetLon = Double.parseDouble(target[1]);

					coordOutField.setText(Convert(inputField.getText()));

					double dist = Distance(latitude, longitude, targetLat, targetLon);

					if (unitBox.getSelectedItem().equals("miles")) {
						dist = Math.round((dist * 0.6214) * 100) / 100.0;
					}
					distOutField.setText(Double.toString(dist));

					locOutField.setEnabled(false);
					countryField.setEnabled(false);

					mapButton.setEnabled(true);
					qrzButton.setEnabled(false);
					qrzcqButton.setEnabled(false);

				}

				if (InputBox.getSelectedItem().equals("Callsign")) {
					String [] coordsInput = GetCoordinates(inputField.getText());


					double targetLat = Double.parseDouble(coordsInput[0]);
					double targetLon = Double.parseDouble(coordsInput[1]);

					locOutField.setText(CoordsToLoc(targetLat, targetLon));
					coordOutField.setText(coordsInput[0]+","+coordsInput[1]);
					double dist = Distance(latitude, longitude, targetLat, targetLon);

					if (unitBox.getSelectedItem().equals("miles")) {
						dist = Math.round((dist * 0.6214) * 100) / 100.0;
					}
					distOutField.setText(Double.toString(dist));

					try {
					countryField.setText(coordsInput[2]);}
					catch (Exception e1) {};

					mapButton.setEnabled(true);
					qrzButton.setEnabled(true);
					qrzcqButton.setEnabled(true);
				}

				if (InputBox.getSelectedItem().equals("Coordinates")) {
					String target[] = inputField.getText().split(",");
					double targetLat = Double.parseDouble(target[0]);
					double targetLon = Double.parseDouble(target[1]);

					double dist = Distance(latitude, longitude, targetLat, targetLon);

					if (unitBox.getSelectedItem().equals("miles")) {
						dist = Math.round((dist * 0.6214) * 100) / 100.0;
					}
					distOutField.setText(Double.toString(dist));
					locOutField.setText(CoordsToLoc(targetLat, targetLon));
					coordOutField.setEnabled(false);
					countryField.setEnabled(false);

					mapButton.setEnabled(true);
					qrzButton.setEnabled(false);
					qrzcqButton.setEnabled(false);

				}

				if (logging.equals("yes")) {

					if (InputBox.getSelectedItem().equals("Locator")) {
						locatorLog = inputField.getText().toUpperCase();
					} else {
						locatorLog = locOutField.getText();
					}
					;

					if (InputBox.getSelectedItem().equals("Coordinates")) {
						coordsLog = inputField.getText();
					} else {
						coordsLog = coordOutField.getText();
					}
					;

					String outputLog = date + "," + time + "," + inputField.getText().toUpperCase() + ","
							+ InputBox.getSelectedItem() + "," + locatorLog + "," + countryField.getText() + ","
							+ coordsLog + "," + distOutField.getText().toUpperCase() + "," + unitBox.getSelectedItem();

					try {
						BufferedWriter bw = Files.newBufferedWriter(Paths.get(homePath + "/\\gqthres/\\gqthres.log"),
								StandardOpenOption.APPEND);
						bw.write(outputLog + "\n");
						bw.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
		    }
		};

		inputField.addActionListener(action);
		startButton.addActionListener(action);
{
			
		}

		mapButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent mapAction) {

				Process p;
				try {
					if (coordOutField.getText().isEmpty()) {
					p = Runtime.getRuntime().exec("C:\\/Program Files/\\/Mozilla Firefox/\\firefox.exe" + " https://www.luftlinie.org/" + latitude + "," + longitude
							+ "/" + inputField.getText());
					}
					
					else {
						p = Runtime.getRuntime().exec("C:\\/Program Files/\\/Mozilla Firefox/\\firefox.exe" + " https://www.luftlinie.org/" + latitude + "," + longitude
								+ "/" + coordOutField.getText());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		qrzButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent qrzAction) {

				Process p;
				try {
					p = Runtime.getRuntime().exec("C:\\/Program Files/\\/Mozilla Firefox/\\firefox.exe" + " https://www.qrz.com/db/" + inputField.getText());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		qrzcqButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent qrzcqAction) {

				Process p;
				try {
					p = Runtime.getRuntime().exec("C:\\/Program Files/\\/Mozilla Firefox/\\firefox.exe" + " https://www.qrzcq.com/call/" + inputField.getText());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new gqthresWin();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}