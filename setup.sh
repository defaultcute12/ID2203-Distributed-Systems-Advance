#! /bin/bash
###############################################################################
#     File Name           :     setup.sh                                      #
#     Created By          :     Klas Segeljakt                                #
#     Creation Date       :     [2017-02-07 13:12]                            #
#     Last Modified       :     [2017-02-07 17:48]                            #
#     Description         :     Setup script for project.                     #
###############################################################################

if [ "$1" == "setup" ]; then
    java -jar server/target/project17-server-1.0-SNAPSHOT-shaded.jar -p 55770 > /dev/null 2>&1 &
    java -jar server/target/project17-server-1.0-SNAPSHOT-shaded.jar -p 55771 -c 127.0.0.1:55770 > /dev/null 2>&1 &
    java -jar server/target/project17-server-1.0-SNAPSHOT-shaded.jar -p 55772 -c 127.0.0.1:55770 > /dev/null 2>&1 &
    java -jar client/target/project17-client-1.0-SNAPSHOT-shaded.jar -p 55773 -b 127.0.0.1:55770
elif [ "$1" == "teardown" ]; then
    jobs -l | grep "].[0-9]+ " > asd.txt
    kill -9 $(jobs -l |  grep "]. [0-9]+ ")
fi
