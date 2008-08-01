#!/bin/bash

if [ $# != 1 ]; then
  echo "Usage: update-versions.sh <version>"
  exit 1
fi

version=$1
projects="jcatapult-core jcatapult-ant jcatapult-commerce jcatapult-dbmgr jcatapult-deployer jcatapult-email jcatapult-mvc jcatapult-persistence jcatapult-scaffolder jcatapult-security jcatapult-struts jcatapult-filemgr"

# Run some checks
for p in $projects; do
  if [ ! -d $p ]; then
    echo "You need to check out the $p project."
    exit 1
  fi

done

# Fix the project.xml files
for p in $projects; do
  if [ -f $p/project.xml ]; then
    cd $p
    sed "s/version=\".*\">/version=\"${version}\">/g" project.xml > tmp-project.xml
    rm project.xml
    mv tmp-project.xml project.xml
    cd ..
  fi
done
