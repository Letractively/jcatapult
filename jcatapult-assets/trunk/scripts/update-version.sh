#!/bin/bash

if [ $# != 1 ]; then
  echo "Usage: update-versions.sh <version>"
  exit 1
fi

version=$1
dir=$(dirname $0)
projects=$(cat $dir/projects)

# Run some checks
for p in $projects; do
  if [ ! -d $p ]; then
    echo "You need to check out the $p project."
    exit 1
  fi

done

# Fix the project.xml files
for p in $projects; do
  cd $p
  sed "s/<property name=\"version.jcatapult\" value=\".*\"\/>/<property name=\"version.jcatapult\" value=\"${version}-{integration}\"\/>/g" project.xml | \
    sed "s/version=\".*\">/version=\"${version}\">/g" > tmp-project.xml
  rm project.xml
  mv tmp-project.xml project.xml
  cd -
done
