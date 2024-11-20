# 101 Setup

The goal of this lab is to gain access to the Virtual Lab on AWS Academy, load some datasets on S3 (that will be used in future classes), and understand how we are going to work with Spark - locally and on EMR.

Here is a summary:
- [101-1 Access to AWS Academy](#101-1-access-to-aws-academy)
  - [101-1.a Registration to AWS Academy](#101-1a-registration-to-aws-academy)
  - [101-1.b Turning on the Virtual Lab on AWS Academy](#101-1b-turning-on-the-virtual-lab-on-aws-academy)
  - [101-1.c Accesing Virtual Lab on AWS Academy: GUI or CLI?](#101-1c-accesing-virtual-lab-on-aws-academy-gui-or-cli)
  - [101-1.d AWS CLI configuration](#101-1d-aws-cli-configuration)
- [101-2 Working with S3 and uploading datasets](#101-2-working-with-s3-and-uploading-datasets)
- [101-3 Working with Spark](#101-3-working-with-spark)
  - [101-3.a Local interactive way (notebook)](#101-3a-local-interactive-way-notebook)
  - [101-3.b Local/Remote deployment (spark-submit)](#101-3b-localremote-deployment-spark-submit)
- [101-extra Configuring your own computer](#101-extra-configuring-your-own-computer)

## 101-1 Access to AWS Academy

Follow these instructions to gain access to the AWS Academy and start working with it.

### 101-1.a Registration to AWS Academy

AWS Academy offers *classes* where students are allowed free usage of some AWS resources up to 50$; registration is necessary, but not credit card is needed.

Classes have a **duration of 6 months**; after that, all resources will be lost. To give you access for the full academic year, there will be two classes:
- Class 1: valid from September 14, 2024, to March 15, 2025.
- Class 2: valid from March 16, 2025, to September 14, 2025.

Invites will be sent to all students registered to the course on [Virtuale](https://virtuale.unibo.it) according to this modality:
- All students registered on Virtuale by September 30, 2024, will receive an invitation to Class 1 on October 1, 2024.
- All students registered on Virtuale by February 28, 2025, will receive an invitation to Class 2 on March 1, 2025.

When you receive the invitation link, proceed with the registration. Contact the teacher in case of issues.

### 101-1.b Turning on the Virtual Lab on AWS Academy

The Virtual Lab must be turned on every time you want to work with the AWS resources. **It turns off automatically after 4 hours**, unless you ask for an extension of 4 more hours.

- [**Access AWS Academy here**](https://awsacademy.instructure.com/)
- Go to "Modules" > "Launch AWS Academy Learner Lab"
  - Agree to the terms and conditions (only the first time)
- Click the "Start Lab" button (top-right corner)
  - The first time, it will take about 5 minutes to startup; next times, it will take only a few seconds
- A *countdown* will appear, starting from 4 hours
  - **To extend the 4-hour window**, click the "Start Lab" button again
- A *credit counter* will appear, indicating the amount of credit left
- Click on "AWS" (top-left corner) when you have a green light
  - You will now be in your own virtual lab
  - This is the ***Console home*** of the Virtual Lab
- **Click the "End Lab"** button (top-right corner) at the end of class
  - If you don't, it should automatically shutdown in 4 hours, but 1) it will consume credit (only EC2 instances are automatically shut down), and 2) better safe than sorry.

#### Troubleshooting (Q&A)

>*Q: What happens when I end the Virtual lab?*  
>A: It depends on the service: data in S3 will persist (and consume very little credit even when the Virtual Lab is down); EMR clusters (and all data within them, e.g., anything you save onto HDFS) will be completely destroyed.
>
>*Q: What happens when I finish my credits?*  
>A: You will not be able to use the Virtual Lab anymore, not even the data in S3. However, you can request an additional Virtual Lab environment to be instantiated on a different account (with a different email), just ask the teacher.

### 101-1.c Accesing Virtual Lab on AWS Academy: GUI or CLI?

After launching the Virtual Lab (see 101-1.b), you can interact with AWS resources in two ways: via GUI or via CLI. What you can do is the same.
- Pros of GUI
  - It's nicer
  - No need to learn commands and syntax
- Cons of GUI
  - It's slow
  - It can change unpredicatbly

This year, we will use the CLI. 

### 101-1.d AWS CLI configuration

Open a Git Bash (recommended) or a Command Prompt on the directory where you have cloned this repo.

To use the AWS CLI, you need to create a profile and a key-pair (.ppk).

#### Profile creation

>**IMPORTANT**
>
> Profile information (access key, secret access key, and token) must be updated every time you restart the Virtual Lab.

To create (or to update) a profile, issue the following command.

```bash
# Replace <my_profile_name> with a name of your choice; what I use is aws-2425-class1-egallinucci (we will create another key-pair for class2)
aws configure --profile <my_profile_name> 
```

You will be asked the following details:
- *AWS Access Key ID*: you find it in the AWS Acamedy website (where you cliuck the "Start Lab" button); here, look for AWS details > AWS CLI > aws_access_key_id
- *AWS Secret Access Key*: as above
- *Default region name*: us-east-1
- *Default output format*: json

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

#### Key-pair creation

```bash
aws ec2 describe-key-pairs
# Shows the list of created keys

aws ec2 create-key-pair --key-format ppk --key-name <my_key_pair> --output text > keys/<my_key_pair>.ppk
# Replace <my_key_pair> with a name of your choice; this command creates the ppk and downloads it into the keys folder
```

#### Troubleshooting (Q&A)

>*Q: I get a 400 error when creating the key-pair.*  
>A: The profile and/or the token may have been copy-pasted incorrectly. 
> Try to recreate them, even if you are sure that you did it correctly. 
>
>*Q: I get an error message stating that the .ppk is not valid.*  
>A: The .ppk may be incorrect; check that:
>- it begins with "PuTTY-User-Key-File-2: ssh-rsa"
>- it ends with the Private-MAC
>
>*Q: I get an error message stating that the access permissions on the .ppk are not valid.*  
>A: Change permissions to make the file accessible only by the owner; for this, use the command ```chmod 400 file.ppk```.

## 101-2 Working with S3 and uploading datasets

Understand how to work with S3 via CLI and upload some datasets. 

Documentation is available [here (lower-level commands)](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/s3/index.html#cli-aws-s3) and [here (higher-level commands)](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/s3api/index.html#cli-aws-s3api).

- See available buckets 
  ```bash
  aws s3api list-buckets
  ```

- **Create your bucket**; replace <my_bucket_name> with something like *unibo-bd2425-egallinucci*
  ```bash
  aws s3api create-bucket --bucket <my_bucket_name>
  ```

- **Create datasets folder**
  ```bash
  aws s3api put-object --bucket <my_bucket_name> --key datasets/
  ```

- Copy, rename, and delete a folder
  ```  bash
  aws s3 cp s3://<my_bucket_name>/datasets/ s3://<my_bucket_name>/somefoldername/
  
  aws s3 mv s3://<my_bucket_name>/somefoldername/ s3://<my_bucket_name>/foldertodelete/

  aws s3 rm s3://<my_bucket_name>/foldertodelete/
  ```

- **Upload datasets**
  - Inside the S3 "datasets" folder, put all the files that you find in the "datasets" folder of this repo
  - Download the ZIP file [here](https://big.csr.unibo.it/downloads/bigdata/weather-datasets.zip), unzip it inside the "datasets/big" folder of this repo (which is not committed), and upload these files as well
  
  ```bash
  aws s3api put-object --bucket <my_bucket_name> --key datasets/capra.txt --body datasets/capra.txt
  
  # Repeat for other files as well
  ```

- Check contents of bucket and folders
  ```bash
  aws s3api list-objects-v2 --bucket <my_bucket_name>
  aws s3api list-objects-v2 --bucket <my_bucket_name> --prefix datasets/
  ```

Ultimately, you should end up with the following datasets.

- *capra.txt*: tiny unstructured file with a known Italian riddle (for debugging purposes)
- *divinacommedia.txt*: tiny unstructured file with the full Divina Commedia by Dante Alighieri
- *tweet.dsv*: structured file with 10K public tweets from 2016 about vaccines
- *weather-full.txt*: structured file with weather data from all over the world in year 2000 (collected from the [National Climatic Data Center](ftp://ftp.ncdc.noaa.gov/pub/data/noaa/) of the USA; weights 13GB
  - Sample row: 005733213099999**19580101**03004+51317+028783FM-12+017199999V0203201N00721004501CN0100001N9 **-0021**1-01391102681
  - The date in YYYYMMDD format is located at 0-based position 15-23
  - The temperatue in x10 Celsius degrees is located at 0-based positions 87-92
- *weather-sample10.txt*: a sample of 10% of the data in weather-full.txt; weights 1.3GB
- *weather-sample1.txt*: a sample of 1% of the data in weather-full.txt; weights 130MB
- *weather-stations.csv*: structured file with the description of weather stations collecting the weather data

All these files will continue to be available even after the Virtual Lab is ended.


## 101-3 Working with Spark

There are multiple ways to work with Apache Spark. In this course, we will use the following modalities.

- Local interactive way (notebook) - mainly during our laboratories
- Local deployment (spark-submit) - mainly to develop your project
- Remote deployment with EMR (spark-submit) - mainly to deploy your project

### 101-3.a Local interactive way (notebook)

Inside the folder "src/main/python/lab101" you will find a *local-notebook.ipynb* file; it contains an example of a Spark job using native Scala.

> Note: even though the notebook contains Scala code, it is under src/main/python as autocompletion works better. 

To enable notebooks in IntelliJ, you must perform the following operations.
- Go to File > Project structure > SDKs; here, add a new SDK pointing to the "python.exe" file on your computer.
  - In the Packages tab, make sure that jupyter, findspark, and python-kernel are installed.
- Go to File > Settings > Languages & Frameworks > Jupyter > Jupyter Servers; here, select the ".main" of your project and select the configured Python SDK under "Python intepreter".

Now, you can open the notebook and start running the cells.

In this configuration, the **Spark UI** is automatically launched by the Jupyter server.

### 101-3.b Local/Remote deployment (spark-submit)

Inside the folder "src/main/scala/lab101" you will find an *ExampleJob.scala* file; it contains the same Spark job of 101-3.a, but designed to be run with the ```spark-submit``` command.

The job is designed to easily switch from local to remote deployments. Before running the job, you must do the following.
- (Remote only) Create a cluster on AWS EMR via CLI (see [AWS CLI cheatsheet](aws-cli-cheatsheet.md))
- (Remote only) Make sure that SSH connections are enables on the Securit Group of the master node (must be done only once, but a cluster must be created first) (see [AWS CLI cheatsheet](aws-cli-cheatsheet.md))
- (Remote only) Under src/main/resources, create a file called "aws_credentials.txt"; put the value of your *aws_access_key* in the first line and the value of your *aws_secret_access_key* in the second line (see 101-1.d).
- Open the utils.Config file and update the variables' values according with your settings
- Define new Run/Debug Configurations with the following settings:
  - For local deployment
    - Configuration type: Spark Submit - Local     (deprecated)
    - Name: Spark Local
    - Spark home: should be found automatically
    - Application: point to the .jar file   inside   the build/libs folder of this   repository; if   you don't find it, build the   project
    - Class: lab101.ExampleJob
    - Run arguments: ```local```
    - Cluster manager: Local
    - Master: local
  - For remote deployment (a cluster must be already up-and-running on EMR)
    - Configuration type: Spark Submit - Cluster
    - Name: Spark Cluster
    - Region: us-east-1
    - Remote Target: Add EMR connection
      - Authentication type: Profile from credentials file
      - Profile name: (the one you set in 101-1.d)
      - Click on "Test connection" to verify: if you cannot connect or there are no deployed cluster, the connection will not be saved
    - Enter a new SSH Configuration
      - Host: the address of the primary node of the cluster, i.e., the MasterPublicDnsName (see [AWS CLI cheatsheet]())
      - Username: hadoop
      - Authentication type: Key pair
      - Private key file: point to your .ppk
      - Test the connection
    - Application: point to the .jar file   inside   the build/libs folder of this   repository; if   you don't find it, build the   project
    - Class: lab101.ExampleJob
    - Run arguments: ```remote```
    - Before launch: Upload Files Through SFTP

Build the project and run it, both locally and remotely; check that the output is correctly generated in both cases.

> Note: according to your preferences/habits, you may choose to:
> - Put input and output directories as parameters of the Debug/Run Configuration instead of hard-coding them (though this means changing the configuration every time, or creating many of them)
> - Add a "build" step in the "Before launch" option of the Debug/Run Configuration to avoid the error of running old jars (though you will incur in additional time if jar you want to run didn't need to be rebuilt)



## 101-extra Configuring your own computer

All computers in the laboratories are pre-configured with everything you need. To work on you local computer, follow these instructions to install and configure your enviroment.

### AWS CLI

- Download it from [here](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html) and install it.

### Apache Spark

> IMPORTANT. Spark requires Java 8, 11, or 17.

- Download it from [here](https://archive.apache.org/dist/spark/spark-3.5.1/spark-3.5.1-bin-hadoop3.tgz).
- Unzip and put it in c:/ (or any other location; in the following, I will assume it is unzipped in c:/)
- Add the following environment variables (assuming JAVA_HOME already exists)
  - HADOOP_HOME = c:/spark-3.5.1-bin-hadoop3
  - SPARK_HOME = c:/spark-3.5.1-bin-hadoop3
  - PATH = PATH + c:/spark-3.5.1-bin-hadoop3/bin
- In Spark's folder, add an empty ```history``` folder, then edit the file **conf/spark-default.conf** to add the following parameters
  ```
  spark.eventLog.enabled           true
  spark.history.fs.logDirectory    file:///c:/spark-3.5.1-bin-hadoop3/history/
  spark.eventLog.dir               file:///c:/spark-3.5.1-bin-hadoop3/history/
  ```
- Download the following files and put them in the **bin** folder inside Spark's folder
    - [Winutils.exe](https://github.com/steveloughran/winutils/raw/master/hadoop-3.0.0/bin/winutils.exe)
	- [hadoop.dll](https://github.com/steveloughran/winutils/raw/master/hadoop-3.0.0/bin/hadoop.dll) (warning: your browser may block this download)
- Open a Git Bash on Spark's folder and launch this command: ```bin/spark-class.cmd org.apache.spark.deploy.history.HistoryServer```
    - Verify that Spark's UI shows up at [http://localhost:18080](http://localhost:18080)

### Python

To run local notebooks, you need a local Python installation, up to 3.11, with the following requirements.

```
pip install findspark
pip install jupyter
pip install spylon-kernel
python -m spylon_kernel install
```

### IntelliJ IDEA Ultimate

- Download it from [here](https://www.jetbrains.com/idea/download)
- Redeem a free educational license [here](https://www.jetbrains.com/community/education/#students)
- Install the following plugins (File > Settings > Plugins)
    - Gradle
    - Scala
    - Python
    - Spark
    - Jupyter 
        - You must download it from [here](https://plugins.jetbrains.com/plugin/22814-jupyter/versions/stable) in a compatible version with your IntelliJ version, then choose "Install plugin from disk" to install it