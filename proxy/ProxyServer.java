package proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author carolina
 */
public class ProxyServer {

    public ProxyServer() {
    }
    /**
     * @param args the command line arguments
     */

    /*
     * ------------------------------------main-------------------------------------
     */
    public static void main(String[] args) throws IOException {
        ProxyServer proxy = new ProxyServer();
        int puerto;
        String direccion = null;
        if (args.length == 0) {
            puerto = 5555;
        } else if (args.length == 1) {
            direccion = args[0];
            puerto = 5555;
        } else {
            direccion = args[0];
            puerto = Integer.parseInt(args[1]);
        }
        HiloAdm Administrador = new HiloAdm();
        Administrador.start();
        ServerSocket ss = new ServerSocket(puerto);
        while (true) {
            Socket s = ss.accept();
            HiloNav navega = new HiloNav(s);
            navega.start();

        }
    }
}