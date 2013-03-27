#!/bin/bash
#-----------------------------------------------------------------------------------------------
# Please consult the project's README.
#
# This script:
# (1) Sets up the endorsed library using wget's.
#     These may fail if the brisskit maven repo is changed in any way.
# (2) Invokes maven to set up the Eclipse classpath.
#     However, this may need review to ensure required resources are also on the classpath.
#
# Author: Jeff Lusted (jl99@leicester.ac.uk)
#-----------------------------------------------------------------------------------------------


#===========================================================================
# Setup the endorsed_lib directory
#=========================================================================== 
cd ./endorsed_lib
rm *
wget http://maven.brisskit.org/nexus/content/repositories/thirdparty/jaxb/jaxb-api/x.x/jaxb-api-x.x.jar
wget http://maven.brisskit.org/nexus/content/repositories/central/stax/stax-api/1.0.1/stax-api-1.0.1.jar
wget http://maven.brisskit.org/nexus/content/repositories/central/woodstox/wstx-asl/3.0.1/wstx-asl-3.0.1.jar
cd ..


#===========================================================================
# Set up the Eclipse classpath
#===========================================================================
mvn eclipse:eclipse

echo ""
echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
echo "!        Please refresh your Eclipse environment.        !"
echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"