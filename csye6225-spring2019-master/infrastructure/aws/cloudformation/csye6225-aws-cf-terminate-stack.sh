#!/bin/bash
if [ -z "$1" ]
then
	echo "Please provide a valid command line argument for stack_name"
	exit 1
else
	echo "Initiating with deletion of stack using cloudformation"
fi

var1=$(aws cloudformation describe-stacks --stack-name "$1-Network" --query "Stacks[0].StackId" --output text 2>&1)

if [ $? -eq 0 ]
    then
        id=$(aws cloudformation describe-stacks --stack-name $var1 --query "Stacks[*].StackId" --output text 2>&1)
        aws cloudformation delete-stack --stack-name $var1
        aws cloudformation wait stack-delete-complete --stack-name $var1
        aws cloudformation describe-stacks --stack-name $id --query "Stacks[*].StackStatus" --output text

   if [ $? -eq 0 ]
        then
            echo "Stack $1-Netowrk successfully deleted!!!"
   else
 	    echo "Failed Stack deletion"
 	    exit 1
   fi

   

else
	echo "Stack $1-Network doesn't exist"
	exit 0
fi




