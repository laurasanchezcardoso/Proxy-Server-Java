/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package administrador;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author carolina
 */
public class Mensaje {

    JFrame ventana;
    JDialog dialog;

    private Mensaje() {
    }

    public Mensaje(String mensaje) {
        ventana = new JFrame();
        dialog = new JDialog(ventana, "", true);
        JLabel contenido = new JLabel(mensaje);
        Container contentPane = dialog.getContentPane();
        contentPane.add(contenido, BorderLayout.CENTER);
        JButton ok = new JButton("ok");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }

            private void okActionPerformed(ActionEvent evt) {
                ventana.dispose();
            }
        });
        contentPane.add(ok, BorderLayout.PAGE_END);
        contentPane.setPreferredSize(contentPane.getPreferredSize());
        dialog.setSize(new Dimension(300, 200));
        dialog.setResizable(false);

    }

    public void mostrar() {
        dialog.setVisible(true);
    }
}

