/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author carolina
 */
public class ClienteTCPparaWeb {

    private Socket conexion;
    private OutputStreamWriter salida;
    private BufferedReader entrada;
    private String direccion;
    private int puerto;
    public ArrayList<String> lista;
    private InputStream entrar;
    private int bytes = 0;

    private ClienteTCPparaWeb() {
    }

    public ClienteTCPparaWeb(String direccion, int puerto) {
        this.direccion = direccion;
        this.puerto = puerto;
    }

    public void conect() throws IOException {
       conexion = new Socket(direccion, puerto);
       conexion.setSoTimeout(18000);
    }

    public void mandar(String mensaje) throws IOException {
        OutputStream canal = conexion.getOutputStream();
        this.salida = new OutputStreamWriter(canal);
        salida.write(mensaje);
        salida.flush();
    }

    public String recibir() throws IOException, ClassNotFoundException {
        entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
        String contenedor = new String();
        contenedor = entrada.readLine();
        String parte = new String();
        while (parte != null) {
            parte = entrada.readLine();
            contenedor = contenedor + "\n" + parte;
        }
        return contenedor;
    }

    public byte[] recibirIm() throws IOException {
        entrar = conexion.getInputStream();
        byte[] mensaje = new byte[4096];
        bytes = entrar.read(mensaje);
        return mensaje;
    }

    public int getNunBytes() {
        return bytes;
    }

    public void close() throws IOException {
        conexion.close();
    }
}