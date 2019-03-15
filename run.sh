#!/bin/bash
# set -x
# bo not want to change user profile in osx insance
which xmllint > /dev/null
# the bash variables are currently unused -  may be needed in the future
if [  $? -eq  0 ] ; then
  APP_VERSION=$(xmllint -xpath "/*[local-name() = 'project' ]/*[local-name() = 'version' ]/text()" pom.xml)
fi
if $(uname -s | grep -qi 'Darwin')
then
  # https://www.java.com/en/download/help/version_manual.xml
  JAVA_VERSION=$('/Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home/bin/java' -version 2>& 1| sed -n 's|^.*"\(.*\)\".*$|\1|p')
  if [ -z $JAVA_VERSION} ]; then
    JAVA_VERSION='1.8.0_121'
  fi
  export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk$JAVA_VERSION.jdk/Contents/Home

  # NOTE: No uniform way to query OSX for the installed maven version
  # If Maven is installed to 'Applications' need to adjust the command line to: "mdfind -onlyin '/Applications' -name mvn"
  M2=$(mdfind -onlyin "${HOME}/Downloads" -name mvn | sed -n 's|^\(.*\)/mvn$|\1|p'|head -1)

  if [ -z $MAVEN_VERSION ] ; then
    MAVEN_VERSION='3.3.9'
    export M2_HOME="$HOME/Downloads/apache-maven-$MAVEN_VERSION"
    export M2="$M2_HOME/bin"
  else
    export M2
    export M2_HOME=$(echo $M2| sed 's|/bin||')
  fi
  export MAVEN_OPTS='-Xms256m -Xmx512m'
  export PATH=$M2_HOME/bin:$PATH
  # following OSX-specific launch option is only critical for SWT, 
  # is ignored for Swing or javaFX
  # http://stackoverflow.com/questions/3976342/running-swt-based-cross-platform-jar-properly-on-a-mac
  # https://stackoverflow.com/questions/7527789/specifying-maven-memory-parameter-without-setting-maven-opts-environment-variabl/30441186#30441186
  LAUNCH_OPTS='-XstartOnFirstThread'
  export MAVEN_OPTS="-Xms256m -Xmx512m ${LAUNCH_OPTS}"
fi

mvn clean test -DargLine=$LAUNCH_OPTS
