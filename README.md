# About
This is basic task with enriching data 
from .csv file without any optional enhancements.
## Requirements
- JDK17 
- Docker and docker-compose

## How to start the app
1. Clone the repo
2. open repo in your IDE
3. start Redis container from GUI or type `docker-compose up -d`.
After launch container will have data from 
[products.csv](products.csv) file. Container is running on 6379th port. 
check if it's not used or
change port in [docker-compose.yml](docker-compose.yml)
before executing command.
4. Start the app from IDE or build .jar file and execute it
    app do not require any arguments on start.
# Api
## `/api/enrich`
accepts .csv file and return enriched data in .csv format.

#### example call 
`curl -F "file=@src/test/resources/trades.csv" http://localhost:8080/api/enrich`
#### example response
`20230108,Government Bonds Domestic,GBP,1200.2` <br>
`20230109,Convertible Bonds Domestic,USD,1300.3`<br>
`20230109,Corporate Bonds International,EUR,1400.4`<br>
`20230110,Interest Rate Futures,GBP,1500.5`<br>
`20131231,Inflation-Linked Bonds,USD,1600.6`<br>
`20230111,Convertible Bonds Domestic,GBP,1800.8`<br>


# Performance
## Redis initialization
When docker-compose starts container it mounts 
[init-cache.sh](init-cache.sh) bash script and 
[products.csv](products.csv) into the container.
After launching redis, script starts. 
It waits few seconds to ensure Redis started 
and then start inserting data. 
To ensure speed of insertion script adds batches of Products 1k each. 
This way we ensure quick start of the container with data.

For comparison, insertion one by one without batching 
all data from the given .csv file 
(around 80k key-value pairs) took around 3 minutes.
With batching all process take less than 10 seconds. 


