package proxy;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class HiloNav extends Thread {

    private ArrayList<String> ListaURL;
    private ArrayList<String> ListaPalabra;
    public int está;
    private ServidorTCPParaMultihilo servidorNav;
    private int indic;
    private Socket s;
    private int encontrada = 0;
    private ArrayList<String> comando;
    private boolean flag = true;
    /*---------------------------constructor del hilo-----------------------------*/

    public HiloNav(Socket s) {
        this.s = s;


    }
    /*-------------metodo para separar las acciones segun el pedido---------------*/

    public void pedido(String comando) {
        if (comando.equals("GET")) {
            indic = 1;
        } else if (comando.equals("POST")) {
            indic = 2;
        } else {
            indic = 3;
        }
    }

    public int Buscar(String direccion) {

        for (int i = 0; i < ListaURL.size(); i++) {
            if (!direccion.equalsIgnoreCase(ListaURL.get(i))) {
                /*busco en pedidoDePagina lista de URLs bloqueadas y aumento el contador si 
                 hay coincidencias*/
                está = 0;
            } else {
                está = 1;
            }
        }
        return está;
    }

    public String BuscarYRemplazar(String pagina) {
        ListaPalabra = HiloAdm.getListaPalabra();
        if (!ListaPalabra.isEmpty()) {
/*si la lista no está vacia se busca dentro de la pagina*/
            for (int i = 0; i < ListaPalabra.size(); i++) {
                if (pagina.contains(ListaPalabra.get(i))) {
                    String palabra = ListaPalabra.get(i);
                    pagina = pagina.replace(palabra, " ");/*remplazo la palabra en la pagina*/
                } else {
                    pagina = pagina;
                }
            }
        }

        return pagina;
    }
    /*----------------Metodo para buscar una palabra URL y mandar el mensaje------*/

    public void URLdenegada() {
        int i;

        for (i = 1; i < comando.size(); i++) {
            this.encontrada = this.encontrada + Buscar(comando.get(i));
        }/*cada vez que se encuentre una palabra prihibida en pedidoDePagina URL se incrementa
         el contador*/
        if (encontrada > 0) {
            try {/*si hay palabras prohibidas se manda el mensaje correspondiente*/
                servidorNav.mandar("<html>URL prohibida</html>");
                this.flag = false;
            } catch (IOException ex) {
            }
        } else {
            ArrayList pedido = servidorNav.pedido();
            ClienteTCPparaWeb clienteNav = new ClienteTCPparaWeb(pedido.get(1).toString().split(" ")[1], 80);
            /*he abierto el cliente para comunicarse con el servidor de la pagina, puerto 80*/
            try {
                String peticion = ((String) (pedido.get(0))).replace("1.1", "1.0");
/*trabajo con http 1.0*/
                String pedidoDePagina = peticion + "\n" + pedido.get(1) + "\n" + pedido.get(2) + "\n" + pedido.get(3) + "\n"
                        + pedido.get(4) + "\n" + "Connection: close" + "\n" + pedido.get(7) + "\n\n";
             /*armo el pedido*/
                clienteNav.conect();
                clienteNav.mandar(pedidoDePagina);
                try {
                    if (pedidoDePagina.toLowerCase().contains("png")
                            || pedidoDePagina.toLowerCase().contains("jpg")
                            || pedidoDePagina.toLowerCase().contains("gif")
                            || pedidoDePagina.toLowerCase().contains("image")
                            || pedidoDePagina.toLowerCase().contains("jpeg")
                            || pedidoDePagina.toLowerCase().contains("application")) {
                        int comprobante;
                        byte[] imagen = clienteNav.recibirIm();/*recibo imagen*/
                        comprobante = clienteNav.getNunBytes();/*pido numero de bytes leidos*/
                        servidorNav.mandarIm(imagen, clienteNav.getNunBytes());/*mando la imagen al nav*/
                        while (comprobante != -1) {

                            imagen = clienteNav.recibirIm();
                            comprobante = clienteNav.getNunBytes();
                            servidorNav.mandarIm(imagen, clienteNav.getNunBytes());
                        }
                        clienteNav.mandar("HTTP/1.0 200 OK\n\r");
                        servidorNav.Close();
                        clienteNav.close();
                        this.flag = false;
                    } else {
                        String o = BuscarYRemplazar(clienteNav.recibir());/*busco y remplazo palabras prohibidas*/
                        servidorNav.mandar(o);
                        clienteNav.mandar("HTTP/1.0 200 OK\n\r");
                        clienteNav.close();
                        servidorNav.Close();
                        this.flag = false;
                    }




                } catch (ClassNotFoundException ex) {
                }
            } catch (IOException ex) {
            } catch (IndexOutOfBoundsException ex) {
            }
        }

    }

    public void accion() {
        ListaURL = HiloAdm.getListaURL();/*pido la lista URL*/
        ListaPalabra = HiloAdm.getListaPalabra();/*pido lista palabra*/
        if (indic == 1) {
            if (HiloAdm.isGet() == true) {
                this.URLdenegada();
            } else {
                try {
                    servidorNav.mandar("HTTP/1.0 403 Prohibido \r\nConnection:"
                            + " close\r\n\r\n <html>Get prohibido"
                            + " por administrador<br/></html>");
                    this.URLdenegada();
                } catch (IOException ex) {
                }
            }
        } else if (indic == 2) {
            if (HiloAdm.isPost() == true) {
                this.URLdenegada();
            } else {
                try {
                    servidorNav.mandar("HTTP/1.0 403 Forbidden \r\nConnection: close"
                            + "\r\n\r\n <html>Post prohibido por administrador<br/></html>");
                    this.URLdenegada();
                } catch (IOException ex) {
                }
            }
        } else {
            try {
                servidorNav.mandar("HTTP/1.0 501 Not Implemented\r\n\r\n <html>no implementado<br/></html>");
                this.URLdenegada();
            } catch (IOException ex) {
            }
        }
    }

    public void run() {
        while (flag) {
            try {
                servidorNav = new ServidorTCPParaMultihilo(s);
                comando = new ArrayList();
                comando = servidorNav.recibir();
                String accion = comando.get(0);
                this.pedido(accion);
                this.accion();
            } catch (IOException ex) {
            } catch (ClassNotFoundException ex) {
            } catch (NullPointerException ex) {
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
        }
    }
}
