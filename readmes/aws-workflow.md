# AWS Workflow

In this README, you can find a vademecum of the steps you need to take run Spark jobs on an AWS cluster.


- Cluster setup
    - [Start the AWS environment](101.md#101-1b-turning-on-the-virtual-lab-on-aws-academy)
        - [Login](https://awsacademy.instructure.com/), Modules, Learner Lab, Start Lab
    - Open the AWS CLI
    - [Setup the profile on the AWS CLI](101.md#101-1d-aws-cli-configuration): access key, secret access key, export env variable, set token
        - Get the data from the AWS page where you started the lab
        - Run `aws ec2 describe-key-pairs` to check the connection
        - If it doesn't work, recreate the profile and make sure you copy-paste credentials correctly
    - *(only once)* [Create a key-pair](101.md#101-1d-aws-cli-configuration)
        - Make sure that the key-pair is well-formatted
        - Make sure that you don't lose it or mess with it; if you do, you must re-create it
    - [Create a cluster](101.md#101-3-working-with-spark)
        - EC2 machines are limited to m4.large (2 cores, 8GB of RAM)
        - You cannot request a cluster with more than 32 cores overall
        - Double-check the number of "core" instances, depending on the amount of resources you plan to use
    - [Get the MasterDNS of the cluster](aws-cli-cheatsheet.md#emr) (will be needed in the Spark run configuration)
        - `aws emr list-instances --cluster-id <cluster_id> --instance-group-type MASTER`
    - *(only once)* [Make sure that SSH connections to the master node are possible](aws-cli-cheatsheet.md#emr)
        - `aws ec2 authorize-security-group-ingress --group-name ElasticMapReduce-master --ip-permissions IpProtocol=tcp,FromPort=22,ToPort=22,IpRanges="[{CidrIp=0.0.0.0/0}]"`
- [Spark application setup](101.md#101-3b-localremote-deployment-spark-submit) and run
    - Open Intellij IDEA
    - Update the `src/main/resources/aws-credentials.txt` file
    - Make sure that the `Config.scala` file is set correctly
    - Build the jar with `./gradlew` from the terminal
        - If it fails, try deleting the `build` folder
    - Create/Edit the Spark Cluster run configuration
        - Make sure that the host in the EMR connection is the MasterDNS of the cluster
        - Make sure that YARN Client is selected as deploy mode
        - Make sure that Executor parameters are set
        - Make sure that all program parameters are given
        - Make sure that the jar has been built after the last edit
    - Run
    - Open AWS's web UI 
        - Click the green light on the page where you start the lab
    - Go to EMR > Your cluster > Click the "Spark History Server" link
    - Download the Application report (downloads a zip)
- [History Server setup](101.md#101-extra-configuring-your-own-computer)
    - *(only once)* Make sure that Spark is installed correctly on the computer
    - *(only once)* Make sure that the `conf/spark-default.conf` file in Spark's installation folder is configured correctly
    - Put the file from the downloaded zip into the history folder
    - Open a Git Bash on Spark's installation folder and launch the server
        - `bin/spark-class.cmd org.apache.spark.deploy.history.HistoryServer`
        - The command is NOT supposed to terminate, as it will keep the server active
        - If the command fails because of a file that cannot be accessed nor removed (can happen on lab machine), make a new folder and indicate it in the conf file
    - Access the Spark UI at [http://localhost:18080](http://localhost:18080)
        
