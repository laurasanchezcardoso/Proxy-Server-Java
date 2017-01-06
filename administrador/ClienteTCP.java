/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package administrador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author carolina
 */
public class ClienteTCP {

    private Socket conexion;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String direccion;
    private int puerto;

    private ClienteTCP() {
    }

    public ClienteTCP(String direccion, int puerto) {
        this.direccion = direccion;
        this.puerto = puerto;
    }

    public void conect() throws IOException {
        this.conexion = new Socket(direccion, puerto);
    }

    public void mandar(Object mensaje) throws IOException {
        OutputStream canal = conexion.getOutputStream();
        salida = new ObjectOutputStream(canal);
        salida.writeObject(mensaje);
        salida.flush();
    }

    public Object recibir() throws IOException, ClassNotFoundException {
        entrada = new ObjectInputStream(conexion.getInputStream());
        return entrada.readObject();
     
    }

    public void close() throws IOException {
        conexion.close();
    }
}

