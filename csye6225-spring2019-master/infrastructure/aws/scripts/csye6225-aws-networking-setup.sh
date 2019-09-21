handle_error()
{
    if [ -z "$1" ] 
     then exit 
    fi
}

handle_creation_error()
{
    if [ $1 -ne "0" ] 
     then exit 
    fi
}


VPC_NAME=$1
handle_error $VPC_NAME
echo "Creating VPC...."
VPC_ID=$(aws ec2 create-vpc --cidr-block 10.0.0.0/24 | jq -r '.Vpc .VpcId')
handle_error $VPC_ID
echo "VPC created with ID: $VPC_ID"

aws ec2 create-tags --resources $VPC_ID --tags Key=Name,Value=$VPC_NAME-csye6225-vpc


echo "Creating subnets...."
SUBNET_ID1=$(aws ec2 create-subnet --vpc-id $VPC_ID --cidr-block 10.0.0.0/26 --availability-zone us-east-1a | jq -r '.Subnet .SubnetId')
handle_error $SUBNET_ID1
aws ec2 create-tags --resources $SUBNET_ID1 --tags Key=Name,Value=$VPC_NAME-csye6225-subnet1
echo "Subnet $SUBNET_ID1 created for VPC $VPC_ID"

SUBNET_ID2=$(aws ec2 create-subnet --vpc-id $VPC_ID --cidr-block 10.0.0.64/26 --availability-zone us-east-1b | jq -r '.Subnet .SubnetId')
handle_error $SUBNET_ID2
aws ec2 create-tags --resources $SUBNET_ID2 --tags Key=Name,Value=$VPC_NAME-csye6225-subnet2
echo "Subnet $SUBNET_ID2 created for VPC $VPC_ID"

SUBNET_ID3=$(aws ec2 create-subnet --vpc-id $VPC_ID --cidr-block 10.0.0.128/26 --availability-zone us-east-1c | jq -r '.Subnet .SubnetId')
handle_error $SUBNET_ID3
aws ec2 create-tags --resources $SUBNET_ID3 --tags Key=Name,Value=$VPC_NAME-csye6225-subnet3
echo "Subnet $SUBNET_ID3 created for VPC $VPC_ID"
echo "Subnets Created"


echo "Creating Internet Gateway...."
IG_ID=$(aws ec2 create-internet-gateway | jq -r '.InternetGateway .InternetGatewayId')
handle_error $IG_ID
aws ec2 create-tags --resources $IG_ID --tags Key=Name,Value=$VPC_NAME-csye6225-ig
echo "Internet Gateway with id $IG_ID created"

echo "Attaching Internet Gate to VPC...."
ATTACH_IG=$(aws ec2 attach-internet-gateway --internet-gateway-id $IG_ID --vpc-id $VPC_ID)
handle_error $IG_ID
echo "Internet Gateway $IG_ID attached to VPC $VPC_ID"


echo "Creating Route Table...."
ROUTETBL_ID=$(aws ec2 create-route-table --vpc-id $VPC_ID | jq -r '.RouteTable .RouteTableId')
handle_error $ROUTETBL_ID
aws ec2 create-tags --resources $ROUTETBL_ID --tags Key=Name,Value=$VPC_NAME-csye6225-rt
echo "Route Table $ROUTETBL_ID created for VPC $VPC_ID"

echo "Associating subnets to route table..."
aws ec2 associate-route-table --route-table-id $ROUTETBL_ID --subnet-id $SUBNET_ID1
aws ec2 associate-route-table --route-table-id $ROUTETBL_ID --subnet-id $SUBNET_ID2
aws ec2 associate-route-table --route-table-id $ROUTETBL_ID --subnet-id $SUBNET_ID3
echo "Subnets Associated"


echo "Creating Route...."
ROUTE_ID=$(aws ec2 create-route --route-table-id $ROUTETBL_ID --destination-cidr-block 0.0.0.0/0 --gateway-id $IG_ID | jq -r '.Route .RouteId')
handle_error $ROUTE_ID
echo "Route $ROUTETBL_ID created for Internet Gateway $IG_ID for VPC $VPC_ID"
 
SECURITYGRP_ID=$(aws ec2 describe-security-groups | jq --arg vpcid "$VPC_ID" -r '.SecurityGroups | .[] | select(.VpcId==$vpcid) | .GroupId')

echo "Revoking default ingresses..."
aws ec2 revoke-security-group-ingress --group-id $SECURITYGRP_ID --source-group $SECURITYGRP_ID --protocol all
handle_creation_error $?
echo "Revoked default ingresses"

echo "Creating new inbound rule to allow only TCP traffic on ports 22 and 80...."

aws ec2 authorize-security-group-ingress --group-id $SECURITYGRP_ID --protocol tcp --port 22 --cidr 0.0.0.0/0
handle_creation_error $?

echo "Enabled TCP traffic from anywhere to port 22"
aws ec2 authorize-security-group-ingress --group-id $SECURITYGRP_ID --protocol tcp --port 80 --cidr 0.0.0.0/0
handle_creation_error $?

echo "Enabled TCP traffic from anywhere to port 80"
