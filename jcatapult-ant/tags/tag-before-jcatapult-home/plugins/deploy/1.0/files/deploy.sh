#!/bin/bash

VERSION=$1
MODE=$2
ARCHIVE=$3

PROJECT_NAME=@project.name@
FILES_DIR=@deploy.dir.files@
WORK_DIR=@deploy.dir.work@
DEPLOY_DIR=@deploy.dir.deploy@
STOP_CMD=@deploy.cmd.stop@
START_CMD=@deploy.cmd.start@
ENVIRONMENT=@deploy.environment@

DB_HOST=@deploy.db.host@
DB_PORT=@deploy.db.port@
DB_USERNAME=@deploy.db.username@
DB_PASSWORD=@deploy.db.password@
DB_DATABASE=@deploy.db.database@


NOW=`date +%Y-%m-%dT%H-%M-%S`

echo Running $PROJECT_NAME Deploy Script on $ENVIRONMENT
echo   Version: $VERSION
echo   Archive: $ARCHIVE
echo   Directories:
echo     Files: $FILES_DIR
echo     Work:  $WORK_DIR
echo     Deploy:$DEPLOY_DIR
echo   Commands:
echo     Stop:  $STOP_CMD
echo     Start: $START_CMD
echo   Database:
echo     Host:     $DB_HOST
echo     Username: $DB_USERNAME
echo     Password: $DB_PASSWORD
echo     Database: $DB_DATABASE
echo
echo

VERSION_DIR="$WORK_DIR/$VERSION"
BACKUP_DIR="$WORK_DIR/backup"
if [ ! -d $BACKUP_DIR ] ; then
	mkdir $BACKUP_DIR
fi

#
#BEGIN STEPS FOR TAR ONLY
#
if [ $MODE == 'tar' ] ; then
	if [ -d "$VERSION_DIR" ] ; then
		rm -rf $VERSION_DIR
	fi
	
	mkdir "$VERSION_DIR"
	
	#Untar the archive
	cd "$VERSION_DIR"
	if ! tar -zxf $WORK_DIR/$ARCHIVE ; then 
		echo "Cannot open archive.  Exiting."
		exit 1
	fi
fi
#
#END STEPS FOR TAR ONLY
#

cd "$VERSION_DIR"

if [ ! -d web ] ; then 
	echo "/web Directory missing.  Exiting."
	exit 1
fi


#Copy web files for quick transition to production
cp -r web web_stage


#Stop the server
echo Stopping Server...
$STOP_CMD
sleep 10

#Redeploy Web Files
echo Deploying Web Application Files
mv $DEPLOY_DIR $BACKUP_DIR/deploy_$NOW
mv web_stage $DEPLOY_DIR

#Load Database Scripts
echo Loading Database Scripts TODO

#Copy Files
echo Copying Seed Files TODO

#Start the Server
echo Starting the server...
$START_CMD

if [ -d $WORK_DIR/CURRENT ] ; then
	unlink $WORK_DIR/CURRENT
fi

ln -s $VERSION_DIR $WORK_DIR/CURRENT

echo DONE!