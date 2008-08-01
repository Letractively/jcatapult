#!/bin/bash

if [ $# != 1 ]; then
  echo "Usage: release.sh <version>"
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

  cd $p

  changes=$(svn stat | wc -l)
  if (( $changes > 0 )); then
    echo "$p has pending changes"
    exit 1
  fi

  if ! ant clean test; then
    echo "Project tests failed. Unable to release"
    exit 1
  fi

  cd ..
done

# Fix the project.xml files
for p in $projects; do
  if [ -f $p/project.xml ]; then
    cd $p
    sed "s/<property name=\"version.jcatapult\" value=\".*\"\/>/<property name=\"version.jcatapult\" value=\"${version}\"\/>/g" project.xml | \
      sed "s/version=\".*\">/version=\"${version}\">/g" > tmp-project.xml
    rm project.xml
    mv tmp-project.xml project.xml

    if ! svn ci -m "Next version"; then
      echo "Unable to commit modified project.xml"
      exit 1
    fi

    if ! ant rel < ../release-input; then
      echo "Release of $p failed"
      exit 1
    fi

    cd ..
  fi
done

# Push the ZIP file to the site
cd jcatapult-core
ant release-jcatapult
