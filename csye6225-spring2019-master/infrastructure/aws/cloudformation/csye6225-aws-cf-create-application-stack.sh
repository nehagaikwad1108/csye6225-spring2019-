#! /bin/bash


#########################################################################
### Author 	: Jayesh Iyer   					#
### NUID   	: 001472726     					#
### Description : This script creates a application stack on AWS CLI  	#
###		  using Cloud Formation Template.			#
#########################################################################



TEMPLATE_NAME=$1

if [ -z "$1" ] 
  then
    echo "Error! Argument Required"
    echo "Usage - sh script.sh <TemplateFile> " 
    exit 1
fi

if [ ! -e $TEMPLATE_NAME ]
     then
       echo "Error! Template File not exisits"
       exit 1
fi

echo "Enter Network Stack Name:"
read NETWORK_STACK_NAME

echo "Enter Application STACK_NAME"
read STACK_NAME

echo "Enter KeyPair Name"
read KEY_NAME

echo "Enter Bucket Name for EC2"
read BUCKET_NAME

echo "Enter Bucket Name for Lambda Function"
read LAMBDA_BUCKET_NAME

echo "Enter Application Zip Name"
read ZIP_FILE

###### REPLACE=$(sed -i 's/stackvariable/'${STACK_NAME}'/g' template.json)

echo "Fetching latest AMI Image"
ImageId=$(aws ec2 describe-images --owners self --filter "Name=name,Values=csye6225_??????????" --output json | jq -r '.Images | sort_by(.CreationDate) | last(.[]).ImageId')
echo "Image ID : $ImageId "

echo "Fetching AWS ARN for SSL Certificate"
CertificateArn=$(aws acm list-certificates --certificate-statuses ISSUED --query "CertificateSummaryList[?DomainName=='csye6225-spring2019-$BUCKET_NAME.me']"  | jq -r ".[0].CertificateArn")
echo "CertificateArn : $CertificateArn"


echo "Creating stack..."
STACK_ID=$( \
  aws cloudformation create-stack \
  --stack-name ${STACK_NAME} \
  --template-body file://${TEMPLATE_NAME} \
  --parameters ParameterKey=NetworkStackName,ParameterValue=${NETWORK_STACK_NAME}  \
  ParameterKey=ImageId,ParameterValue=$ImageId \
  ParameterKey=LambdaBucketName,ParameterValue=$LAMBDA_BUCKET_NAME \
  ParameterKey=ZipFile,ParameterValue=$ZIP_FILE \
  ParameterKey=KeyName,ParameterValue=$KEY_NAME \
  ParameterKey=BucketName,ParameterValue=$BUCKET_NAME \
  ParameterKey=CertificateArn,ParameterValue=$CertificateArn \
  | jq -r .StackId \
)

echo "Waiting on ${STACK_ID} create completion..."
aws cloudformation wait stack-create-complete --stack-name ${STACK_ID}
echo "Create Application Stack Status is : "
aws cloudformation describe-stacks --stack-name ${STACK_ID} | jq .Stacks[0].StackStatus
