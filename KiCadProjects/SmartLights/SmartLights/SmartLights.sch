EESchema Schematic File Version 4
EELAYER 30 0
EELAYER END
$Descr A4 11693 8268
encoding utf-8
Sheet 1 1
Title ""
Date ""
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
$Comp
L SmartApartment:HUZZAH u1
U 1 1 6187E5CA
P 4450 3650
F 0 "u1" H 4450 4050 50  0001 C CNN
F 1 "HUZZAH" H 4450 4273 50  0000 C CNN
F 2 "SharedLib:HUZZAH_FOOT" H 4350 3550 50  0001 C CNN
F 3 "" H 4350 3550 50  0001 C CNN
	1    4450 3650
	1    0    0    -1  
$EndComp
$Comp
L Device:R R4
U 1 1 6187FA3F
P 3600 3000
F 0 "R4" V 3500 2900 50  0000 C CNN
F 1 "R" V 3500 3050 50  0000 C CNN
F 2 "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P10.16mm_Horizontal" V 3530 3000 50  0001 C CNN
F 3 "~" H 3600 3000 50  0001 C CNN
	1    3600 3000
	-1   0    0    1   
$EndComp
$Comp
L Device:R R1
U 1 1 618814FD
P 3450 3450
F 0 "R1" V 3350 3350 50  0000 C CNN
F 1 "R" V 3350 3500 50  0000 C CNN
F 2 "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P10.16mm_Horizontal" V 3380 3450 50  0001 C CNN
F 3 "~" H 3450 3450 50  0001 C CNN
	1    3450 3450
	0    1    1    0   
$EndComp
$Comp
L Device:R R2
U 1 1 61882168
P 3450 3750
F 0 "R2" V 3550 3700 50  0000 C CNN
F 1 "R" V 3550 3800 50  0000 C CNN
F 2 "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P10.16mm_Horizontal" V 3380 3750 50  0001 C CNN
F 3 "~" H 3450 3750 50  0001 C CNN
	1    3450 3750
	0    1    1    0   
$EndComp
$Comp
L Device:R R3
U 1 1 61882370
P 3450 4050
F 0 "R3" V 3550 4000 50  0000 C CNN
F 1 "R" V 3550 4150 50  0000 C CNN
F 2 "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P10.16mm_Horizontal" V 3380 4050 50  0001 C CNN
F 3 "~" H 3450 4050 50  0001 C CNN
	1    3450 4050
	0    1    1    0   
$EndComp
$Comp
L Device:LED D1
U 1 1 61882662
P 2950 3750
F 0 "D1" H 2800 3850 50  0000 C CNN
F 1 "LED" H 2943 3876 50  0000 C CNN
F 2 "LED_THT:LED_D2.0mm_W4.8mm_H2.5mm_FlatTop" H 2950 3750 50  0001 C CNN
F 3 "~" H 2950 3750 50  0001 C CNN
	1    2950 3750
	1    0    0    -1  
$EndComp
$Comp
L Device:LED D2
U 1 1 61883135
P 2950 4050
F 0 "D2" H 2800 3900 50  0000 C CNN
F 1 "LED" H 3000 3950 50  0000 C CNN
F 2 "LED_THT:LED_D2.0mm_W4.8mm_H2.5mm_FlatTop" H 2950 4050 50  0001 C CNN
F 3 "~" H 2950 4050 50  0001 C CNN
	1    2950 4050
	1    0    0    -1  
$EndComp
$Comp
L Device:R R5
U 1 1 6188334C
P 5700 3700
F 0 "R5" V 5493 3700 50  0000 C CNN
F 1 "R" V 5584 3700 50  0000 C CNN
F 2 "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P10.16mm_Horizontal" V 5630 3700 50  0001 C CNN
F 3 "~" H 5700 3700 50  0001 C CNN
	1    5700 3700
	0    1    1    0   
$EndComp
$Comp
L Switch:SW_Push SW1
U 1 1 61883C26
P 5400 3900
F 0 "SW1" V 5400 4048 50  0000 L CNN
F 1 "SW_Push" H 5400 4094 50  0001 C CNN
F 2 "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P10.16mm_Horizontal" H 5400 4100 50  0001 C CNN
F 3 "~" H 5400 4100 50  0001 C CNN
	1    5400 3900
	0    1    1    0   
