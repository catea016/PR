# First Laboratory work at Network Programming
## Task description: 
 1. Pull a docker container (alexburlacu/pr-server) from the registry
 2. Run it, don't forget to forward the port 5000 to the port that you want on the local machine
 3. Only languages and libraries supporting threads, locks, and semaphores are allowed. Node or JS generally and Elixir/Erlang are prohibited. Go and Rust are allowed but with some constraints. Discuss with me the implementation.
 4. Now that you're up and running, you need to access the root route of the server and find your way to /register
 5. The access token that you get after accessing the /register route must be put in a HTTP header of the subsequent requests under the key X-Access-Token key
 6. Most routes return a JSON with data, data type, and link keys. Extract data from data key and get next links from link key
 7. Hardcoding the routes is strictly forbidden. You need to "traverse" the API
 8. Access token has a timeout of 20 seconds, and you are not allowed to get another token every time you access a different route. So, one register per program run
 9. Once you fetch all the data, convert it to a common representation, doesn't matter what this representation is
 10. The final part of the lab is to make a concurrent TCP server, serving the fetched content, that will respond to (mandatory) a column selector message, like `SelectColumn column_name`, and (optional) `SelectFromColumn column_name glob_pattern`

## How to run the program

 - docker pull alexburlacu/pr-server
 - docker run -d -p 5000:5000 f8b51d5daf6f
 - run the Main class and after the "Connect to the server on the port 5005" message appear you need to run the Client class