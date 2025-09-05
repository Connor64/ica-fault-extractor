# ICA Fault Extractor Utility

This tool was developed for Industrial Controls and Automation to extract fault details from PLCs.
The tool reads the XML data from the PLC and exports a CSV file containing all the faults and the
attributes you specified.

## How to use the Fault Extractor Utility:
1. Launch the program
   * You will need Java installed on your machine
   * If double-clicking the application doesn't work, try running `java -jar ica-fault-extractor.jar` in your terminal where the file is located
2. Open an XML file containing the faults from your machine 
   * The file should be wrapped with the `<AlarmCollection>` tag 
   * Inside this tag should be several `<Alarm>` tags that contain the fault data
3. Upon loading the file, the program will extract all available attributes from the faults
4. Select the attribute information you want to be included in the output file 
5. Press "Export" to save to a CSV file in a specified location


## How to make changes to the code?
If you need to make changes to this program, you can clone this repository and open it in a Java
development environment. You may need to install a JDK on your system if you do not have one already (this project uses
JDK 21.0.6).

The bulk of the program is included in `EditorWindow.java` and is thoroughly documented.

To create a standalone program, you will need to build the application as an executable .jar file. This process will depend on the IDE you are using.
