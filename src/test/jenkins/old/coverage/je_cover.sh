#!/bin/bash

TEST_ARG1=""

BRANCH="default"
LOG_LEVEL=""

while getopts "O:j:t:b:l:h:" OPTION
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
			;;
		h)
			HGPATH=$OPTARG
	esac
done

echo $TASK_NAME
echo $TEST_ARG1
echo $BRANCH

if [ -d /scratch/tests/${TASK_NAME} ] ; then
	#cd ${TASK_NAME} && hg clone -r ${BRANCH} ssh://adqian@sleepycat-scm.us.oracle.com://a/hgroot/je
	cd ${TASK_NAME} && ${HGPATH}/hg clone -b default ssh://adqian@sleepycat-scm.us.oracle.com://a/hgroot/je
else
	mkdir ${TASK_NAME}
	cd ${TASK_NAME} && ${HGPATH}/hg clone -b default ssh://adqian@sleepycat-scm.us.oracle.com://a/hgroot/je
fi

if [ -f /scratch/tests/${TASK_NAME}/je/jenkins_changeset.txt ] ; then
        rm /scratch/tests/${TASK_NAME}/je/jenkins_changeset.txt
fi

cd je && ${HGPATH}/hg log -l 1 -v >> ./jenkins_changeset.txt && cd ..

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
export CLASSPATH=/scratch/tests/app/ant/lib/junit-4.10.jar:$CLASSPATH

ROOT_DIR=/scratch/tests/${TASK_NAME}
TEST_DIR=${ROOT_DIR}/je/build/test

ANT_VERN=`ant -version`
BUILD_VER=`cd $ROOT_DIR/je && ${HGPATH}/hg tip`

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

if [ X$LOG_LEVEL == X"INFO" ] ; then
	echo "com.sleepycat.je.util.ConsoleHandler.level=INFO" > ${ROOT_DIR}/je/logging.properties
fi

# copy build.xml for coverage test.
#scp jenkins@slc04ark:~/bin/je_cover.xml ${ROOT_DIR}/je/build.xml
#scp jenkins@slc04ark:~/bin/preprocess.sh ${ROOT_DIR}/je/
scp jenkins@slc04ark:~/bin/je_cover.diff ${ROOT_DIR}/je/
cp /scratch/tests/new_clover/clover*.* /scratch/tests/app/ant/lib

cd /${ROOT_DIR}/je && ${HGPATH}/hg import --no-commit ./je_cover.diff

#cd ${ROOT_DIR}/je && ant -lib junit-4.10.jar clean $TEST_ARG1
#cd ${ROOT_DIR}/je && ant -lib junit-4.10.jar init-clover $TEST_ARG1
#cd ${ROOT_DIR}/je && ant -lib junit-4.10.jar clean-clover $TEST_ARG1
#cd ${ROOT_DIR}/je && ant -lib junit-4.10.jar clover.setup $TEST_ARG1
#cd ${ROOT_DIR}/je && ant -lib junit-4.10.jar clover.runtest  
cd ${ROOT_DIR}/je && ant -lib junit-4.10.jar clean clover.alltestsdone -Dclover.ignorefailure=true
#cd ${ROOT_DIR}/je && ant -lib junit-4.10.jar clean clover.singletestdone -Dtestcase=com.sleepycat.persist.test.EvolveProxyClassTest 
#cd ${ROOT_DIR}/je && ant -lib junit-4.10.jar clover.alltestsrun $TEST_ARG1
#cd ${ROOT_DIR}/je && ant -lib junit-4.10.jar clover.alltestsdone $TEST_ARG1

# log files
#cd ${ROOT_DIR}/je && tar czf ${TASK_NAME}.tar.gz ./build ./build.xml ./jenkins_changeset.txt ./cloverage.xml ./clover_html 
#cd ${ROOT_DIR}/je && scp ${TASK_NAME}.tar.gz jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace/

BUILDID=`ssh -l jenkins slc04ark "cat /scratch/jenkins/jobs/je_cover/nextBuildNumber"`
BUILDID=`expr $BUILDID - 1`

LOGLOCATION=~/log_archive/je_cover/$BUILDID
mkdir -p $LOGLOCATION
echo "Host: slc00brq.us.oracle.com" >> ${ROOT_DIR}/je/location_of_environment_and_log.txt

cd $LOGLOCATION

echo "Directory: `pwd`" >> ${ROOT_DIR}/je/location_of_environment_and_log.txt
echo "Username: tests" >> ${ROOT_DIR}/je/location_of_environment_and_log.txt
echo "Password: 123456" >> ${ROOT_DIR}/je/location_of_environment_and_log.txt

cp -r ${ROOT_DIR}/je $LOGLOCATION

cd ${ROOT_DIR}/je && scp jenkins_changeset.txt cloverage.xml jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace/ && scp location_of_environment_and_log.txt jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace/location_of_environment_and_log
cd ${ROOT_DIR}/je && scp -r clover_html build/test/data/ jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace/
