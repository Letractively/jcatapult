#!/bin/bash
dir=$(dirname $0)

# Run some checks
for p in $(cat $dir/projects); do
  if [ ! -d $p ]; then
    echo "You need to check out the $p project."
    exit 1
  fi

  cd $p
  if ! ant clean test; then
    echo "Build failed for $p"
    exit 1
  fi
  cd ..
done
