#!/bin/bash

TEST_ARG1=""

BRANCH="default"
LOG_LEVEL=""

path=$PATH
classpath=$CLASSPATH
run_class=com.sleepycat.util.sim.Dbsim
ant_home=/scratch/tests/app/ant

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
export CVSROOT=:ext:adqian@sleepycat-scm.us.oracle.com:/a/CVSROOT

if [ -d /scratch/tests/${TASK_NAME} ] ; then
	cd ${TASK_NAME}
	hg clone -b default ssh://adqian@sleepycat-scm.us.oracle.com://a/hgroot/je

        sleep 3
	hg clone -b default ssh://adqian@sleepycat-scm.us.oracle.com://a/hgroot/dbsim
	# sleep 3
	# cvs co dbsim
	# scp jenkins@slc04ark:~/bin/dbsim.tar.gz /scratch/tests/${TASK_NAME}/
	# tar xzf dbsim.tar.gz
else
	mkdir ${TASK_NAME}
	cd ${TASK_NAME}
	hg clone -b default ssh://adqian@sleepycat-scm.us.oracle.com://a/hgroot/je

        sleep 3
	hg clone -b default ssh://adqian@sleepycat-scm.us.oracle.com://a/hgroot/dbsim
	# sleep 3
	# cvs co dbsim
	# scp jenkins@slc04ark:~/bin/dbsim.tar.gz /scratch/tests/${TASK_NAME}/
	# tar xzf dbsim.tar.gz
fi


cd /scratch/tests/${TASK_NAME}/je && hg log -l 1 -v >> ./jenkins_changeset.txt

JDK8_HOME=/scratch/tests/app/Java_8
JDK7_HOME=/scratch/tests/app/Java_7
JDK6_HOME=/scratch/tests/app/Java_6
JDK5_HOME=/scratch/tests/app/Java_5

#export ANT_HOME=/scratch/tests/app/ant
#export PATH=$ANT_HOME/bin:$JAVA_HOME/bin:$PATH

ROOT_DIR=/scratch/tests/${TASK_NAME}
JE_DIR=${ROOT_DIR}/je
DBSIM_DIR=${ROOT_DIR}/dbsim
DBSIM_CLSDIR=${DBSIM_DIR}/build/classes
COMP_VER=B_je-3_3_x
COMP_DIR=${ROOT_DIR}/compare

TEST_DIR=${DBSIM_DIR}/build/test

JAVA_VERN=`java -version 2>&1 | head -1 | awk -F "\"" '{print $2}' -`
ANT_VERN=`ant -version 2>&1`
BUILD_VER=`cd $ROOT_DIR/je && hg tip`
DISK_FREE=`df -h | awk -v disk_name=scratch '$0 ~ disk_name {print "Total: "$1"   Used: "$2"   Avail: " $3}' - | tail -1`
OS_VER=`uname -a`
MEM_FREE=`free`

ENV_FILE_NAME=environment_${JAVA_VERN}.txt
ENV_FILE=${DBSIM_DIR}/${ENV_FILE_NAME}

gen_env() {
	touch ${ENV_FILE}
	echo "===================== Environment ======================" > ${ENV_FILE}
	echo " Code branch: $BRANCH $BUILD_VER" >> ${ENV_FILE}
	echo " " >> ${ENV_FILE}
	echo " Java version: $JAVA_VERN" >> ${ENV_FILE}
	echo " Ant version: $ANT_VER" >> ${ENV_FILE}
	echo "" >> ${ENV_FILE}
	echo " OS: $OS_VER" >> ${ENV_FILE}
	echo " Disk: $DISK_FREE" >> ${ENV_FILE}
	echo " $MEM_FREE" >> ${ENV_FILE}
	echo "" >> ${ENV_FILE}
	echo "========================================================" >> ${ENV_FILE}
	echo "" >> ${ENV_FILE}
}

get_jdk_version() {
	JAVA_VERN=`java -version 2>&1 | head -1 | awk -F "\"" '{print $2}' -`
}

