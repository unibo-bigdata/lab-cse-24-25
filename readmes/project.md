# Exam project

Big Data course (81932), University of Bologna.

As of A.Y. 2024/25, the project must be developed locally and deployed on your AWS Virtual Lab.

Last update: 01/12/2024.

## Instructions

- Find a suitable dataset
    - One or more files/tables/collections of data
    - Minimum 5GB of data
- Notify the teacher about the dataset and the main job that you mean to carry out
    - The execution plan of the main job must include **at least 2 shuffles**; some sample patterns are shown below
- Load the dataset on your S3 bucket
    - Include to be used for developing/debugging purposes
- When developing/debugging
    - Understand the dataset!
    - Use a sample of the dataset for debugging
    - Use a notebook to implement two versions of the agreed-upon job: a non-optimized one and an optimized one
- When deploying
    - Test both versions of the job and download the corresponding histories 
    - Verify and understand the results (e.g., using Power BI, Tableau, Matplotlib, whichever you prefer)
    - **Important**: the code in the notebook should be optimized for its execution as if it was a production job; for instance:
        - Do not cache when not needed
        - Do not collect unnecessarily
- To deliver the project, notify the teacher via email and send a ZIP file with
    - The project, including both the notebook (with all the cells executed) and the application
    - The sample of the dataset
    - The history of the executed jobs
    - A link to directly download the full dataset
    - Any additional material (e.g., Power BI or Tableau file)
- Evaluation
    - 0 for sufficient projects
    - 1 or 2 points (to be added to the vote of the oral exam) based on technical complexity, performance evaluation, and creativity


## Datasets

Go on [Kaggle](https://www.kaggle.com/datasets?fileType=csv&sizeStart=5%2CGB) and look for CSV datasets with minimum 5GB of data. Many of them contain multimedia files (e.g., audio tracks, images), so you need to search through the pages for valid textual datasets. Some examples:

- [US Stock Dataset](https://www.kaggle.com/datasets/footballjoe789/us-stock-dataset?select=Data)
- [Ethereum Blockchain](https://www.kaggle.com/datasets/buryhuang/ethereum-blockchain)
- [COVID-19 Complete Twitter Dataset](https://www.kaggle.com/datasets/imoore/covid19-complete-twitter-dataset-daily-updates)
- [Official Reddit r/place Dataset CSV](https://www.kaggle.com/datasets/antoinecarpentier/redditrplacecsv)
- [Simulated Transactions](https://www.kaggle.com/datasets/conorsully1/simulated-transactions)

Alternative examples:

- [Amazon Reviews](https://s3.amazonaws.com/amazon-reviews-pds/readme.html)
- [NYC Taxi trips](https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page)
- [List 1](https://www.quora.com/Where-can-I-find-large-datasets-open-to-the-public)
- [List 2](https://hadoopilluminated.com/hadoop_illuminated/Public_Bigdata_Sets.html)
- [BigDataBench (datasets + generator)](https://www.benchcouncil.org/BigDataBench/download.html#DownloadGenerator)
- [Other benchmarks](https://www.predictiveanalyticstoday.com/bigdata-benchmark-suites/)

If you find some broken links, please notify the teacher. Thanks.

**IMPORTANT**: if you find a dataset with multiple files, jobs are usually better (you can do joins and you have more data to evaluate/aggregate).
If you can't find a dataset like that, consider adding one or more synthetic files by generate fake data (LLMs could help you on that).  

## Main job patterns

Here are some typical job patterns to get an execution plan with at least 2 shuffles. Consider the MovieLens dataset used in Lab 104.

- *Join and aggregate*: this is trivial if your dataset is composed by multiple files/tables; for instance:
    - Join movies with ratings
    - Aggregate to calculate the average rating of each genre
- *Double aggregation*: do a first pre-aggregation on two (or more) attributes, then further aggregating on a subset of those attributes; for instance:
    - Aggregate ratings by movieid and year, to get the average rating of each movie in each year
    - Aggregate by year to calculate the average of the average ratings for each year (which could be interpreted as the "average quality" of the movies seen every year)
- *Self-join*: do a first aggregation on one attribute, then join the result with the original dataset and re-aggregate on a different attribute; for instance:
    - Aggregate by movieid to calculate the average rating and the number of ratings for each movie, then calculate a "Class" attribute as follows
        - If the count is <10, classify the movie as "Insufficient ratings"
        - If the count is >=10 and the average rating is <=2, classify the movie as "Bad"
        - If the count is >=10 and the average rating is >2 and <=4, classify the movie as "Average"
        - If the count is >=10 and the average rating is >4, classify the movie as "Good"
    - Join the result with the ratings on the movieid
    - Aggregate by class and year to see how many ratings are give each year on the different classes of movies