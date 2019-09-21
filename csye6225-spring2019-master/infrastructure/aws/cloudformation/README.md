# CSYE 6225 - Spring 2019

This repository contains shell scripts to create and delete AWS Virtual Private Cloud (VPC) using the AWS Command Line Interface (AWS CLI).


## Contents
- csye6225-aws-cf-create-stack.sh : The script automates the creation of a custom IPv4 VPC, having 3 public subnets, a public route table and an Internet gateway.
- csye6225-aws-cf-terminate-stack.sh : The script automates the deletion of a previously create VPC Stack.
- csye6225-aws-cf-create-application-stack.sh : This script automates the creation of an application Stack consisting of EC2 Instance for WebApp , RDS Instance for MariaDB and DynamoDBi.
- csye6225-aws-cf-terminate-application-stack.sh : This script automates the deletion of a previously created application stack.


## Prerequisites
- AWS CLI
- JQ Library for Bash


## Configuration
- AWS Cloud formation uses templates in either JSON or YAML format.  
- [Sample Reference Templates](https://aws.amazon.com/cloudformation/aws-cloudformation-templates/)
   

## Usage
1. Clone the repository into your local folder 
2. Navigate to the AWS CloudFormation folder 
   ```
   cd <local-folder-path>/infrastructure/aws/cloudformation/
   ```
3. Create new AWS Cloudformation template as per requirements and place it in same folder as the script.
   ```
   vi template.json
   ```
4. Run network stack creation script to create a new VPC.
   ```
   sh csye6225-aws-cf-create-stack.sh <Template_FILE> <STACK_NAME>
   ```
   Run application stack creation script to create a new Application Stack.
   ```
   sh csye6225-aws-cf-create-application-stack.sh <Template_FILE> <STACK_NAME> <AWS_KeyPair_NAME>
   ```
5. Run network deletion script to delete an existing VPC Stack.
   ```
   sh csye6225-aws-cf-terminate-stack.sh <STACK_NAME>
   ```
   Run application deletion script to delete an application stack.
   ```
   sh csye6225-aws-cf-terminate-application-stack.sh <STACK_NAME>
   ```
