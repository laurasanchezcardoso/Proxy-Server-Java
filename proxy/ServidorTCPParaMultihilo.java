/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author carolina
 */
public class ServidorTCPParaMultihilo {

    private Socket conexion;
    private OutputStreamWriter salida;
    private BufferedReader entrada;
    private ArrayList<String> contenido = new ArrayList();
    private ArrayList<String> pedido = new ArrayList();
    private ObjectOutputStream salir;

    private ServidorTCPParaMultihilo() {
    }

    public ServidorTCPParaMultihilo(Socket conexion) {
        this.conexion = conexion;
    }

    public void mandar(String mensaje) throws IOException {
        OutputStream canal = this.conexion.getOutputStream();
        this.salida = new OutputStreamWriter(canal);
        salida.write(mensaje);
        salida.flush();
    }

    public void mandarIm(byte[] mensaje, int length) throws IOException {
        DataOutputStream canal = new DataOutputStream(this.conexion.getOutputStream());
        canal.write(mensaje,0,length);
        canal.flush();
    }

    public ArrayList<String> recibir() throws IOException, ClassNotFoundException {
        entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
        String lineaRecibida = entrada.readLine();
        pedido.add(lineaRecibida);
        int contador;
        for (contador = 0; contador <= 7; contador++) {
            String alfa = entrada.readLine();
            if (alfa != null) {
                pedido.add(alfa);
            }

        }


        String[] partes = lineaRecibida.split(" ");/*separo la linea para extraer la direccion*/
        if ("GET".equals(partes[0])) {
            String[] palabras = partes[1].replace("/", " ").split(" ");/*aca me quedo desde www a com*/
            contenido.add(partes[0]);/*guardo el tipo de operacion*/
            String[] pURL = palabras[2].replace(".", " ").split(" ");/*extraigo las palabras de la direccion*/
            int i = 0;
            for (i = 0; i < pURL.length; i++) {
                contenido.add(pURL[i]);/*guardo las palabras en el ArrayList para devolver y comparar*/
            }
            int j;/*puede que despues de com haya una barra y el nombre del recurso
             tambien hay que controlar eso*/
            if (palabras.length > 3) {
                for (j = 3; j < palabras.length; j++) {
                    contenido.add(palabras[j]);

                }
            }


        } else if ("POST".equals(partes[0])) {/*identico mecanismo que para el get*/
            String[] palabras = partes[1].replace("/", " ").split(" ");
            contenido.add(partes[0]);
            String[] pURL = palabras[2].replace(".", " ").split(" ");
            int i = 0;
            for (i = 0; i < pURL.length; i++) {
                contenido.add(pURL[i]);
            }
            int j;/*puede que despues de com haya una barra y el nombre del recurso
             tambien hay que controlar eso*/
            if (palabras.length > 3) {
                for (j = 3; j < palabras.length; j++) {
                    contenido.add(palabras[j]);

                }
            }
        } else {/*mecanismo para los otros metodos de http*/
            contenido.add(partes[0]);
            String[] pURL = partes[1].replace(".", " ").split(" ");
            int i = 0;
            for (i = 0; i < pURL.length; i++) {
                contenido.add(pURL[i]);
            }

        }
        return this.contenido;
    }

    public ArrayList pedido() {
        return pedido;
    }

    public void Close() throws IOException {
        conexion.close();
    }
}
