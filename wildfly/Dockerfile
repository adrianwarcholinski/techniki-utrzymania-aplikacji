FROM jboss/wildfly:20.0.0.Final

COPY modules/ /opt/jboss/wildfly/modules/
COPY standalone.xml /opt/jboss/wildfly/standalone/configuration/
ADD ssbd01.war /opt/jboss/wildfly/standalone/deployments/
