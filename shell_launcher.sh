#!/bin/bash
rm beans -rf
mkdir beans
javac -cp jars/fifth.jar:jars/simulator.jar -s src/ -d beans/ src/supportGUI/*\.java src/algorithms/*\.java src/characteristics/*\.java
java -cp jars/*:beans/ supportGUI.Viewer