run_dbsim() {
	cd ${DBSIM_DIR}

	export CLASSPATH=$classpath:build/classes:lib/antlr.jar:lib/je.jar

	# compile dbsim source codes
	ant clean compile

	# create the environment directory for running dbsim tests
	mkdir ${TEST_DIR}

	cd ${DBSIM_CLSDIR}
	export CLASSPATH=$classpath:.:${DBSIM_DIR}/lib/antlr.jar:${DBSIM_DIR}/lib/je.jar

	case ${CASE} in 
		"recovery"|"duplicate")
			java -cp $CLASSPATH -DsetErrorListener=true -ea ${run_class} -h ${TEST_DIR} -c ${DBSIM_DIR}/configs/${CASE}.conf -V -B >& ${DBSIM_DIR}/Case_${CASE}-JDK_${JAVA_VERN}.tmp
			;;
		"abortstress"|"dwStress"|"embedded_abort")
			java -cp $CLASSPATH -DsetErrorListener=true -ea ${run_class} -h ${TEST_DIR} -c ${DBSIM_DIR}/configs/${CASE}.conf -I
			java -cp $CLASSPATH -DsetErrorListener=true -ea ${run_class} -h ${TEST_DIR} -c ${DBSIM_DIR}/configs/${CASE}.conf >& ${DBSIM_DIR}/Case_${CASE}-JDK_${JAVA_VERN}.tmp
			;;
	esac

	if [ X"$CASE" = X"fileformat" ] ; then
		# checkout the 
		mkdir ${COMP_DIR}
		#cd ${COMP_DIR} && hg clone -r B_je-3_3_x ssh://adqian@sleepycat-scm.us.oracle.com://a/hgroot/je
		cd ${COMP_DIR} && hg clone -b default ssh://adqian@sleepycat-scm.us.oracle.com://a/hgroot/je
		cd ${COMP_DIR}/je && ant jar

		cd ${DBSIM_DIR}
		mv lib/je.jar lib/main.jar
		cp ${COMP_DIR}/je/build/lib/je.jar ./lib/compare.jar
		ant filefmt -Dtestjar.v1=lib/compare.jar -Dtestjar.v2=lib/main.jar >& ${DBSIM_DIR}/Case_${CASE}-JDK_${JAVA_VERN}.tmp
	fi


	rm -rf ${TEST_DIR}
	cd ${ROOT_DIR}
}

save_results() {
	echo "Get the files"

    BUILDID=`ssh -l jenkins slc04ark "cat /scratch/jenkins/jobs/je_dbsim_embedded_abort/nextBuildNumber"`
    BUILDID=`expr $BUILDID - 1`
    LOGLOCATION=~/log_archive/je_dbsim_embedded_abort/$BUILDID
    mkdir -p $LOGLOCATION

    cd $LOGLOCATION
    echo "Host: slc04aro.us.oracle.com" >> ${JE_DIR}/location_of_environment_and_log.txt
    echo "Directory: `pwd`" >> ${JE_DIR}/location_of_environment_and_log.txt
    echo "Username: tests" >> ${JE_DIR}/location_of_environment_and_log.txt
    echo "Password: 123456" >> ${JE_DIR}/location_of_environment_and_log.txt

    cp -r ${ROOT_DIR} $LOGLOCATION

	cd ${JE_DIR} && scp jenkins_changeset.txt location_of_environment_and_log.txt jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace/
    scp ${ROOT_DIR}/test_* jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace/
    scp ${DBSIM_DIR}/\[log\]DBSim_EmbeddedAbort_* jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace/

}

build_jejar() {
	# compile and generate the je.jar
	cd ${JE_DIR} && ant jar
	cd ${JE_DIR}/build/lib && cp je.jar ${DBSIM_DIR}/lib/
}

dbsim() {
	export export ANT_HOME=${ant_home}
	export CLASSPATH=$classpath

	#for JDK_HOME in "$JDK7_HOME" "$JDK6_HOME"
	for JDK_HOME in "$JDK8_HOME"
	do
		export JAVA_HOME=$JDK_HOME
		export PATH=$JAVA_HOME/bin:$ANT_HOME/bin:$path

		get_jdk_version

		# Generate the report.
		gen_env
		
		# build je.jar
		build_jejar


        if [ "$JDK_HOME" == "$JDK8_HOME" ] ; then
            VER_STR="JDK8"
        fi

        if [ "$JDK_HOME" == "$JDK6_HOME" ] ; then
            VER_STR="JDK6"
        fi

        date_start=`date +"%s"`		

		CASE="embedded_abort"
		echo "running $CASE under $JAVA_VERN"
		run_dbsim

        date_end=`date +"%s"`
        intervel=$[$date_end - $date_start]
        cd ~/ && bash error_substract_dbsim.sh ${DBSIM_DIR}/Case_${CASE}-JDK_${JAVA_VERN}.tmp output.log "JE.DBSim" "EmbeddedAbort" $intervel ${ROOT_DIR}/test_${JAVA_VERN}.xml
        tar czf ${DBSIM_DIR}/[tar]DBSim_EmbeddedAbort_${VER_STR}.tar.gz ${DBSIM_DIR}/Case_${CASE}-JDK_${JAVA_VERN}.tmp
        tail ${DBSIM_DIR}/Case_${CASE}-JDK_${JAVA_VERN}.tmp -n 100 > ${DBSIM_DIR}/[log]DBSim_EmbeddedAbort_${VER_STR}.log

	done

	save_results
}

if [ X$LOG_LEVEL == X"INFO" ] ; then
	echo "com.sleepycat.je.util.ConsoleHandler.level=INFO" > ${ROOT_DIR}/je/logging.properties
fi

# run the dbsim
dbsim

# log files

