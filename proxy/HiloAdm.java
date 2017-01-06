/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.io.IOException;
import java.util.ArrayList;

/*
 * --------------------------Atributos de la clase---------------------------
 */
/**
 *
 * @author udelar
 */
public class HiloAdm extends Thread {

    private static ArrayList<String> ListaPalabra = null;
    private static ArrayList<String> ListaURL = null;
    private boolean hola = true;
    public ServidorTCP servidorAdm;
    private String[] Lineas;
    private String Palabra;
    private static boolean Get = true;
    private static boolean Post = true;
    private int indic;
    /*
     * ---------------------constructor---------------------------------------------
     */

    public HiloAdm() {
        ListaPalabra = new ArrayList<String>();
        ListaURL = new ArrayList<String>();
    }
    /*----------------------------------------------------metodos-----------------------*/

    public int analizar(Object comando) throws IOException {

        Lineas = ((String) comando).split(" ");
        indic = 0;
        if ("addDUW".equals(Lineas[0]) || "addDW".equals(Lineas[0])
                || "deleteDUW".equals(Lineas[0]) || "deleteDW".equals(Lineas[0])) {
            indic = 1;
        } else if ("listDUW".equals(Lineas[0]) || "listDW".equals(Lineas[0])) {
            indic = 2;
        } else if ("allowPOST".equals(Lineas[0]) || "allowGET".equals(Lineas[0])
                || "denyPOST".equals(Lineas[0]) || "denyGET".equals(Lineas[0])) {
            indic = 3;
        } else if ("GET".equals(Lineas[0]) || "POST".equals(Lineas[0])) {
            indic = 4;
        } else {
            indic = 5;
        }

        return indic;
    }

    public void EnviarListas(ServidorTCP servidorAdm) throws IOException {
        if ("listDUW".equals(Lineas[0])) {
            enviarLista(servidorAdm, ListaURL);
        } else if ("listDW".equals(Lineas[0])) {
            enviarLista(servidorAdm, ListaPalabra);
        }
    }

    /*
     * el metodo enviarLista envia una lista vacia si la lista esta vacia y la lista si
     * hay elementos en ella"
     */
    public void enviarLista(ServidorTCP servidorAdm, ArrayList lista)
            throws IOException {
        if (lista.isEmpty()) {
            ArrayList Vacio = new ArrayList();
            servidorAdm.mandar(Vacio);
        } else {
            servidorAdm.mandar(lista);
        }

    }
    /*nos fijamos el estado de la bandera y retornamos un valor en consecuencia*/

    public static int AllowODeny(boolean bandera) {
        if (bandera == true) {
            return 1;
        } else {
            return 2;
        }
    }
    /*se envian las banderas*/

    public void MandarBanderas(ServidorTCP servidorAdm) {
        if ("GET".equals(Lineas[0])) {
            if (AllowODeny(this.isGet()) == 1) {
                try {
                    servidorAdm.mandar("allow");
                } catch (IOException ex) {
                }
            } else {
                try {
                    servidorAdm.mandar("deny");
                } catch (IOException ex) {
                }
            }
        } else {
            if (AllowODeny(this.isPost()) == 1) {
                try {
                    servidorAdm.mandar("allow");
                } catch (IOException ex) {
                }
            } else {
                try {
                    servidorAdm.mandar("deny");
                } catch (IOException ex) {
                }
            }
        }
    }
    /*getter de el estado del get*/

    public synchronized static boolean isGet() {
        return Get;
    }
    /*getters de las listas*/

    public synchronized static ArrayList<String> getListaPalabra() {
        return ListaPalabra;
    }

    public synchronized static ArrayList<String> getListaURL() {
        return ListaURL;
    }
    /*getter del post*/

    public synchronized static boolean isPost() {
        return Post;
    }
    /*ManipularLista se encarga de llamar a los metodos agregar o borrar
     segun sea la lista y el caso*/

    public void ManipularLista() {
        Palabra = Lineas[1];
        if ("addDUW".equals(Lineas[0])) {
            this.AgregarPalabra(ListaURL);

        } else if ("addDW".equals(Lineas[0])) {
            this.AgregarPalabra(ListaPalabra);

        } else if ("deleteDUW".equals(Lineas[0])) {
            this.BorrarPalabra(ListaURL);
        } else if ("deleteDW".equals(Lineas[0])) {
            this.BorrarPalabra(ListaPalabra);
        }
    }
    /*
     * ------------------Metodos Agregar y Borrar-------------------------------
     */

    public synchronized void AgregarPalabra(ArrayList<String> lista) {
        lista.add(Palabra);
    }

    public synchronized void BorrarPalabra(ArrayList lista) {
        int i = Integer.parseInt(Palabra);
        lista.remove(i);

    }
    /*cambia el estado de las banderas segun el comando que ha llegado*/

    public synchronized void Banderas() {
        if ("denyPOST".equals(Lineas[0])) {
            this.Post = false;
        } else if ("allowPOST".equals(Lineas[0])) {
            this.Post = true;
        } else if ("denyGET".equals(Lineas[0])) {
            this.Get = false;
        } else {
            this.Get = true;
        }
    }

    /*
     ---------------------------------run------------------------------------------------
     */
    public void run() {
        System.out.println("Esperando conexiones");
        boolean flag = true;
        while (flag) {
            try {

                servidorAdm = new ServidorTCP(6666);/*creo una instancia de servidor TCP*/
                servidorAdm.conexion();/*conecto*/
                this.hola = true;
                while (this.hola == true) {
                    try {
                        Object comando = servidorAdm.recibir();/*recibo el 
                         * comando de la ventana de administracion*/
                        switch (this.analizar(comando)) {
                            case 1: {
                                this.ManipularLista();
                                servidorAdm.mandar("recibido");
                                break;
                            }
                            case 2: {
                                this.EnviarListas(servidorAdm);
                                break;
                            }
                            case 3: {
                                this.Banderas();
                                servidorAdm.mandar("recibido");
                                break;
                            }
                            case 4: {
                                this.MandarBanderas(servidorAdm);
                                break;
                            }
                            case 5: {
                                servidorAdm.Close();
                                hola = false;
                                break;
                            }
                        }
                    } catch (ClassNotFoundException ex) {
                    } catch (NullPointerException ex) {
                    } catch (IOException ex) {
                    }
                }
            } catch (IOException ex) {
            }
        }
    }
}