# supersimplestocks [![Build Status](https://travis-ci.org/oche-jay/supersimplestocks.svg?branch=master)](https://travis-ci.org/oche-jay/simplemessagingapp)

Super Simple Stocks application is a Web Server with a REST API that accepts stock trades as JSON messages and performs
some calculations on them.

### Build Instructions
To test and build this application:

    git clone https://github.com/oche-jay/supersimplestocks && cd simplemessagingapp
    mvn test exec:java
    
### Messaging Interface
 The API accepts instructions and parameters in the form of HTTP POST and GET messages.
  
 ### Testing
A suite of data-driven unit tests can be found in  [src/test/java](/src/test/java/) folder.

 ### Acceptance Criteria                             

  - [x] Given a market price as input, calculate the dividend yield for a given stock  
        
        API call: /dividend-yield/{stockName}/{inputPrice}
        
  Sample Requests: 
  
    curl localhost:8000/dividend-yield/TEA/50 
         {
             "stock": "TEA",
             "marketPrice": 50.25,
             "dividendYield": 0,
             "type": "COMMON"
         }
 
    curl localhost:8000/dividend-yield/GIN/50  
         {
             "stock": "GIN",
             "marketPrice": 50.25,
             "dividendYield": 0.0398,
             "type": "PREFERRED"
         }
             
     curl localhost:8000/dividend-yield/ZZZ/50 (unkown stock)
         {
             "message": "NoSuchElementException: No value present",
             "httpStatus": 500
         }

  - [x]  Given a market price as input, calculate the Price-to-Earning Ratio for a given stock  
         
         API call: /pe-ratio/{stockName}/{inputPrice}
                   
     Sample Requests and Responses:  
           
           curl localhost:8000/pe-ratio/POP/10.05  
           {
              "stock": "POP",
              "marketPrice": 10.05,
              "peRatio": 1.2563
           }
            
           curl localhost:8000/pe-ratio/GIN/5    
           {
               "stock": "GIN",
               "marketPrice": 5,
               "peRatio": 0.625
           }          
            
           curl localhost:8000/pe-ratio/XXX/33 #Unknown Stock
            {
                "message": "NoSuchElementException: No value present",
                "httpStatus": 500
            }
            
  - [x] Record a trade, with timestamp, quantity of shares, buy or sell indicator and trade price For a given stock
  

    This has been implemented as a HTTP POST message. The server performs a quick validation on all incoming 
    JSON payloads (quantity must not be empty or less than 1, price cannot be negative, indicator 
    must be BUY or SELL). The server adds the timestamp to the payload on the serverside, and records the trade
    in an internal datastructure.
    
    Sample Requests and Responses: 
    Buy Stock: 
    
        curl -H "Content-Type: application/json" -X POST -d ' 
        {
                "stock": "TEA",
                "quantity": 7,
                "price": "2.5",
                "indicator": "BUY"
        }' http://localhost:8000/trade
 
    Response:
    
        {
            "message": "Successfully recorded trade",
            "httpStatus": 200
        }

    Sell Stock:
    
        curl -H "Content-Type: application/json" -X POST -d ' 
        {
                "stock": "TEA",
                "quantity": 7,
                "price": "2.5",
                "indicator": "SELL"
        }' http://localhost:8000/trade
   
    Response:
     
        {
            "message": "Successfully recorded trade",
            "httpStatus": 200
        }
          
    Invalid Trade (unknown Indicator)
    
        curl -H "Content-Type: application/json" -X POST -d ' 
        {
                "stock": "TEA",
                "quantity": 7,
                "price": "2.5",
                "indicator": "BUX"
        }' http://localhost:8000/trade
                
    Response:
    
       {
           "message": "JsonParseException: required field missing from sent JSON message: indicator",
           "httpStatus": 400
       }
         
 
                
  - [x] Calculate Volume Weighted Stock Price based on trades in past 15 minutes For a given stock
        
        curl localhost:8000/volume-weighted-stockprice/TEA
        #Response:
        {
            "stock": "TEA",
            "volumeWeightedStockPrice": 2.5
        }
  
  
  - [x] Calculate the GBCE All Share Index using the geometric mean of prices for all stocks
        
        curl localhost:8000/all-share-index
        #Response:
        {
            "allShareIndex": 2.5089
        }
        
        
