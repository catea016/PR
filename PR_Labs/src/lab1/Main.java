package lab1;


public class Main {
    private static final String homeURL = "http://localhost:5000/home";
    private static final int port = 5005;

    public static void main(String[] args) {
        DataReader dataReader = new DataReader();
        dataReader.getAccessToken();
        Request request = new Request(homeURL);
        request.start();
        while (true) {
            if (request.threads == 0) {
                System.out.println( "\nConnect to the server on the port " + port);
                Server tcpServer = new Server();
                tcpServer.run(port);
                break;
            }
        }

    }
}
