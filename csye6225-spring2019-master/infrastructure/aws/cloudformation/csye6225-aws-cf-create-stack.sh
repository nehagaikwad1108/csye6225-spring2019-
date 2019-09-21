#! /bin/bash


#########################################################################
### Author 	: Jayesh Iyer   					#
### NUID   	: 001472726     					#
### Description : This script creates a vpc stack on AWS CLI using 	#
###		  Cloud Formation Template.				#
### Usage	: sh script.sh <template.json> <Stack_NAME>		# 									#
#########################################################################



TEMPLATE_NAME=$1
STACK_NAME=$2

if [ -z "$1" ] || [ -z "$2" ]
  then
    echo "Error! Argument Required"
    echo "Usage - sh script.sh <TemplateFile> <Stack_Name>" 
    exit 1
fi

if [ ! -e $TEMPLATE_NAME ]
   then
     echo "Error! Template File not exisits"
     exit 1
fi     

###### REPLACE=$(sed -i 's/stackvariable/'${STACK_NAME}'/g' template.json)

echo "Creating stack..."
STACK_ID=$( \
  aws cloudformation create-stack \
  --stack-name ${STACK_NAME} \
  --template-body file://${TEMPLATE_NAME} \
  --parameters ParameterKey=StackName,ParameterValue=${STACK_NAME} \
  | jq -r .StackId \
)

echo "Waiting on ${STACK_ID} create completion..."
aws cloudformation wait stack-create-complete --stack-name ${STACK_ID}
echo "Status of create Stack is as below :"
aws cloudformation describe-stacks --stack-name ${STACK_ID} | jq .Stacks[0].StackStatus
