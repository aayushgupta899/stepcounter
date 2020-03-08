#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Jan 31 17:28:41 2020

@author: aayushgupta
"""

import numpy as np
import matplotlib.pyplot as plt
from scipy.signal import find_peaks
arr = np.genfromtxt('/Users/aayushgupta/Documents/csv_files_a1/8_output.csv',delimiter=',')
threshold = arr[0,0]
numShakes = arr[0,1]
arr = arr[1:]
thresholdArr = np.zeros_like(arr)
thresholdArr[:,1] = arr[:,1]
thresholdArr[:,0] = [threshold]
x = arr[:,0]
t = arr[:,1]
peaks, _ = find_peaks(x, height=0)
plt.plot(t, x,'b-')
plt.plot(thresholdArr[:,1], thresholdArr[:,0], 'r--', label='Threshold = '+(str)(threshold))
peaksCount = 0
shakes = []
tShakes = []
for i in peaks:
    if x[i] > threshold:
        peaksCount += 1
        shakes.append(x[i])
        tShakes.append(t[i])
plt.plot([], [], ' ', label='Number of Shakes:'+(str)(peaksCount))
plt.plot(tShakes, shakes, "x")
plt.legend()
plt.xlabel('Time(s)')
plt.ylabel('Shake Detector Input')
plt.savefig((str)(threshold)+'_output.png')
