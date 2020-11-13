/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import conexao.CRUD;
import conexao.ConexaoBD;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author MARCOS
 */
public class ControllerDelSign {

    ConexaoBD conexaoAdd;
    CRUD crud;

    private static ControllerDelSign uniqueInstance;

    private ControllerDelSign(ConexaoBD conexao) {
        crud = CRUD.getInstance(conexao);
    }

    public static synchronized ControllerDelSign getInstance(ConexaoBD conexao) {
        if (uniqueInstance == null) {
            uniqueInstance = new ControllerDelSign(conexao);
        }

        return uniqueInstance;
    }

    public ArrayList<Object[]> selectAll() {
        return crud.SelectAll();
    }

    public ArrayList<Object[]> selectSearch(String name) {
        return crud.SelectSearch(name);
    }

    public ArrayList<String[]> delete(ArrayList<String[]> list) {
        ArrayList<String[]> successDeleted = new ArrayList<>();
        
        for (int i=0; i<list.size(); i++) {
            String[] sinal = list.get(i);
            
            if(crud.delete(sinal[0])){
                File imgSinal = new File("dados/images/gif/" + sinal[1]);
                File imgSinalAss = new File("dados/images/imagem_associativa/" + sinal[2]);
                imgSinal.delete();
                imgSinalAss.delete();
                
                successDeleted.add(sinal);
            }
        }
        return successDeleted;
    }
}
