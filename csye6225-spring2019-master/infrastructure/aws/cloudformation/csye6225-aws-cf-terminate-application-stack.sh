#! /bin/bash

if [ -z "$1" ]
  then
    echo "Parameters missing. Please input stack name"
    exit 1
fi


echo "Checking if stack exists"
var1=$(aws cloudformation describe-stacks --stack-name "$1-App" --query "Stacks[0].StackId" --output text 2>&1)

if [ $? -eq 0 ]
    then
	echo "Stack found with id $var1"
        echo "Starting deletion of stack $var1"	
        id=$(aws cloudformation describe-stacks --stack-name $var1 --query "Stacks[*].StackId" --output text 2>&1)
        aws cloudformation delete-stack --stack-name $var1
        aws cloudformation wait stack-delete-complete --stack-name $var1
        aws cloudformation describe-stacks --stack-name $id --query "Stacks[*].StackStatus" --output text

   if [ $? -eq 0 ]
        then
            echo "Stack $1-App successfully deleted!!!"
   else
 	    echo "Failed Stack deletion"
 	    exit 1
   fi

   
else
	echo "Stack doesn't exist"
	exit 0
fi

