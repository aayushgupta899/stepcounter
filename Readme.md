#SHAKE DETECTOR - AAYUSH GUPTA
#OVERVIEW
This project is an android application which detects whether the user has shaken their device between a time period. The time period is decided on the press of Start/Stop buttons, and the threshold level of the shake is specified by the user in a EditText box. After the user has provided the necessary inputs and pressed the Start button, the app will start displaying the love accelerometer data. The app will also start recording the value of the magnitude of the resulting acceleration every 0.5 seconds - the sampling frequency which I have chosen. After the user presses the Stop button, the app displays the number of shakes if there are shakes above the threshold value, otherwise “No Shake”. Additionally, the application has a separate area which displays the barometer data on press of the “Show Barometer Data” button.
#WORKING
1. UI: The UI has 8 elements for the shake detector part and 2 elements for the barometer part.   
1.1. The following are the components of the shake detector:  
1.1.1. textView1: Has the hardcoded message “The 3D acceleration values are”.  
1.1.2. textView2: Displays the live accelerometer data on press of the “Start” button.  
1.1.3. textView3: Has the hardcoded message “The Shake Detector detects”.  
1.1.4. textView4: Displays the final result - Number of shakes/”No Shake”.  
1.1.5. textView6: Displays the number of shakes.   
1.1.6. editText1: The user enters the value of threshold here.  
1.1.7. button1: The “Start” button.  
1.1.8. button2: The “Stop” button.    
1.2. The following are the components of the barometer functionality:  
1.2.1. textView5: Displays the live barometer data.  
1.2.2. button3: The “Show Barometer data” button.  
The UI elements are placed according to a relative constraint scheme.  
2. BACKEND: The backend has the following functions:  
2.1. onCreate(): This function is called when the main activity starts - which is our application starting point. I am using this to call some utility functions as well.  
2.2. createSensor(): This is a utility function which is used to create the SensorManager object as well as the accelerometer and the barometer objects. This is called from the onCreate() function.  
2.3. bindView(): This is a utility function which binds the UI elements to the variables on the backend. This is called from the onCreate() function.  
2.4. onStartClick(): This function is called on the press of the Start button. This function first checks if the user has provided a valid value of threshold; if not a toast notification is displayed. Otherwise, the function registers a listener on the accelerometer.  
2.5. onStopClick(): This function is called on pressing the Stop button. This unregisters the listener on the accelerometer, writes the recorded data to file, and resets all the variables and the editView. It also binds the final result to the textView on the UI.  
2.6. showBarometerData(): This function is called when the “Show Barometer Data” button is pressed. It registers/unregisters the listener for the barometer sensor.  
2.7. onPause(): This unregisters the current listener to save battery in case the app is paused.  
2.8. onResume(): This registers the accelerometer sensor again after the app is resumed.   
2.9. onSensorChanged(): This function continuously produces values from the accelerometer or barometer. I conditionally check whether the sensor calling this function is an accelerometer or barometer. For accelerometer, I set the textView2 with the live values of the data using the SensorEvent object. I also sample the data every 0.5 seconds and store it in a list. Additionally, if any value is greater than the threshold, I set a flag to true. This is where the step counter algorithm is implemented. The step counter algorithm:
<pre>
set isPeak = true, isTrough = true, numShakes = 0, lastShakeVal = null, lastDifference = 0
For every call to onSenorChanged():
         If lastShakeVal == null
                        lastShakeVal = currentShakeVal
         else if currentShakeVal < lastShakeVal and  isPeak = true
                        If lastShakeVal >= threshold and lastDifference > 1
                            	numShakes <- numShakes + 1
                        isTrough = true
                        isPeak = false
         else if currentShakeVal < lastShakeVal and isTrough = true
                        isTrough = true
         else if currentShakeVal >= lastShakeVal
                        isPeak = true
         else if val >= lastShakeVal and isTrough = true
                        isPeak = true
                        isTrough = false
         lastDifference = currentShakeVal - lastShakeVal
         lastShakeVal = currentShakeVal
</pre>
For the barometer, I am setting the value textView5 with the live data.
#DIRECTORY STRUCTURE
<pre>
AayushGuptaA1
|_app - Contains the application code
|_csv_files - Contains the csv files having the outputs
|_python - Contains the python code to plot the data - plot_data.py
|_images - Contains the final plotted graphs
|_screenshots - Contains some screenshots of the application
</pre>
#FINAL THOUGHTS
This was a very interesting project and I learned a lot. I completed the working of the shake detector as well as two bonus components: the barometer functionality and the shake counter.
Some of the difficulties which I faced were:  
a. While developing the barometer functionality, I was not getting any output on my device. After a lot of research, I realized that my device doesn’t actually have a pressure sensor. The functionality started working when I ran it on the emulator.  
b. The shake counter algorithm was a little tough to implement, as we had to count the peaks and troughs - but I managed to do it in Java without using external libraries.




