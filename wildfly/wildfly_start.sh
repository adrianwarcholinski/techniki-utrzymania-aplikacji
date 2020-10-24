#!/bin/bash
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#                                                                                       #
# = = = = = = = = =  Starting Wildfly container = = = = = = = = = = = = = = = = = = = = #
#                                                                                       #
# Usage to launch in debug mode                                                         #
#  ... -e DEBUG=true                                                                    #
#                                                                                       #
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #


if [ ! -f /opt/wildfly/.wildfly_admin_created ]; then
  /opt/wildfly/wildfly_add_admin_user.sh
fi


exec /opt/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 --debug *:8787 --server-config=standalone.xml