$EndComp
$Comp
L SmartApartment:Transistor Q2
U 1 1 61885169
P 3200 2750
F 0 "Q2" H 3200 2500 50  0001 C CNN
F 1 "Transistor" H 3050 3000 50  0000 L CNN
F 2 "SharedLib:Transistor_FOOT" H 3150 3000 50  0001 C CNN
F 3 "" H 3150 3000 50  0001 C CNN
	1    3200 2750
	-1   0    0    1   
$EndComp
$Comp
L SmartApartment:Transistor Q1
U 1 1 61886243
P 2900 3250
F 0 "Q1" H 2900 3000 50  0001 C CNN
F 1 "Transistor" H 2750 3500 50  0000 L CNN
F 2 "SharedLib:Transistor_FOOT" H 2850 3500 50  0001 C CNN
F 3 "" H 2850 3500 50  0001 C CNN
	1    2900 3250
	-1   0    0    1   
$EndComp
$Comp
L SmartApartment:Pad X2
U 1 1 61887061
P 2450 2850
F 0 "X2" H 2450 2750 50  0001 C CNN
F 1 "(- WLED)" H 2550 2750 50  0000 C CNN
F 2 "SharedLib:Pad_FOOT" H 2450 2850 50  0001 C CNN
F 3 "" H 2450 2850 50  0001 C CNN
	1    2450 2850
	-1   0    0    1   
$EndComp
Wire Wire Line
	4050 3850 3850 3850
Wire Wire Line
	4050 3550 3950 3550
Wire Wire Line
	3950 3550 3950 3150
Wire Wire Line
	3950 3150 3600 3150
Wire Wire Line
	4050 3650 3850 3650
Wire Wire Line
	3850 3650 3850 3450
Wire Wire Line
	3850 3450 3600 3450
Wire Wire Line
	4050 3750 3600 3750
Wire Wire Line
	3850 3850 3850 4050
Wire Wire Line
	3850 4050 3600 4050
Wire Wire Line
	3300 3750 3100 3750
Wire Wire Line
	3300 4050 3100 4050
Wire Wire Line
	3300 3450 3300 3250
Wire Wire Line
	3300 3250 3050 3250
Wire Wire Line
	3600 2850 3600 2750
Wire Wire Line
	3600 2750 3350 2750
Wire Wire Line
	3000 2650 2700 2650
Wire Wire Line
	2700 2650 2700 3150
Wire Wire Line
	2700 3150 2550 3150
Wire Wire Line
	2550 3150 2550 3750
Wire Wire Line
	2550 3750 2800 3750
Connection ~ 2700 3150
Wire Wire Line
	2800 4050 2550 4050
Wire Wire Line
	2550 4050 2550 3750
Connection ~ 2550 3750
Wire Wire Line
	4050 4150 3850 4150
Wire Wire Line
	3850 4150 3850 4300
Wire Wire Line
	3850 4300 2550 4300
Wire Wire Line
	2550 4300 2550 4050
Connection ~ 2550 4050
$Comp
L SmartApartment:Pad X1
U 1 1 618905A3
P 2250 3350
F 0 "X1" H 2250 3250 50  0001 C CNN
F 1 "(- OLED)" H 2350 3450 50  0000 C CNN
F 2 "SharedLib:Pad_FOOT" H 2250 3350 50  0001 C CNN
F 3 "" H 2250 3350 50  0001 C CNN
	1    2250 3350
	-1   0    0    1   
$EndComp
Wire Wire Line
	2700 3350 2400 3350
Wire Wire Line
	3000 2850 2600 2850
$Comp
L SmartApartment:Pad X3
U 1 1 618914C0
P 2700 2400
F 0 "X3" H 2700 2300 50  0001 C CNN
F 1 "(- B/O)" V 2800 2550 50  0000 R CNN
F 2 "SharedLib:Pad_FOOT" H 2700 2400 50  0001 C CNN
F 3 "" H 2700 2400 50  0001 C CNN
	1    2700 2400
	0    -1   -1   0   
$EndComp
Wire Wire Line
	2700 2650 2700 2550
Connection ~ 2700 2650
Wire Wire Line
	4850 3950 5200 3950
Wire Wire Line
	4850 3850 5200 3850
Wire Wire Line
	5200 3850 5200 3650
Wire Wire Line
	5200 3650 5400 3650
Wire Wire Line
	5200 4100 5400 4100
Wire Wire Line
	5200 3950 5200 4100
Wire Wire Line
	5400 3650 5400 3700
Wire Wire Line
	5400 3700 5550 3700
Connection ~ 5400 3700
Wire Wire Line
	5850 3700 5850 4150
Wire Wire Line
	5850 4150 4850 4150
$EndSCHEMATC
