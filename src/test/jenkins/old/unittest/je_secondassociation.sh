#!/bin/bash

TEST_ARG1=""

BRANCH="default"
LOG_LEVEL=""

while getopts "O:j:t:b:l:" OPTION
do
	case $OPTION in
		O)
			TEST_ARG1=$OPTARG
			;;
		j)
			JDK_VERSION=$OPTARG
			;;
		t)
			TASK_NAME=$OPTARG
			;;
		b)
			BRANCH=$OPTARG
			;;
		l)
			LOG_LEVEL=$OPTARG
	esac
done

echo $TASK_NAME
echo $TEST_ARG1
echo $BRANCH

if [ -d /scratch/tests/${TASK_NAME} ] ; then
	cd ${TASK_NAME} && hg clone -b default ssh://adqian@sleepycat-scm.us.oracle.com://a/hgroot/je
else
	mkdir ${TASK_NAME}
	cd ${TASK_NAME} && hg clone -b default ssh://adqian@sleepycat-scm.us.oracle.com://a/hgroot/je
fi

if [ -f /scratch/tests/${TASK_NAME}/je/jenkins_changeset.txt ] ; then
        rm /scratch/tests/${TASK_NAME}/je/jenkins_changeset.txt
fi

cd je && hg log -l 1 -v >> ./jenkins_changeset.txt && cd ..

if [ X$JDK_VERSION == X"7" ] ; then
	export JAVA_HOME=/scratch/tests/app/Java_7
elif [ X$JDK_VERSION == X"5" ] ; then
	export JAVA_HOME=/scratch/tests/app/Java_5
else
	export JAVA_HOME=/scratch/tests/app/Java_6
fi

export JAVA_HOME=/scratch/tests/app/Java_8
export ANT_HOME=/scratch/tests/app/ant
export PATH=$ANT_HOME/bin:$JAVA_HOME/bin:$PATH

ROOT_DIR=/scratch/tests/${TASK_NAME}
JE_DIR=${ROOT_DIR}/je
TEST_DIR=${JE_DIR}/build/test/standalone
ENV_FILE=${JE_DIR}/environment.txt
ENV_FILE_NAME=environment.txt

JAVA_VERN=`java -version 2>&1 | head -1 | awk -F "\"" '{print $2}' -`
ANT_VERN=`ant -version`
BUILD_VER=`cd $ROOT_DIR/je && hg tip`
DISK_FREE=`df -h | awk -v disk_name=scratch '$0 ~ disk_name {print "Total: "$1"   Used: "$2"   Avail:" $3}' - | tail -1`
MEM_FREE=`free -m`

echo " "
echo "========================================================="
echo "                                                        "
java -version
ant -version
echo "JAVA_HOME=$JAVA_HOME                                   "
echo "ANT_HOME=$ANT_HOME                                     "
echo "Code branch: $BRANCH $BUILD_VER                        "
echo "                                                        "
echo "========================================================="
echo " "

gen_report() {
	echo "===================== Environment ======================" > ${ENV_FILE}
	echo " " >> ${ENV_FILE}
	echo " Java version: $JAVA_VERN" >> ${ENV_FILE}
	echo " Ant version: $ANT_VER" >> ${ENV_FILE}
	echo "" >> ${ENV_FILE}
	echo " OS: $OS_VER" >> ${ENV_FILE}
	echo " Disk: $DISK_FREE" >> ${ENV_FILE}
	echo " $MEM_FREE" >> ${ENV_FILE}
	echo "" >> ${ENV_FILE}
	echo "========================================================" >> ${ENV_FILE}
}

gen_report


if [ X$LOG_LEVEL == X"INFO" ] ; then
	echo "com.sleepycat.je.util.ConsoleHandler.level=INFO" > ${ROOT_DIR}/je/logging.properties
fi

cd ${JE_DIR} && ant -Dlongtest=true -Dtestcase=com.sleepycat.je.test.SecondaryAssociationTest -Dtimeout=6000000 test


# log files
#cd ${JE_DIR} && tar czf ${TASK_NAME}.tar.gz ./build.xml ./build ./jenkins_changeset.txt
#cd ${JE_DIR} && scp ${TASK_NAME}.tar.gz jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace/

BUILDID=`ssh -l jenkins slc04ark "cat /scratch/jenkins/jobs/je_standalone_secondassociation/nextBuildNumber"`
BUILDID=`expr $BUILDID - 1`

LOGLOCATION=~/log_archive/je_standalone_secondassociation/$BUILDID
mkdir -p $LOGLOCATION
echo "Host: slc04arq.us.oracle.com" >> ${JE_DIR}/location_of_environment_and_log.txt

cd $LOGLOCATION

echo "Directory: `pwd`" >> ${JE_DIR}/location_of_environment_and_log.txt
echo "Username: tests" >> ${JE_DIR}/location_of_environment_and_log.txt
echo "Password: 123456" >> ${JE_DIR}/location_of_environment_and_log.txt

cp -r ${JE_DIR} $LOGLOCATION


cd ${JE_DIR} && scp jenkins_changeset.txt location_of_environment_and_log.txt jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace
cd ${JE_DIR} && scp -r build/test/data/ jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace
