package esinais;

import java.io.File;
import javax.swing.filechooser.FileFilter;

class AllFiles extends FileFilter {  
        public boolean accept(File f) {  
           return f.isDirectory() || f.getName().toLowerCase().endsWith(".png") || 
                   f.getName().toLowerCase().endsWith(".gif") ||
                   f.getName().toLowerCase().endsWith(".jpg");  
        }
        public String getDescription() {  
           return "Todos tipos de imagem";  
        }  
}