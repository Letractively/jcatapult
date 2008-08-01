#!/bin/bash

projects="jcatapult-core jcatapult-ant jcatapult-commerce jcatapult-dbmgr jcatapult-deployer jcatapult-email jcatapult-mvc jcatapult-persistence jcatapult-scaffolder jcatapult-security jcatapult-struts jcatapult-filemgr"

# Run some checks
for p in $projects; do
  if [ ! -d $p ]; then
    echo "You need to check out the $p project."
    exit 1
  fi

  cd $p
  if ! ant clean jar; then
    echo "Build failed for $p"
    exit 1
  fi
  cd ..
done
