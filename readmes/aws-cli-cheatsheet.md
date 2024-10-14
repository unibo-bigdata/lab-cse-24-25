# AWS CLI Cheatsheet

In this README, you can find a reference to the most used AWS CLI commands for this course.

The full documentation is available [here](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/index.html#cli-aws).

This file will be updated throughout the course.

## Profile creation

>**IMPORTANT** - Profile information (access key, secret access key, and token) must be updated every time you restart the Virtual Lab.

To create (or to update) a profile, issue the following command.

```bash
# Replace <my_profile_name> with a name of your choice; what I use is aws-2425-class1-egallinucci (we will create another key-pair for class2)
aws configure --profile <my_profile_name> 
```

You will be asked the following details:
- *AWS Access Key ID*: you find it in the AWS Acamedy website (where you cliuck the "Start Lab" button); here, look for AWS details > AWS CLI > aws_access_key_id
- *AWS Secret Access Key*: as above
- *Default region name*: us-east-1
- *Default output format*: leave None (just hit Enter)

Next, set the environment variable pointing to the profile.

```bash
# UNIX (or using Git Bash on Windows)
export AWS_PROFILE=<my_profile_name>

# Using Window's Command Prompt
setx AWS_PROFILE <my_profile_name>
```

Add the token to enable access.

```bash
aws configure set aws_session_token <my_token>
# Replace <my_token> with the token you find in the AWS Acamedy website, together with the aws_access_key_id (see above)
```

## Key-pairs

Show existing key-pairs

```bash
aws ec2 describe-key-pairs
```

Create a key-pair
```bash
aws ec2 create-key-pair --key-format ppk --key-name <my_key_pair> --output text > keys/<my_key_pair>.ppk
# Replace <my_key_pair> with a name of your choice; this command creates the ppk and downloads it into the keys folder
```

## S3

Documentation is available [here (lower-level commands)](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/s3/index.html#cli-aws-s3) and [here (higher-level commands)](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/s3api/index.html#cli-aws-s3api).


See available buckets 
```bash
aws s3api list-buckets
```

Create a bucket
```bash
aws s3api create-bucket --bucket <my_bucket_name>
```

Create a folder
```bash
aws s3api put-object --bucket <my_bucket_name> --key <my_folder_name>/
```

Copy, rename, and delete a folder
```  bash
aws s3 cp s3://<my_bucket_name>/<my_folder_name>/ s3://<my_bucket_name>/<another_folder_name>/

aws s3 mv s3://<my_bucket_name>/<my_folder_name>/ s3://<my_bucket_name>/<another_folder_name>/

aws s3 rm s3://<my_bucket_name>/<my_folder_name>/
```

Upload a file

```bash
aws s3api put-object --bucket <my_bucket_name> --key <path_to_file_on_s3> --body <local_path_to_file>
```

List entire content of a bucket
```bash
aws s3api list-objects-v2 --bucket <my_bucket_name>
```

Filter content of a bucket (e.g., to find specific folders and files)
```bash
aws s3api list-objects-v2 --bucket <my_bucket_name> --prefix <path_to_filter_on>
```

## EMR

Documentation is available [here](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/emr/index.html#cli-aws-emr).


Enable inbound SSH connections to the Security Group of the Master node (must be done only once, but one cluster must have been created first - see next command).

```bash
aws ec2 authorize-security-group-ingress --group-name ElasticMapReduce-master --ip-permissions IpProtocol=tcp,FromPort=22,ToPort=22,IpRanges="[{CidrIp=0.0.0.0/0}]"
```

To decide which EMR versions to load, see [here](https://docs.aws.amazon.com/emr/latest/ReleaseGuide/emr-release-components.html).

Create cluster (see examples [here](https://github.com/aws/aws-cli/blob/develop/awscli/examples/emr/create-cluster-examples.rst)); this is the default configuration for laboratories, but you can change it for the project (e.g., to increase the computational power if needed).

```
aws emr create-cluster \
    --name "Big Data Cluster" \
    --release-label "emr-7.3.0" \
    --applications Name=Hadoop Name=Spark \
    --instance-groups InstanceGroupType=MASTER,InstanceCount=1,InstanceType=m4.large InstanceGroupType=CORE,InstanceCount=2,InstanceType=m4.large \
    --service-role EMR_DefaultRole \
    --ec2-attributes InstanceProfile=EMR_EC2_DefaultRole,KeyName=<my_key_pair_name> \
    --region "us-east-1"
```

List clusters

```bash
# All clusters
aws emr list-clusters 

# Only the last one
aws emr list-clusters --max-items 1
```

See cluster details (needs the cluster_id, which is given at creation time and can be found by listing clusters)

```bash
aws emr describe-cluster --cluster-id <my_cluster_id>
```

Find the MasterPublicDnsName (to be used for SSH connections)
```bash
aws emr list-instances --cluster-id <cluster_id> --instance-group-type MASTER
```

Terminate a cluster

```bash
aws emr terminate-clusters --cluster-ids <cluster_id>
```

