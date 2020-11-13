package conexao;

import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

/**
 *
 * @author MARCOS
 */
public class CRUD {
    ConexaoBD conexaoDB;
    BufferedImage returnedImage;
    
    private CRUD(ConexaoBD conexao) {
        conexaoDB = conexao;
    }
    
    private static CRUD uniqueInstance;
 
    public static synchronized CRUD getInstance(ConexaoBD conexao) {
        if (uniqueInstance == null)
            uniqueInstance = new CRUD(conexao);
           
        return uniqueInstance;
    }
    
    public ArrayList<Object[]> SelectAll(){
        ArrayList<Object[]> list = new ArrayList<>();
        
        try {
            Statement sta = conexaoDB.getStatement();
            ResultSet res = sta.executeQuery("SELECT nome_sinal, endereco_sinal, endereco_imagem FROM PUBLIC.SINAL");
            
            while (res.next()) {
                Object[] row = new Object[3];
                
                row[0] = res.getString("nome_sinal");
                row[1] = clearWordReturn(res.getString("endereco_sinal"));
                row[2] = clearWordReturn(res.getString("endereco_imagem"));
                
                list.add(row);
            }           
        } catch (SQLException ex) {
            Logger.getLogger(CRUD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }
    
    public ArrayList<Object[]> SelectSearch(String name){
        ArrayList<Object[]> list = new ArrayList<>();
        
        try {
            Statement sta = conexaoDB.getStatement();
            ResultSet res = sta.executeQuery("SELECT nome_sinal, endereco_sinal, endereco_imagem FROM PUBLIC.SINAL"
                    + " WHERE nome_sinal LIKE '%" + name + "%';");
            
            while (res.next()) {
                Object[] row = new Object[3];
                
                row[0] = res.getString("nome_sinal");
                row[1] = clearWordReturn(res.getString("endereco_sinal"));
                row[2] = clearWordReturn(res.getString("endereco_imagem"));
                
                list.add(row);
            }           
        } catch (SQLException ex) {
            Logger.getLogger(CRUD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }
    
    public boolean Select(String word){
        boolean test = false;
        word = ClearWord(word);
        
        try {
            VetorPesquisa dados = new VetorPesquisa();
        
            conexaoDB.query("SELECT endereco_sinal FROM PUBLIC.SINAL WHERE nome_sinal = '"
                    + word.toLowerCase() + "'", dados);
       
            Vector linhas = dados.getLinhas();
            
            if (!"[]".equals(linhas.toString())) 
                test = true;
            
             } catch (SQLException ex) {
            Logger.getLogger(CRUD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return test;
    }
    
    public String ClearWord(String word){
        String newWord = word.replaceAll("[^A-Za-z0-9áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ]", "");
        return newWord;
    }
    
    public String clearWordReturn(String word){
        String wordReturn = "";
        if (word != null){
            String[] newWord = word.split("/");
            wordReturn = newWord[newWord.length - 1];
        }
        
        return wordReturn;
    }
    
    public String NewPathSinal(String associetedWord, String pathSinal){
        String typeSinal = pathSinal.substring(pathSinal.length() - 3, pathSinal.length());
        String newPathSinal = "/images/gif/" + associetedWord + "." + typeSinal;
        
        return newPathSinal;
    }
    
    public String NewPathAssImg(String  associetedWord, String associativeImg){
        String pathImagem = associativeImg;
        if (pathImagem.equals("")) {
            pathImagem = "null";
        } else {
            String typeImagem = pathImagem.substring(pathImagem.length() - 3, pathImagem.length());
            pathImagem = "/images/gif/" + associetedWord + "." + typeImagem;
        }
        
        return pathImagem;
    }
    
    private void SetTransferFile(String word, String pathImg, String pathSinal) throws IOException, SQLException{
        if (pathImg.equals(""))
             CopiarArquivo("gif", pathSinal, word, true);
        else{ 
            CopiarArquivo("imagem_associativa", pathImg, word, true);
            CopiarArquivo("gif", pathSinal, word, true);
        }
    }
    
    public boolean Update(String  associetedWord, String signImg, String associativeImg){
        boolean result = true;
        
        try {
            String word = ClearWord(associetedWord);
            
            String pathSinal = signImg;
            String newPathSinal = NewPathSinal(word, pathSinal);
            
            String pathAssImg = associativeImg;
            String newPathAssImg = NewPathAssImg(word, associativeImg);
            
            SetTransferFile(word, pathAssImg, pathSinal);
            
            if (!pathAssImg.isEmpty())
                conexaoDB.update("UPDATE PUBLIC.SINAL SET endereco_sinal = '"
                        + newPathSinal + "', endereco_imagem = '" + newPathAssImg + "' WHERE nome_sinal = '"
                        + word.toLowerCase() + "'");
            else
                conexaoDB.update("UPDATE PUBLIC.SINAL SET endereco_sinal = '"
                        + newPathSinal + "' WHERE nome_sinal = '" + word.toLowerCase() + "'");
            
            conexaoDB.update("COMMIT");
        } catch (IOException | SQLException ex) {
            result = false;
        }
        
        return result;
    }
    
    public boolean delete(String sinal){
        boolean result = true;
        try {
            conexaoDB.delete("DELETE FROM PUBLIC.SINAL WHERE nome_sinal = '"
                        + sinal.toLowerCase() + "'");
            
            conexaoDB.delete("COMMIT");
        } catch (SQLException ex) {
            result = false;
        }
        
        return result;
    }
    
    public boolean add(String  associetedWord, String signImg, String associativeImg){
        boolean result = true;
        
        try {
            String word = ClearWord(associetedWord);
            
            String pathSinal = signImg;
            String newPathSinal = NewPathSinal(word, pathSinal);
            
            String pathAssImg = associativeImg;
            String newPathAssImg = NewPathAssImg(word, associativeImg);
            
            SetTransferFile(word, pathAssImg, pathSinal);
            
            if (!pathAssImg.isEmpty()) {
                conexaoDB.update("INSERT INTO SINAL (NOME_SINAL, ENDERECO_SINAL, ENDERECO_IMAGEM) VALUES ('"
                        + word.toLowerCase() + "', '" + newPathSinal + "', '" + newPathAssImg + "')");
            } else {
                conexaoDB.update("INSERT INTO SINAL (NOME_SINAL, ENDERECO_SINAL) VALUES ('"
                        + word.toLowerCase() + "', '" + newPathSinal + "')");
            }
        } catch (IOException | SQLException ex) {
            result = false;
        }
         
        return result;
    }
    
    public void CopiarArquivo(String newPathImg, String path, String word, boolean resposta) throws IOException, SQLException {
        String palavra = word;
        String type = path.substring(path.length() - 3, path.length());
        String newPath = "/images/"+newPathImg+"/" + palavra + "." + type;
        if (resposta) {
            File image = new File(path);
            File newImage = new File("dados" + newPath);
            newImage.delete();
            int size = 1;
            if ("gif".equals(type)) {
                ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
                ir.setInput(ImageIO.createImageInputStream(image));
                size = ir.getNumImages(true);
            }
            if (size >= 1) {
                FileChannel sourceChannel = null;
                FileChannel destinationChannel = null;
                try {
                    sourceChannel = new FileInputStream(image).getChannel();
                    destinationChannel = new FileOutputStream(newImage).getChannel();
                    sourceChannel.transferTo(0, sourceChannel.size(),
                            destinationChannel);
                } finally {
                    if (sourceChannel != null && sourceChannel.isOpen()) {
                        sourceChannel.close();
                    }
                    if (destinationChannel != null && destinationChannel.isOpen()) {
                        destinationChannel.close();
                    }
                }
            } else {
                ImageIO.write(returnedImage, type, newImage);
            }
        } else {
            File image = new File(path);
            File newImage = new File("dados" + newPath);

            int size = 1;
            if ("gif".equals(type)) {
                ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
                ir.setInput(ImageIO.createImageInputStream(image));
                size = ir.getNumImages(true);
            }
            if (size >= 1) {
                FileChannel sourceChannel = null;
                FileChannel destinationChannel = null;
                try {
                    sourceChannel = new FileInputStream(image).getChannel();
                    destinationChannel = new FileOutputStream(newImage).getChannel();
                    sourceChannel.transferTo(0, sourceChannel.size(),
                            destinationChannel);
                } finally {
                    if (sourceChannel != null && sourceChannel.isOpen()) {
                        sourceChannel.close();
                    }
                    if (destinationChannel != null && destinationChannel.isOpen()) {
                        destinationChannel.close();
                    }
                }
            } else {
                ImageIO.write(returnedImage, type, newImage);
            }
        }
    }

}
