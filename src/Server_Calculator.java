import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basé sur l'exemple du server reçu pour l'utilisation de docker.
 * Cette application sera le serveur pour le laboratoire Exercise-Calculator
 * Il suivra la norme proposée par Victor Truan.
 *
 * @author Victor Truan
 */
public class Server_Calculator {

    static final Logger LOG = Logger.getLogger(Server_Calculator.class.getName());
    InputStream is;

    /**
     * This method does the entire processing.
     */
    public void start() {
        System.out.println("Starting server...");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        Operation op;

        try {
            LOG.log(Level.INFO, "Creating a server socket and binding it on any of the available network interfaces and on port {0}", new Object[]{Integer.toString(Protocol.DEFAULT_PORT)});
            serverSocket = new ServerSocket(Protocol.DEFAULT_PORT, 50);
            logServerSocketAddress(serverSocket);

            do {
                LOG.log(Level.INFO, "Le serveur attends une connexion au port " + Protocol.DEFAULT_PORT, new Object[]{serverSocket.getInetAddress(), Integer.toString(serverSocket.getLocalPort())});
                clientSocket = serverSocket.accept();

                LOG.log(Level.INFO, "Un client est connecté");
                logSocketAddress(clientSocket);
                LOG.log(Level.INFO, "Mise en place du Reader et Writer.");
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                is = clientSocket.getInputStream();

                writer = new PrintWriter(clientSocket.getOutputStream());
                LOG.log(Level.INFO, "Reader et Writer en place");

                do {
                    String tmp;
                    try {
                        while (!reader.ready()) {
                        }
                    } catch (Exception e) {
                        System.out.println("Bad");
                    }
                    tmp = reader.readLine();

                    String[] buff = tmp.split(Protocol.DELIM);

                    switch (buff[0]) {
                        case Protocol.INIT:
                            writer.println(Protocol.ASK_NUMBER + Protocol.DELIM + "\n");
                            writer.flush();
                            LOG.log(Level.INFO, "Ask Number to client.");
                            break;
                        case Protocol.SEND_NUMBER:
                            if (buff.length != 4) {
                                writer.println("Pas le bon nombre d'argument pour " + buff[0] + ", 4 attendu et " + buff.length + " reçu.");
                                return; //Permet de forcer le passage par finally.
                            } else {
                                op = new Operation(Double.valueOf(buff[1]), Double.valueOf(buff[2]), buff[3]);
                                op.compute();
                            }
                            if (op.getResult() != null) {
                                writer.println(Protocol.RESULT + Protocol.DELIM + op.getResult());
                                writer.flush();
                            } else {
                                LOG.log(Level.INFO, "Opération " + buff[3] + " envoyé au serveur et non reconnue.");
                                writer.println("Opération " + buff[3] + " non reconnue, fermeture de la connexion");
                                return;
                            }
                            LOG.log(Level.INFO, "Résultats envoyé au serveur.");
                            break;
                        case "Cancel":
                            LOG.log(Level.INFO, "Connexion fermée par le client.");
                        default:
                            LOG.log(Level.INFO, "Connexion fermée abruptement");
                            return;
                    }
                } while (clientSocket.isConnected());
            } while (true);
        } catch (
                IOException ex) {
            LOG.log(Level.INFO, "Reader4: ");

            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            LOG.log(Level.INFO, "Fermeture de la conexion.");
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Server_Calculator.class.getName()).log(Level.SEVERE, null, ex);
            }
            writer.close();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Server_Calculator.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Server_Calculator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * A utility method to print server socket information
     *
     * @param serverSocket the socket that we want to log
     */
    private void logServerSocketAddress(ServerSocket serverSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{serverSocket.getLocalSocketAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(serverSocket.getLocalPort())});
        LOG.log(Level.INFO, "               is bound: {0}", new Object[]{serverSocket.isBound()});
    }

    /**
     * A utility method to print socket information
     *
     * @param clientSocket the socket that we want to log
     */
    private void logSocketAddress(Socket clientSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(clientSocket.getLocalPort())});
        LOG.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
        LOG.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        Server_Calculator server = new Server_Calculator();
        server.start();
    }

}