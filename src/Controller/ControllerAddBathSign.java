package Controller;

import conexao.ConexaoBD;
import conexao.CRUD;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author MARCOS
 */
public class ControllerAddBathSign {
    ConexaoBD conexaoAdd;
    BufferedImage returnedImage;
    CRUD crud;
    ArrayList<String> successSavedImg;
    ArrayList<String> errorSavingImg;
    
    private static ControllerAddBathSign uniqueInstance;
 
    private ControllerAddBathSign(ConexaoBD conexao) {
        successSavedImg = new ArrayList();
        errorSavingImg = new ArrayList();
        crud = CRUD.getInstance(conexao);
    }
 
    public static synchronized ControllerAddBathSign getInstance(ConexaoBD conexao) {
        if (uniqueInstance == null)
            uniqueInstance = new ControllerAddBathSign(conexao);
           
        return uniqueInstance;
    }
    
    public void Add(ArrayList<String[]> signList){
        for(int i = 0; i < signList.size(); i++){
            String associetedWord = signList.get(i)[0];
            String signImg = signList.get(i)[1];
            String associativeImg = signList.get(i)[2];
            
            if(crud.add(associetedWord, signImg, associativeImg))
                successSavedImg.add(signList.get(i)[0]);
            else
                errorSavingImg.add(signList.get(i)[0]); 
        }
    }
    
    public void Update(ArrayList<String[]> signList){
        for(int i = 0; i < signList.size(); i++){
            String associetedWord = signList.get(i)[0];
            String signImg = signList.get(i)[1];
            String associativeImg = signList.get(i)[2];
            
            if(crud.Update(associetedWord, signImg, associativeImg))
                successSavedImg.add(signList.get(i)[0]);
            else
                errorSavingImg.add(signList.get(i)[0]); 
        }
    }
    
    public void ClearArray(){
        successSavedImg.clear();
        errorSavingImg.clear();
    }
    
    public boolean checkWordSignal(String  associetedWord){
        boolean test = crud.Select(associetedWord);
               
        return test;
    }
    
    public String ListRepeatedSigns(ArrayList<String[]> repeatedImgList){
        String message = null;
        
        if(!repeatedImgList.isEmpty()){
            message = "Os sinais descritos na lista a seguir já se encontram registrados.\n"
                    + "Deseja atualizá-los?"
                    + "\n\nSegue a lista:";
            for(int i = 0; i < repeatedImgList.size(); i++){
                message = message + "\n     " + (String) repeatedImgList.get(i)[0];
            }
        }
        
        return message;
    }
    
    public String SuccessSavedImg(){
        String message = null;
        if(!successSavedImg.isEmpty())
            message = "A seguir, os sinais registrados com sucesso!";
            for(int i = 0; i < successSavedImg.size(); i++)
                message = message + "\n  " + successSavedImg.get(i);
        
        return message;
    }
    
    public String ErrorSavingImg(){
        String message = null;
        if(!errorSavingImg.isEmpty())
            message = "A seguir, os sinais não registrados por erros de sistema!";
            for(int i = 0; i < errorSavingImg.size(); i++)
                message = message + "\n  " + errorSavingImg.get(i);
        
        return message;
    }
    
    public ArrayList<Object[]> UpdateRows(ArrayList<Object[]> lista){
        ArrayList<Object[]> listAuxiliary = new ArrayList<>();
        for(int i = 0; i < lista.size(); i++){
            if(!successSavedImg.contains((String) lista.get(i)[3])){
                listAuxiliary.add(lista.get(i));
            }
        }
        
        return listAuxiliary;
    }
   
    private boolean isImg(String nameImg){
        boolean test = false;
        
        String extension = nameImg.substring(nameImg.length()-3, nameImg.length());
        
        if(extension.equals("jpg") || extension.equals("png") || extension.equals("gif"))
            test = true;
        
        return test;
    }
    
    private ArrayList<Object[]> ClearAssociativeImg(ArrayList<Object[]> lista){
        ArrayList<Object[]> auxList = new ArrayList<>();
        for(int i = 0; i < lista.size(); i++){
            Object[] element = lista.get(i);
            element [2] = "";
            
            auxList.add(element);
        }
        
        return auxList;
    }
    
    public ArrayList<Object[]> AssociativeImg(String folderPathImgAssociatives, ArrayList<Object[]> lista){
        lista = ClearAssociativeImg(lista);
        
        //LISTAR ARQUIVOS DE PASTA IMAGENS ASSOCIATIVAS       
        File arquivos = new File(folderPathImgAssociatives);
        File[] file = arquivos.listFiles();
        
        for(int i=0; i<file.length ; i++){
            File f = file[i];
            if(f.isFile() && isImg(f.getName())){
                String nameAssImg = f.getName();
                nameAssImg = nameAssImg.substring(0, nameAssImg.length()-4);
                nameAssImg = nameAssImg.toLowerCase();
                
                for(int j=0; j<lista.size(); j++){
                    Object[] linha = lista.get(j);
                    String nameSinal = (String) linha[1];
                    nameSinal = nameSinal.substring(0, nameSinal.length()-4);
                    nameSinal = nameSinal.toLowerCase();
                    
                    if(nameAssImg.equals(nameSinal)){
                        linha[2] = f.getName();
                        
                        lista.remove(j);
                        lista.add(linha);
                        
                        break;
                    }
                }
            }            
        }
        
        return lista;
    }
    
    public ArrayList<Object[]> sinalImg(String folderPathSinais){
        ArrayList<Object[]> list = new ArrayList<>();
        
        //LISTAR ARQUIVOS DE PASTA IMAGENS SINAIS       
        File arquivos = new File(folderPathSinais);
        File[] file = arquivos.listFiles();
          
        if(file != null){       
            for(int i = 0; i < file.length; ++i){ 
                File f = file[i]; 

                if(f.isFile() && isImg(f.getName())){
                    String arquivo = f.getName();

                    Object[] linha ={Boolean.TRUE, arquivo,"", arquivo.substring(0, arquivo.length()-4)};
                    
                    list.add(linha);                    
                } 
            }
        }
        
        return list;
    }  
}
