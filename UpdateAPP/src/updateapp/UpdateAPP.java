/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package updateapp;

import java.net.MalformedURLException;
import javax.swing.JOptionPane;

/**
 *
 * @author march
 */
public class UpdateAPP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String repo = "https://github.com/marcheloBM/ActualizacionAPP";
        String versionActual = "1.6";

        String ultimaVersion = GitHubReleaseGUI.obtenerUltimaVersion(repo);

        if (ultimaVersion == null) {
            JOptionPane.showMessageDialog(null, "⚠️ No se pudo verificar la versión.");
            //Inicia el programa si no se puede verificar
            new FrInicio().setVisible(true);
        } else if (ultimaVersion.equals(versionActual)) {
            //JOptionPane.showMessageDialog(null, "✅ Estás usando la última versión (" + versionActual + ").");
            //Si estamos usando la version actual
            new FrInicio().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "🟢 Hay una nueva versión disponible: " + ultimaVersion);
            int respu = JOptionPane.showConfirmDialog(null, "¿Desea descargar la nueva versión?");
            if (respu == JOptionPane.YES_OPTION) {
                //Abrimos para descargar la nueva version
                GitHubReleaseGUI.main(args);
            } else {
                JOptionPane.showMessageDialog(null, "Intente mantener el programa actualizado.");
                //Si no queremos actualizar a la ultima Version
                new FrInicio().setVisible(true);
            }
        }

//        GitHubReleaseGUI.main(args);
//        boolean resp =buscarUpdate();
//        if(resp==false){
//            new FrHome().setVisible(true);
//        }
        
    }
//    public static boolean buscarUpdate(){
//        boolean resp;
//        if(Actualizacion.verificarConexion()){
//            if(Actualizacion.obtenerVersion().equals(Confi.Version)){
//                resp=false;
//            }else{
//                resp=true;
//                int respu = JOptionPane.showConfirmDialog(null, "Version "+Actualizacion.obtenerVersion()+ " Diponible \n¿Desea Descargar?");
//                if(respu==0){
//                    JOptionPane.showMessageDialog(null, "Descargando Update \nEspere Mensaje");
//                    Actualizacion.descargarUpdate();
//                    resp=true;
//                }else{
//                    resp=false;
//                }
//            }
//        }else{
//            resp=false;
//        }
//        return resp;
//    }
}
