import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket = null;
    private BufferedReader is = null;
    private OutputStream os = null;

    public Client(String address) {
        String SERVER_ADDRESS = address;
        try {
            clientSocket = new Socket(SERVER_ADDRESS, Protocol.DEFAULT_PORT);
            os = clientSocket.getOutputStream();
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            init();

            String line;
            while ((line = is.readLine()) != null) {
                if (line.equals(Protocol.ASK_NUMBER + Protocol.DELIM)) {
                    break;
                }
            }

            Scanner inputUser = new Scanner(System.in);
            Scanner inputUser2 = new Scanner(System.in);
            Scanner inputUser3 = new Scanner(System.in);

            System.out.println("Entrez la première opérande :");
            Double operand1 = inputUser.nextDouble();

            System.out.println("Entrez l'opération (+,-,*,/):");
            String operation = inputUser3.nextLine();

            System.out.println("Entrez la seconde opérande :");
            Double operand2 = inputUser2.nextDouble();

            String request = craftingOperation(operand1, operand2, operation);
            os.write(request.getBytes());

            String line2;
            while ((line2 = is.readLine()) != null) {
                if (line2.contains(Protocol.RESULT + Protocol.DELIM)) {
                    System.out.println(line2);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String craftingOperation(Double operand1, Double operand2, String operation) {
        StringBuilder request = new StringBuilder(Protocol.SEND_NUMBER);
        request.append(Protocol.DELIM + operand1 + Protocol.DELIM + operand2 + Protocol.DELIM + operation + "\n");
        System.out.println(request);
        return request.toString();
    }

    private void init() {
        try {
            String test = Protocol.INIT + Protocol.DELIM + "\n";
            os.write(test.getBytes());
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
