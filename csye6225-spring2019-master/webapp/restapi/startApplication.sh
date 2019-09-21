#! /bin/bash

echo "#! /bin/bash" > /tmp/tempFile.sh
echo "source /etc/profile" >> /tmp/tempFile.sh
sudo chmod 777 /tmp/tempFile.sh
sudo fuser -k 80/tcp
echo "cd /var" >> /tmp/tempFile.sh
echo "sudo systemctl restart amazon-cloudwatch-agent" >> /tmp/tempFile.sh
echo "sudo java -jar restapi.jar --server.port=80 --spring.datasource.url=jdbc:mysql://\$DB_HOST:\$DB_PORT/csye6225 --spring.datasource.username=\$DB_USERNAME --spring.datasource.password=\$DB_PASSWORD --cloud.islocal=false --cloud.bucketName=\$S3_BUCKET --cloud.snsTopic=\$SNS_TOPIC" >> /tmp/tempFile.sh

cd /tmp/

./tempFile.sh > run.out 2> run.err &
