# scout
![](src/main/resources/scout-banner.jpg)
a discord bot that tracks prices changes and product availability. \
for rutgers students, it also tracks course availability :D

### server
the bot is still in progress... when it's ready you can find the link here

## features
### sniper
scout parses information from a product page or a public api to determine stock availability. when a requested product is determined to be 
available, the bot will send a notification to the user's dms. currently, this bot supports bestbuy and gamestop links

### course sniper
for rutgers students, scout can determine section availability of courses offered at rutgers. simply provide the section index and scout
will notify you when that section opens up.

### tracker
***WIP*** scout tracks price information on a certain product by comparing its current price to a previously saved price. the 
user can choose to specify how much a price should change prior to being notified.

## how does scout work?
### bot logistics
scout uses the jda library, a java wrapper for the discord REST api. 

### parsing data 
scout parses information about a product in two ways:
1. directly from the product webpage
2. creating a request to the product's website's api

### notifying users
checks for availability or a price change for each sniper/tracker are all done asynchronously to keep things fast. when a change in availability 
or stock is detected a notification will be sent to a user's private dms.

### deployment
this bot is deployed on my home server with docker, composed with a mysql database.

## questions/issues
scout is always being improved upon. if you have any questions or comments, message me on discord `@mvte#9597`
