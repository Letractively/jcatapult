#!/bin/bash

if [ $# != 1 ]; then
  echo "Usage: release.sh <version>"
  exit 1
fi

version=$1
dir=$(dirname $0)
cd $dir
dir=$PWD
cd -
echo "Working from release script dir $dir"
projects=$(cat $dir/projects)

#
# This runs all the necessary checks against the projects
#
function test_project() {
  if [ ! -d $1 ]; then
    echo "You need to check out the $1 project."
    exit 1
  fi

  cd $1

  changes=$(svn stat | wc -l)
  if (( $changes > 0 )); then
    echo "$1 has pending changes"
    exit 1
  fi

  if ! ant clean test >> /tmp/jcatapult-release.log; then
    echo "Project tests failed. Unable to release"
    exit 1
  fi

  cd ..
}

#
# This updates the version in the project
#
function update_version() {
  cd $1

  sed "s/<property name=\"version.jcatapult\" value=\".*\"\/>/<property name=\"version.jcatapult\" value=\"${version}\"\/>/g" project.xml | \
    sed "s/version=\".*\">/version=\"${version}\">/g" > tmp-project.xml
  rm project.xml
  mv tmp-project.xml project.xml

  if ! svn ci -m "Next version"; then
    echo "Unable to commit modified project.xml"
    exit 1
  fi

  cd ..
}

#
# This releases the project
#
function release_project() {
  cd $1

  if ! ant rel < $dir/release-input >> /tmp/jcatapult-release.log; then
    echo "Release of $1 failed"
    exit 1
  fi

  if [ $1 = "jcatapult-dbmgr" ]; then
    echo "Here"

    # We need to grab the release of the jcatapult-dbmgr and copy it to jcat-ant
    if ! svn rm ../jcatapult-ant/plugins/mysql/1.0/lib/jcatapult*; then
      echo "Unable to remove previous jcatapult-dbmgr from jcatapult-ant MySQL plugin"
      exit 1
    fi

    if ! cp target/jars/jcatapult-dbmgr-${version}.jar ../jcatapult-ant/plugins/mysql/1.0/lib/; then
      echo "Unable to update jcatapult-ant MySQL plugin with the new jcatpault-dbmgr"
      exit 1
    fi

    if ! svn add ../jcatapult-ant/plugins/mysql/1.0/lib/jcatapult*; then
      echo "Unable to add new jcatapult-dbmgr to the jcatapult-ant MySQL plugin"
      exit 1
    fi

    if ! svn ci -m "Next version" ../jcatapult-ant; then
      echo "Unable to commit new jcatapult-dbmgr to the jcatapult-ant MySQL plugin"
      exit 1
    fi
  fi

  cd ..
}

# Run some checks
for p in $projects; do
  echo "Testing $p"
  test_project $p
done

# Fix the project.xml files
for p in $projects; do
  echo "Updating $p"
  update_version $p
done

# First release the DBMgr and then copy that release to jcatapult ant
for p in $projects; do
  echo "Releasing $p"
  release_project $p
done

# Push the ZIP file to the site
cd jcatapult-core
ant release-jcatapult
