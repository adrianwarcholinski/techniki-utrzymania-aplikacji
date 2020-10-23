sudo rpm -Uvh https://repo.mysql.com/mysql80-community-release-el7-3.noarch.rpm
sudo sed -i 's/enabled=1/enabled=0/' /etc/yum.repos.d/mysql-community.repo
sudo yum -y --enablerepo=mysql80-community install mysql-community-server ## CentOS & RedHat
sudo systemctl enable mysqld.service
sudo systemctl start mysqld.service
passwordString=$(sudo grep "A temporary password" /var/log/mysqld.log)
passowrd=$(echo $passowrdString | awk 'NF { print $NF }')
echo "$password" >test.txt
