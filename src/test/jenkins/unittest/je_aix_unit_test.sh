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

export JAVA_HOME=/scratch/nosql/ibm-java-ppc64-71
export ANT_HOME=/scratch/nosql/app/ant
export PATH=$PATH:$JAVA_HOME/bin:$ANT_HOME/bin

echo " "
echo "========================================================="
echo "                                                        "
/scratch/nosql/ibm-java-ppc64-71/bin/java -version
ant -version
echo "JAVA_HOME=$JAVA_HOME                                   "
echo "ANT_HOME=$ANT_HOME                                     "
echo "                                                        "
echo "========================================================="
echo " "


ssh tests@slc04arl "rm -rf ~/je_aix_unit_test && mkdir ~/je_aix_unit_test"
ssh tests@slc04arl "cd je_aix_unit_test && hg clone ssh://adqian@sleepycat-scm.us.oracle.com//a/hgroot/je"
ssh tests@slc04arl "cd je_aix_unit_test/je && hg log -l 1 -v >> ./jenkins_changeset.txt"

rm -rf ~/je_aix_unit_test
scp -r tests@slc04arl:~/je_aix_unit_test .

cd ~/je_aix_unit_test/je && ant -lib /scratch/nosql/app/ant/lib/junit-4.10.jar test -Dproxy.host=www-proxy -Dproxy.port=80 -Djvm=/scratch/nosql/ibm-java-ppc64-71/bin/java

BUILDID=`ssh -l jenkins slc04ark "cat /scratch/jenkins/jobs/${TASK_NAME}/nextBuildNumber"`
BUILDID=`expr $BUILDID - 1`
LOGLOCATION=~/log_archive/${TASK_NAME}/$BUILDID

echo "Host: stuzx68.us.oracle.com" >> ~/je_aix_unit_test/je/location_of_environment_and_log.txt

mkdir -p $LOGLOCATION
cd $LOGLOCATION

echo "Directory: `pwd`" >> ~/je_aix_unit_test/je/location_of_environment_and_log.txt
echo "Username: nosql" >> ~/je_aix_unit_test/je/location_of_environment_and_log.txt
echo "Password: q" >> ~/je_aix_unit_test/je/location_of_environment_and_log.txt

cp -r ~/je_aix_unit_test/je $LOGLOCATION

ssh jenkins@slc04ark "rm -rf ~/jobs/${TASK_NAME}/workspace/location_of_environment_and_log"
ssh jenkins@slc04ark "mkdir -p ~/jobs/${TASK_NAME}/workspace/location_of_environment_and_log"

cd ~/je_aix_unit_test/je && scp jenkins_changeset.txt jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace/ && scp location_of_environment_and_log.txt jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace/location_of_environment_and_log
cd ~/je_aix_unit_test/je && scp -r build/test/data/ jenkins@slc04ark:~/jobs/${TASK_NAME}/workspace/



