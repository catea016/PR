# **First laboratory work for Network Programming**
### Tasks:
1. We had to choose a programming language that we will use for that laboratory, so I chose Java.
2. Then we had to access the root route of the server and find the way to register.
3. The access token that we get after accessing the register route must be put in http header 
   of subsequent requests under the key X-Access-Token key.
4. Most routes return a json with data and link keys. We had to extract data from data key 
   and get next links from link key.
5. Access token has a timeout of 20 seconds, and we are not allowed to get another token 
   every time you access different route. So, one register per program must run.
6. Once we fetch all the data, we had to convert it to a common representation, so I converted in a json.
7. The final part of the lab is to make a concurrent TCP server, serving the fetched content, 
   that will respond to (mandatory) a column selector message, like SelectColumn column_name.
   
### How to run:
1. docker pull alexburlacu/pr-server
2. docker run -p5000:5000 alexburlacu/pr-server
3. In order to run the program I have the class MultithreadingRequest that have the main method and need to run it.
   (this class is used to run the TCP server)
4. In order to access the server and to get the respond we have to enter in telnet port through command line
   using the command: >> telnet localhost 5005