[![Stories in Ready](https://badge.waffle.io/FunThomas424242/gui4j.svg?label=ready&title=Ready)](http://waffle.io/FunThomas424242/gui4j)
[![Build Status](https://travis-ci.org/FunThomas424242/gui4j.svg?branch=master)](https://travis-ci.org/FunThomas424242/gui4j)

(This is a fork to deliver the gui4j project of sourceforge.net into a maven repo at bintray)

Gui4j - Describe Java/Swing GUIs in XML
=======================================
www.gui4j.org

Usage
-----

See files/README.txt for notes regarding the usage of gui4j.


Building gui4j and generating its documentation and view.dtd
------------------------------------------------------------

This project started out using Ant as its build tool and later switched to Maven.
The build.xml found in the deploy directory is still called by maven for the task of
generating the gui4j documentation.

Prerequisite for doing a build:
- Install Maven 1.x (our project structure is not compatible to Maven 2.x)

Additional prerequisite for generating the documentation:
- Checkout the project "Gui4jDocumentation" under that name in a directory
  parallel to "Gui4j".

Build Goals:
- "maven site": to generate all documentation
- "maven dist": to build a complete distribution.

Build results are found under the target directory.
Main documentation entry: target/docs/index.html
Reference doc entry: target/docs/gui4jdocs/index.html

You can find the generated view.dtd under target/docs/gui4jdocs/view.dtd

