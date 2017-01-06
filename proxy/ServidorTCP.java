
package proxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author carolina
 */
public class ServidorTCP {

    private ServerSocket servidor;
    private Socket conexion;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private int puerto;

    private ServidorTCP() {
    }

    public ServidorTCP(int puerto) {
        this.puerto = puerto;
    }

    public void conexion() throws IOException {
        servidor = new ServerSocket(puerto);
        conexion = servidor.accept();
    }

    public void mandar(Object mensaje) throws IOException {
      OutputStream canal = this.conexion.getOutputStream();  
	this.salida = new ObjectOutputStream(canal);
        salida.writeObject(mensaje);
        salida.flush();
    }

    public Object recibir() throws IOException, ClassNotFoundException {
        entrada = new ObjectInputStream(conexion.getInputStream());
        Object aux = entrada.readObject();
        return aux;
    }

    public void Close() throws IOException {
        conexion.close();
        servidor.close();
    }
}

