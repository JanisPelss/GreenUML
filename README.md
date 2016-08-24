
#Green UML release 4.6.0
- [Requirements](#requirements)
- [Installation](#installation)
 - [Through Eclipse (recommended)](#through-eclipse-recommended)
 - [Through file system drop-in](#through-file-system-drop-in)
- [Users guide and additional information](#users-guide-and-additional-information)
- [Contribute](#contribute)
 - [Requirements to develop Green UML](#requirements-to-develop-green-uml)
 - [What can be improved](#what-can-be-improved)
- [Bugs](#bugs)
- [License information](#license-information)
- [Origins, historical versions](#origins-historical-versions)

====================================================================================================================

Green is a LIVE round-tripping editor, meaning that it supports both software engineering and reverse engineering. 
You can use green to create a UML class diagram from code, or to generate code by drawing a class diagram.

The editor features a simple point-and-click interface. Right-clicking in the editor brings up a 
context-sensitive menu that includes features such as the refactoring and quick-fix functionality.
Green supports exporting files to JPG, PNG and GIF formats, as well as saving them as Green UML projects, so that you can share you diagrams without any issues.

## What's been changed in this version
- This Green UML version works properly on Eclipse 4.6 Neon
- Fixed all discovered bugs
- Added logger and an option to write output to file

## Requirements
Eclipse 4.6 Neon

## Installation
### Through Eclipse (recommended)
- Open Eclipse, and navigate to Help > Install New Software...
- Click on Add... 
- Fill the fields as following: <br>
     Name: GreenUML <br>
     Location: http://odo.lv/ftp/tools/GreenUpdateSite
- Click OK 
- Choose what features to install and follow further instructions

### Through file system drop-in
- After downloading the Green zip file, extract the contents to [Eclipse installation directory]\dropins\
- If warned that any existing files with the same name will be overwritten, select "Yes to All"


## Users guide and additional information
- [Using the Green UML Plugin for Eclipse](https://www.clear.rice.edu/comp310/Eclipse/GreenUML/)
- In Eclipse, click Help > Help Contents > Green Users Guide


## Contribute
If you want to contribute to this project, please create a fork of this and commit all of your changes there. 

### Requirements to develop Green UML:
- [Eclipse Modelling Tools (package Neon R)](http://www.eclipse.org/downloads/packages/release/Neon/R)
- [GMF Tooling](http://download.eclipse.org/modeling/gmp/gmf-tooling/updates/releases/)

### What can be improved:
- Green UML is working only on Eclipse 4.6 Neon now, further development could target other Eclipse versions
- Additional functionality

## Bugs
Please report bugs to [GreenUML/issues](https://github.com/ValdisVitolins/GreenUML/issues) on GitHub. 
Please note that we do not guarantee bug fixes or updates in the future.

## License information
- This software is distributed under [Eclipse Public License - v 1.0](https://www.eclipse.org/legal/epl-v10.html). 
Please see it for terms of use and redistribution. 
- Alternatively, a TL;DR version is available [here](https://www.tldrlegal.com/l/epl).

## Origins, historical versions
- Green UML was originally developed by Dr. Carl Alphonce, Colin Fike, Remo Fischione, Nicholas Wheeler and others from Computer Science and Engineering Department at University at Buffalo.
- The original versions of Green UML can be obtained [here](http://green.sourceforge.net/builds.html), however, the last updates there date back to 2009 and Eclipse 3.5
- Original web page: [green.sourceforge.net](http://green.sourceforge.net)
- The project is forked from [this GitHub repository](https://github.com/fmjrey/Green-UML)


