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

# RAG

## Set Context

### Request (will use default resources/RAG.txt)

````
POST http://localhost:8080/api/rag/context
{}
````

### Response
````
HttpStatus 201

Success
````

## Questions

### Request
````
POST http://localhost:8080/api/rag/question
{
    "input" : "What is RAG?",
    "cleanHistory": false
}
````

### Response
````
RAG is a hybrid framework that combines retrieval-based and generative methods to answer queries. It retrieves relevant information from a dataset and generates context-aware responses.
````

### Request
````
POST http://localhost:8080/api/rag/question
{
    "input" : "How is RAG implemented?",
    "cleanHistory": false
}
````

### Response
````
RAG can be implemented using tools like vector search engines for retrieval and large language models for generation. Popular frameworks for implementing RAG include OpenAI's GPT and Hugging Face Transformers.
````

### Request
````
POST http://localhost:8080/api/rag/question
{
    "input" : "What are the use cases of RAG",
    "cleanHistory": false
}
````

### Response
````
Common use cases for RAG include improving chatbot interactions, summarizing large documents, and providing intelligent search capabilities for enterprise systems.
````

### Request
````
POST http://localhost:8080/api/rag/question
{
    "input" : "What is the LLM?",
    "cleanHistory": false
}
````

### Response
````
No Data provided
````


