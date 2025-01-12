# Traffic lights plugin

### Request
````
{
"input": "Please return the satet of traffic lights"   
}
````

### Response
````
The current state of the traffic lights is as follows:
- Red light: On
- Green light: Off
- Yellow light: Off
````

### Request
````
{
    "input": "Please switch off red light"   
}
````

### Response
````
The red light has been switched off.
````

### Request
````
{
    "input": "Please switch on yellow light"   
}
````

### Response
````
The yellow light has been switched on.
````

### Request
````
{
    "input": "Please return the satet of traffic lights"   
}
````

### Response
````
The current state of the traffic lights is as follows:
- Red light: Off
- Green light: Off
- Yellow light: On
````

# Currency converter plugin

### Request
````
{
    "input": "Please convert 100 UAH to USD"   
}
````

### Response
````
100 UAH is equivalent to 2.4 USD.
````

### Request
````
{
    "input": "Please convert 100 EUR to USD"   
}
````
### Response
````
100 EUR is equivalent to 105.0 USD.
````

# Vector search

## Save embeddings

### Request
````
http://localhost:8080/api/embeddings/save
{"text": "Best pizza places nearby."}
````

### Response
````
201 Created
````

### Request
````
http://localhost:8080/api/embeddings/save
{"text": "Popular breakfast spots in New York."}
````

### Response
````
201 Created
````

### Request
````
http://localhost:8080/api/embeddings/save
{"text": "Top-rated seafood restaurants in Miami."}
````

### Response
````
201 Created
````
### Request
````
http://localhost:8080/api/embeddings/save
{"text": "Cheap hotels near the beach."}
````

### Response
````
201 Created
````

### Request
````
http://localhost:8080/api/embeddings/save
{"text": "Recipes for quick pasta dishes."}
````

### Response
````
201 Created
````

### Search Request
````
http://localhost:8080/api/embeddings/search
{"input": "pasta"}
````

### Search Response
````
[
    {
        "id": 0,
        "version": 17,
        "score": 0.88188004,
        "payload": {
            "info": "{\"text\": \"Recipes for quick pasta dishes.\"}"
        }
    }
]
````
