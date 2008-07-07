#/bin/bash

# We need the DB url for JDBC, the directory of the files for this project
# and the classpath for all the component SQL files. The classpath can be
# built from the web directory for now
if [ $# < 4 || $# > 5 ]; then
  echo "Usage: loader.sh [--no-domain] <url> <project-name> <sql-dir> <web-dir>"
  exit 1
fi

project_contains_domain="true"
if [ "$1" == "--no-domain" ]; then
  project_contains_domain="false"
  shift 1
fi

url=$1
project=$2
sql_dir=$3
web_dir=$4

# Build the classpath
classpath=""
for f in $(ls $web_dir/WEB-INF/lib); do
  classpath="$web_dir/WEB-INF/lib/$f:$classpath"
done

echo "Classpath: $classpath"
java -cp $classpath org.jcatapult.migration.DatabaseMigrator punit $url $project $project_contains_domain $sql_dir
