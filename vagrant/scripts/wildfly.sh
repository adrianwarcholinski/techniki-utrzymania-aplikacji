WILDFLY_VERSION=20

sudo yum update
sudo yum install -y default-jdk
sudo yum install -y wget

sudo wget -q https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz -P /tmp
sudo tar -xf /tmp/wildfly-$WILDFLY_VERSION.tar.gz -C /opt/
sudo ln -s /opt/wildfly-$WILDFLY_VERSION /opt/wildfly

sudo groupadd -r wildfly
sudo useradd -r -g wildfly -d /opt/wildfly -s /sbin/nologin wildfly
sudo chown -RH wildfly: /opt/wildfly

sudo systemctl daemon-reload
sudo systemctl enable wildfly
sudo systemctl start wildfly
