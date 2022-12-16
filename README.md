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

### sniper: parsing data 
a snipe's type is determined by the website of the product specified by the user. scout will automatically determine if a given link is a supported
`URLType`, and creates its corresponding `Snipe` object with the `SnipeFactory` class. any class extending `Snipe` must implement two things:
1. how to parse the item's name
2. how to determine if the item is in stock

### modeling and persistence
a user is represented by a `UserModel`, simply containing the user's id and their list of snipe/tracking requests. the `UserModelDatabase` uses a
hash map to store all of the users that are registered. a user is registered when they send a message anywhere a bot can see.
scout uses java's `Serializable` interface to write and read objects to file. this (will) include the `UserModelDatabase` and `RutgersCourseDatabase`. \
*why don't i use a database?* idk how yet lol

## questions/issues
scout is always being improved upon. if you have any questions or comments, message me on discord `@mvte#9597`
