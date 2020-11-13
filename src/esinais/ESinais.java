package esinais;

import conexao.ConexaoBD;
import conexao.VetorPesquisa;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.CannotRedoException;
import java.io.FileNotFoundException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.codec.GifImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ESinais extends javax.swing.JFrame {

    ArrayList<Labels> labels = new ArrayList();

    UndoManager undoManager = new UndoManager();
    String[] palavras, imAss;
    ImageIcon imageIcon, imagemAssociativa;
    JFileChooser savePDF;
    ConexaoBD conexao;
    Load load;
    String imagemSinal,imagemAssoc;

    boolean firstfocusdone;

    int idAtual;
    int returnValPDF;
    int x, value,nol;

    public ESinais() {
        try {
            conexao = new ConexaoBD();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Não foi possivel conectar ao Banco de Dados"
                    + "\nTalvez já exista outra instância aberta", "e-Sinais", JOptionPane.ERROR_MESSAGE);
            this.close();
        }
        nol = 0;
        initComponents();
        manualMenuItem.setVisible(false);
        manualToolBarButton.setVisible(false);
        load = new Load(this);

        this.setIconImage(getToolkit().getImage(getClass().getResource("/images/"
                + "Icons/logo.png")));

        jScrollPane2.getVerticalScrollBar().setUnitIncrement(50);

        jPanel2.setFocusable(true);
        jPanel1.setLayout(new WrapLayout(WrapLayout.LEFT));
    }

    public void updateButtons() {
        undoMenuItem.setEnabled(undoManager.canUndo());
        redoMenuItem.setEnabled(undoManager.canRedo());
        undoToolBarButton.setEnabled(undoManager.canUndo());
        redoToolBarButton.setEnabled(undoManager.canRedo());
    }

     public void clear() {
        for (int i = 0; i < nol; i++) {
            //Excluir labels
            labels.get(i).getNome().setVisible(false);
            jPanel1.remove(labels.get(i).getNome());
        }

        labels = new ArrayList();
        nol = 0;

        clearMenuItem.setEnabled(false);
        clearToolBarButton.setEnabled(false);
    }

    public void decreaseFont() {
        if (jTextArea1.getFont().getSize() > 8) {
            jTextArea1.setFont(new java.awt.Font("Monospaced", 0, jTextArea1.getFont().getSize() - 2));
        }
    }

    public void increaseFont() {
        if (jTextArea1.getFont().getSize() < 44) {
            jTextArea1.setFont(new java.awt.Font("Monospaced", 0, jTextArea1.getFont().getSize() + 2));
        }
    }

    public void defaultFont() {
        jTextArea1.setFont(new java.awt.Font("Monospaced", 0, 20));
    }

    public void undo() {
        try {
            undoManager.undo();
        } catch (CannotRedoException cre) {
            cre.printStackTrace();
        }
        updateButtons();
    }

    public void redo() {
        try {
            undoManager.redo();
        } catch (CannotRedoException cre) {
            cre.printStackTrace();
        }
        updateButtons();
    }

    void close() {
        try {
            conexao.close();
        } catch (SQLException ex) {
            Logger.getLogger(ESinais.class.getName()).log(Level.SEVERE, null, ex);
        }
        load.dispose();
        this.dispose();
    }

    void translate() {
        if (firstfocusdone) {

            clear();

            String text;
            text = jTextArea1.getText();
            text = text.replaceAll("[^A-Za-z0-9 áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ\n\t]", "");
            text = text.replace(" ", ";");
            text = text.replace("\t", ";");
            text = text.replace("\n", ";");
            x = 0;

            palavras = text.split(";");

            load.setText("Carregando Sinais...");
            load.setValue(0);
            load.close = false;

            SwingWorker worker;
            worker = new SwingWorker() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (int i = 0; i < palavras.length && !load.close; i++) {

                        if (palavras[i].length() != 0) {
                            String palavra = palavras[i];
                            
                            //Salvar a palavra para buscar a imagem associativa
                            imagemSinal = palavras[i];

                            //Buscar "palavra" no banco de dados
                            try {
                                JLabel lbl = new JLabel();
                                lbl.setName("Label_" + x);

                                lbl.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                                lbl.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                                lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                                lbl.addMouseListener(new java.awt.event.MouseAdapter() {
                                    @Override
                                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                                        JLabel j1 = (JLabel) evt.getSource();
                                        String nomeLabel[] = j1.getName().split("_");
                                        idAtual = Integer.parseInt(nomeLabel[1]);
                                        popup(evt);
                                    }
                                });

                                jPanel1.add(lbl);
                                labels.add(new Labels(lbl));
                                nol++;

                                VetorPesquisa dados = new VetorPesquisa();
                                String querySinal, queryImagem;

                                //buscar o sinal
                                querySinal = "SELECT endereco_sinal FROM PUBLIC.SINAL WHERE "
                                        + "nome_sinal = '" + palavra.toLowerCase() + "'";

                                //buscar a imagem associativa
                                queryImagem = "SELECT endereco_imagem FROM PUBLIC.SINAL WHERE "
                                        + "nome_sinal = '" + palavra.toLowerCase() + "'";

                                //pesquisar o GIF
                                conexao.query(querySinal, dados);

                                Vector linhas = dados.getLinhas();

                                String outSinal;
                                String outImagem = "";

                                if ("[]".equals(linhas.toString())) {
                                    outSinal = "dados" + "/images/gif/not_found.gif";
                                    outImagem = "dados/images/imagem_associativa/not_found.gif";
                                }else {
                                    outSinal = "dados" + linhas.toString().substring(2, linhas.toString().length() - 2);
                                    //pesquisar a imagem associativa
                                    conexao.query(queryImagem, dados);
                                    //Guarda os resultados da pesquisa
                                    Vector linhas2 = dados.getLinhas();
                                   
                                    if (!linhas2.toString().equals("[[null]]")) {
                                        outImagem = "dados" + linhas2.toString().substring(2, linhas2.toString().length() - 2);
                                    } else {
                                        outImagem = "dados/images/imagem_associativa/not_found.gif";
                                    }

                                }
                                imageIcon = new ImageIcon(outSinal);
//                                imAss[i] = outImagem;

                                imagemAssoc = outImagem;
                                labels.get(x).getNome().setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
                                labels.get(x).getNome().setIcon(imageIcon);
                                labels.get(x).getNome().setToolTipText(palavra);
                                labels.get(x).setEndereco(outSinal);
                                labels.get(x).getNome().setVisible(true);
                                labels.get(x).setId(x);

                                x++;

                                clearMenuItem.setEnabled(true);
                                clearToolBarButton.setEnabled(true);
                            } catch (SQLException ex) {
                                Logger.getLogger(ESinais.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        value = (int) (((double) i / (double) palavras.length) * 100.0);
                        load.setValue(value);
                    }
                    return null;
                }

                protected void done() {
                    if (load.close) {
                        clear();
                    }
                    load.setVisible(false);
                }
            };

            worker.execute();
            load.setVisible(true);

            jTextArea1.setFocusable(true);
        }
    }

    void save(int id) throws IOException {
        JFileChooser saveFile = new JFileChooser();

        UIManager.put("FileChooser.fileNameLabelText", "Nome da Imagem:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Mostrar imagens do tipo:");
        SwingUtilities.updateComponentTreeUI(saveFile);

        saveFile.setDialogTitle("Salvar Imagem");

        saveFile.setAcceptAllFileFilterUsed(false);
        saveFile.setFileFilter(new AllFiles());

        int returnVal = saveFile.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            int result = JOptionPane.YES_OPTION;

            String type = labels.get(id).getEndereco().substring(labels.get(id).getEndereco().length() - 3,
                    labels.get(id).getEndereco().length());

            String path = saveFile.getSelectedFile().getAbsolutePath() + "." + type;

            File newImage = new File(path);
            File image = new File(labels.get(id).getEndereco());

            if (newImage.exists()) {
                result = JOptionPane.showConfirmDialog(this, "Este arquivo já existe\n"
                        + "Deseja Substitui-lo?", "e-Sinais", JOptionPane.YES_NO_CANCEL_OPTION);
            }

            if (result == JOptionPane.YES_OPTION) {

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
            }
        }
    }

    void AddSign(int id) {
        if (id < 0) {
            new AddSign(this, "", conexao).setVisible(true);
        } else {
            String text;
            text = jTextArea1.getText();
            text = text.replaceAll("[^A-Za-z0-9 áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ\t\n]", "");
            text = text.replace(" ", ";");
            text = text.replace("\t", ";");
            text = text.replace("\n", ";");

            palavras = text.split(";");

            ArrayList<String> usefulWords = new ArrayList();

            for (int i = 0; i < palavras.length; i++) {
                if (palavras[i].length() != 0) {
                    usefulWords.add(palavras[i]);
                }
            }

            new AddSign(this, usefulWords.get(id), conexao).setVisible(true);
        }
        imageIcon.getImage().flush();
        translate();
    }
    
    void AddBathSign() {
        AddBathSign.getInstance(this, conexao).setVisible(true);      
        translate();
    }

    void ShowImage(int id) throws SQLException{
         if (id >= 0) {
        VetorPesquisa dados = new VetorPesquisa();
                                String queryImagem;

                                //buscar a imagem associativa
                                queryImagem = "SELECT endereco_imagem FROM PUBLIC.SINAL WHERE "
                                        + "nome_sinal = '" + palavras[id].toLowerCase() + "'";

                                conexao.query(queryImagem, dados);
                                String outImagem = "";

                                    //conexao.query(queryImagem, dados);
                                    //Guarda os resultados da pesquisa
                                    Vector linhas2 = dados.getLinhas();
                                    if(linhas2.size() == 0){
                                         outImagem = "dados/images/imagem_associativa/not_found.gif";
                                    } else{
                                    if (!linhas2.toString().equals("[[null]]")) {
                                        outImagem = "dados" + linhas2.toString().substring(2, linhas2.toString().length() - 2);
                                    } else{
                                         outImagem = "dados/images/imagem_associativa/not_found.gif";
                                    }
                                    }
                                imagemAssoc = outImagem;
        
            new View_Image(this, imagemAssoc, palavras[id]).setVisible(true);
        }
        imageIcon.getImage().flush();
        translate();
    }

    void export() {
        int result = JOptionPane.NO_OPTION;

        if (nol > 50) {
            /*result = JOptionPane.showConfirmDialog(this, "Seu texto de entrada contém "
                    + nol + " palavras\nO recomendado é no maximo 50 palavras\n"
                    + "Deseja continuar?", "e-Sinais", JOptionPane.YES_NO_OPTION);
             */
            result = JOptionPane.YES_OPTION;
        } else {
            result = JOptionPane.YES_OPTION;
        }

        if (result == JOptionPane.YES_OPTION) {
            savePDF = new JFileChooser();

            UIManager.put("FileChooser.fileNameLabelText", "Nome do Arquivo:");
            UIManager.put("FileChooser.filesOfTypeLabelText", "Mostrar arquivos do tipo:");
            SwingUtilities.updateComponentTreeUI(savePDF);

            returnValPDF = savePDF.showSaveDialog(null);

            load.setText("Gerando PDF...");
            load.setValue(0);
            load.close = false;

            SwingWorker worker2 = new SwingWorker() {
                protected Void doInBackground() throws Exception {
                    if (returnValPDF == JFileChooser.APPROVE_OPTION) {
                        try {
                            Document document = new Document();
                            FileOutputStream outputStr = new FileOutputStream(savePDF.getSelectedFile().getAbsolutePath() + ".pdf");
                            PdfWriter.getInstance(document, outputStr);
                            
                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Calendar cal = Calendar.getInstance();

                            document.open();

                            Font f = FontFactory.getFont("Times-Roman", 18, Font.BOLD);
                            document.add(new Paragraph("Gerado Automaticamente pelo e-Sinais\n"+ df.format(cal.getTime())));

                            Paragraph title = new Paragraph("e-Sinais", f);
                            title.setAlignment(Element.ALIGN_CENTER);

                            Paragraph images = new Paragraph();
                            images.setLeading(180);
                            Phrase p = new Phrase();

                            for (int i = 0; i < nol && !load.close; i++) {
                                String path = labels.get(i).getEndereco();
                                String type = path.substring(path.length() - 3, path.length());

                                if ("gif".equals(type)) {
                                    GifImage myGif = new GifImage(path);

                                    for (int j = 1; j <= myGif.getFrameCount() && !load.close; j += 4) {
                                        if (j > myGif.getFrameCount()) {
                                            break;
                                        }
                                        Image img = myGif.getImage(j);

                                        img.scaleAbsolute(100, 150);
                                        p.add(new Chunk(img, 0, 0, true));
                                        //p.add(" ");
                                    }
                                } else {
                                    Image img = Image.getInstance(path);
                                    img.scaleAbsolute(100, 150);
                                    p.add(new Chunk(img, 0, 0, true));
                                }

                                p.add(" ");

                                value = (int) (((double) i / (double) nol) * 100.0);
                                load.setValue(value);
                            }

                            images.add(p);

                            document.add(title);
                            document.add(images);

                            document.close();

                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(ESinais.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (DocumentException | IOException ex) {
                            Logger.getLogger(ESinais.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    return null;
                }

                protected void done() {
                    load.setVisible(false);
                }
            };

            worker2.execute();
            load.setVisible(true);
        }
    }

    void about() {
        new About(this).setVisible(true);
    }

    void manual() {
        //Em construção...
    }

    void jTextArea1UndoableEditHappened(UndoableEditEvent e) {
        if (firstfocusdone) {
            undoManager.addEdit(e.getEdit());
            updateButtons();
        }
    }

    void popup(java.awt.event.MouseEvent evt) {
        imagePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }

    void count() {
        int cont = 0;
        String text = jTextArea1.getText();
        text = text.replaceAll("[^A-Za-z0-9 áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ\n\t]", "");

        text = text.replace(" ", ";");
        text = text.replace("\t", ";");
        text = text.replace("\n", ";");

        palavras = text.split(";");

        for (int i = 0; i < palavras.length; i++) {

            if (palavras[i].length() != 0) {
                cont++;
            }
        }

        wordsLabel.setText(cont + " palavra(s)");
    }

    private void jTextArea1ChangedText(DocumentEvent e) {
    }

    private void jTextArea1InsertText(DocumentEvent e) {
        count();
    }

    private void jTextArea1RemoveText(DocumentEvent e) {
        count();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textPopupMenu = new javax.swing.JPopupMenu();
        cutMenuItemPopup = new javax.swing.JMenuItem();
        copyMenuItemPopup = new javax.swing.JMenuItem();
        pasteMenuItemPopup = new javax.swing.JMenuItem();
        imagePopupMenu = new javax.swing.JPopupMenu();
        saveImageMenuItem = new javax.swing.JMenuItem();
        addSignalMenuItem = new javax.swing.JMenuItem();
        showImageMenuItem = new javax.swing.JMenuItem();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        translateButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        translateToolBarButton = new javax.swing.JButton();
        clearToolBarButton = new javax.swing.JButton();
        addWordToolBarButton = new javax.swing.JButton();
        addNWordsToolBarButton = new javax.swing.JButton();
        delWordToolBarButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        undoToolBarButton = new javax.swing.JButton();
        redoToolBarButton = new javax.swing.JButton();
        manualToolBarButton = new javax.swing.JButton();
        aboutToolBarButton = new javax.swing.JButton();
        exitToolBarButton = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        decreaseFontButton = new javax.swing.JButton();
        defaultFontButton = new javax.swing.JButton();
        increaseFontButton = new javax.swing.JButton();
        wordsLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exportMenuItem = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        undoMenuItem = new javax.swing.JMenuItem();
        redoMenuItem = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        FontMenu = new javax.swing.JMenu();
        decreaseFontMenuItem = new javax.swing.JMenuItem();
        defaultFontMenuItem = new javax.swing.JMenuItem();
        increaseFontMenuItem = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        clearMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        translateMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        addWordMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        manualMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        cutMenuItemPopup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/cut.png"))); // NOI18N
        cutMenuItemPopup.setText("Recortar");
        cutMenuItemPopup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItemPopupActionPerformed(evt);
            }
        });
        textPopupMenu.add(cutMenuItemPopup);

        copyMenuItemPopup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/copy.png"))); // NOI18N
        copyMenuItemPopup.setText("Copiar");
        copyMenuItemPopup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyMenuItemPopupActionPerformed(evt);
            }
        });
        textPopupMenu.add(copyMenuItemPopup);

        pasteMenuItemPopup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/paste.png"))); // NOI18N
        pasteMenuItemPopup.setText("Colar");
        pasteMenuItemPopup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteMenuItemPopupActionPerformed(evt);
            }
        });
        textPopupMenu.add(pasteMenuItemPopup);

        saveImageMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/Save_file.png"))); // NOI18N
        saveImageMenuItem.setText("Salvar Imagem");
        saveImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImageMenuItemActionPerformed(evt);
            }
        });
        imagePopupMenu.add(saveImageMenuItem);

        addSignalMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/plus.png"))); // NOI18N
        addSignalMenuItem.setText("Adicionar Sinal");
        addSignalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSignalMenuItemActionPerformed(evt);
            }
        });
        imagePopupMenu.add(addSignalMenuItem);

        showImageMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/ver_imagem.png"))); // NOI18N
        showImageMenuItem.setText("Mostrar Imagem");
        showImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showImageMenuItemActionPerformed(evt);
            }
        });
        imagePopupMenu.add(showImageMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("e-Sinais: Software Tradutor de Palavra do Português para Sinais em LIBRAS");
        setBackground(new java.awt.Color(204, 204, 204));
        setExtendedState(6);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setName(""); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Monospaced", 0, 20)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Digitar texto aqui");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setMaximumSize(new java.awt.Dimension(2147483647, 120));
        jTextArea1.setMinimumSize(new java.awt.Dimension(100, 120));
        jTextArea1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextArea1FocusGained(evt);
            }
        });
        jTextArea1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextArea1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);
        jTextArea1.getDocument().addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent e) {
                jTextArea1UndoableEditHappened(e);
            }
        });

        jTextArea1.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                //System.out.println("insertUpdate");
                jTextArea1InsertText(e);
            }

            public void removeUpdate(DocumentEvent e) {
                // System.out.println("removeUpdate");
                jTextArea1RemoveText(e);
            }

            public void changedUpdate(DocumentEvent e) {
                //System.out.println("changeUpdate");
                jTextArea1ChangedText(e);
            }
        });

        jTextArea1.setComponentPopupMenu(textPopupMenu);

        translateButton.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        translateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/double_arrow_toolbar.png"))); // NOI18N
        translateButton.setText("Traduzir");
        translateButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        translateButton.setFocusable(false);
        translateButton.setRequestFocusEnabled(false);
        translateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                translateButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setToolTipText("");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1141, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(jPanel1);

        jLabel22.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel22.setText("Português Sinalizado:");

        jLabel23.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel23.setText("Sinais em LIBRAS:");

        jToolBar1.setBackground(new java.awt.Color(204, 204, 204));
        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        translateToolBarButton.setBackground(new java.awt.Color(204, 204, 204));
        translateToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/double_arrow_toolbar.png"))); // NOI18N
        translateToolBarButton.setToolTipText("Traduzir (Ctrl + T)");
        translateToolBarButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        translateToolBarButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        translateToolBarButton.setFocusable(false);
        translateToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        translateToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        translateToolBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                translateToolBarButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(translateToolBarButton);

        clearToolBarButton.setBackground(new java.awt.Color(204, 204, 204));
        clearToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/clear_toolbar.png"))); // NOI18N
        clearToolBarButton.setToolTipText("Limpar (Ctrl + L)");
        clearToolBarButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        clearToolBarButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        clearToolBarButton.setEnabled(false);
        clearToolBarButton.setFocusable(false);
        clearToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearToolBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearToolBarButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(clearToolBarButton);

        addWordToolBarButton.setBackground(new java.awt.Color(204, 204, 204));
        addWordToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/plus_toolbar.png"))); // NOI18N
        addWordToolBarButton.setToolTipText("Adicionar Sinal (Ctrl + D)");
        addWordToolBarButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        addWordToolBarButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addWordToolBarButton.setFocusTraversalPolicyProvider(true);
        addWordToolBarButton.setFocusable(false);
        addWordToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addWordToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addWordToolBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWordToolBarButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(addWordToolBarButton);

        addNWordsToolBarButton.setBackground(new java.awt.Color(204, 204, 204));
        addNWordsToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/plus22.png"))); // NOI18N
        addNWordsToolBarButton.setToolTipText("Adicionar Sinal em Lote (Ctrl + D)");
        addNWordsToolBarButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        addNWordsToolBarButton.setFocusable(false);
        addNWordsToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addNWordsToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addNWordsToolBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNWordsToolBarButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(addNWordsToolBarButton);

        delWordToolBarButton.setBackground(new java.awt.Color(204, 204, 204));
        delWordToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/less.png"))); // NOI18N
        delWordToolBarButton.setToolTipText("Adicionar Sinal (Ctrl + D)");
        delWordToolBarButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        delWordToolBarButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        delWordToolBarButton.setFocusTraversalPolicyProvider(true);
        delWordToolBarButton.setFocusable(false);
        delWordToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        delWordToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        delWordToolBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delWordToolBarButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(delWordToolBarButton);

        jSeparator2.setSeparatorSize(new java.awt.Dimension(10, 30));
        jToolBar1.add(jSeparator2);

        jSeparator4.setSeparatorSize(new java.awt.Dimension(10, 30));
        jToolBar1.add(jSeparator4);

        jSeparator5.setSeparatorSize(new java.awt.Dimension(10, 30));
        jToolBar1.add(jSeparator5);

        undoToolBarButton.setBackground(new java.awt.Color(204, 204, 204));
        undoToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/Undo_toolbar.png"))); // NOI18N
        undoToolBarButton.setToolTipText("Desfazer (Ctrl + Z)");
        undoToolBarButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        undoToolBarButton.setEnabled(false);
        undoToolBarButton.setFocusable(false);
        undoToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        undoToolBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoToolBarButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(undoToolBarButton);

        redoToolBarButton.setBackground(new java.awt.Color(204, 204, 204));
        redoToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/Redo_toolbar.png"))); // NOI18N
        redoToolBarButton.setToolTipText("Refazer (Ctrl + Y)");
        redoToolBarButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        redoToolBarButton.setEnabled(false);
        redoToolBarButton.setFocusable(false);
        redoToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        redoToolBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoToolBarButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(redoToolBarButton);

        manualToolBarButton.setBackground(new java.awt.Color(204, 204, 204));
        manualToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/manual_toolbar.png"))); // NOI18N
        manualToolBarButton.setToolTipText("Exibir Ajuda (F1)");
        manualToolBarButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        manualToolBarButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        manualToolBarButton.setFocusable(false);
        manualToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        manualToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        manualToolBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualToolBarButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(manualToolBarButton);

        aboutToolBarButton.setBackground(new java.awt.Color(204, 204, 204));
        aboutToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/about_toolbar.png"))); // NOI18N
        aboutToolBarButton.setToolTipText("Sobre o e-Sinais");
        aboutToolBarButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        aboutToolBarButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        aboutToolBarButton.setFocusable(false);
        aboutToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        aboutToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        aboutToolBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutToolBarButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(aboutToolBarButton);

        exitToolBarButton.setBackground(new java.awt.Color(204, 204, 204));
        exitToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/exit_toolbar.png"))); // NOI18N
        exitToolBarButton.setToolTipText("Sair");
        exitToolBarButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        exitToolBarButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        exitToolBarButton.setFocusable(false);
        exitToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exitToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        exitToolBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitToolBarButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(exitToolBarButton);

        jToolBar2.setBackground(new java.awt.Color(204, 204, 204));
        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar2.setRollover(true);
        jToolBar2.add(jSeparator6);

        decreaseFontButton.setBackground(new java.awt.Color(204, 204, 204));
        decreaseFontButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/fontminus.png"))); // NOI18N
        decreaseFontButton.setToolTipText("Diminuir Fonte (Ctrl + Shift + <)");
        decreaseFontButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        decreaseFontButton.setFocusable(false);
        decreaseFontButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        decreaseFontButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        decreaseFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decreaseFontButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(decreaseFontButton);

        defaultFontButton.setBackground(new java.awt.Color(204, 204, 204));
        defaultFontButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/font.png"))); // NOI18N
        defaultFontButton.setToolTipText("Tamanho Padrão (Ctrl + Espaço)");
        defaultFontButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        defaultFontButton.setFocusable(false);
        defaultFontButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        defaultFontButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        defaultFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultFontButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(defaultFontButton);

        increaseFontButton.setBackground(new java.awt.Color(204, 204, 204));
        increaseFontButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/fontplus.png"))); // NOI18N
        increaseFontButton.setToolTipText("Aumentar fonte (Ctrl + Shift + >)");
        increaseFontButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        increaseFontButton.setFocusable(false);
        increaseFontButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        increaseFontButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        increaseFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                increaseFontButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(increaseFontButton);

        wordsLabel.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        wordsLabel.setText("0 palavra(s)");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(translateButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(wordsLabel))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1144, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(translateButton)
                    .addComponent(wordsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        fileMenu.setMnemonic('A');
        fileMenu.setText("Arquivo");

        exportMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/export.png"))); // NOI18N
        exportMenuItem.setText("Exportar para PDF");
        exportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exportMenuItem);
        fileMenu.add(jSeparator8);

        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/exit.png"))); // NOI18N
        exitMenuItem.setText("Sair");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        jMenuBar1.add(fileMenu);

        editMenu.setMnemonic('E');
        editMenu.setText("Editar");

        undoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        undoMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/Undo.png"))); // NOI18N
        undoMenuItem.setMnemonic('D');
        undoMenuItem.setText("Desfazer");
        undoMenuItem.setEnabled(false);
        undoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(undoMenuItem);

        redoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        redoMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/Redo.png"))); // NOI18N
        redoMenuItem.setMnemonic('R');
        redoMenuItem.setText("Refazer");
        redoMenuItem.setEnabled(false);
        redoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(redoMenuItem);
        editMenu.add(jSeparator9);

        FontMenu.setMnemonic('T');
        FontMenu.setText("Tamanho da Fonte");
        FontMenu.setToolTipText("F");

        decreaseFontMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_COMMA, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        decreaseFontMenuItem.setMnemonic('D');
        decreaseFontMenuItem.setText("Diminuir Fonte");
        decreaseFontMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decreaseFontMenuItemActionPerformed(evt);
            }
        });
        FontMenu.add(decreaseFontMenuItem);

        defaultFontMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, java.awt.event.InputEvent.CTRL_MASK));
        defaultFontMenuItem.setMnemonic('T');
        defaultFontMenuItem.setText("Tamanho Padrão");
        defaultFontMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultFontMenuItemActionPerformed(evt);
            }
        });
        FontMenu.add(defaultFontMenuItem);

        increaseFontMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PERIOD, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        increaseFontMenuItem.setMnemonic('A');
        increaseFontMenuItem.setText("Aumentar Fonte");
        increaseFontMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                increaseFontMenuItemActionPerformed(evt);
            }
        });
        FontMenu.add(increaseFontMenuItem);

        editMenu.add(FontMenu);
        editMenu.add(jSeparator10);

        clearMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        clearMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/clear.png"))); // NOI18N
        clearMenuItem.setMnemonic('i');
        clearMenuItem.setText("Limpar");
        clearMenuItem.setEnabled(false);
        clearMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(clearMenuItem);

        jMenuBar1.add(editMenu);

        toolsMenu.setMnemonic('F');
        toolsMenu.setText("Ferramentas");

        translateMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        translateMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/double_arrow.png"))); // NOI18N
        translateMenuItem.setMnemonic('T');
        translateMenuItem.setText("Traduzir");
        translateMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                translateMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(translateMenuItem);
        toolsMenu.add(jSeparator3);

        addWordMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        addWordMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/plus.png"))); // NOI18N
        addWordMenuItem.setMnemonic('A');
        addWordMenuItem.setText("Adicionar Sinal");
        addWordMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWordMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(addWordMenuItem);

        jMenuBar1.add(toolsMenu);

        helpMenu.setMnemonic('u');
        helpMenu.setText("Ajuda");

        manualMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/manual.gif"))); // NOI18N
        manualMenuItem.setMnemonic('E');
        manualMenuItem.setText("Exibir Ajuda");
        manualMenuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                manualMenuItemMouseClicked(evt);
            }
        });
        manualMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(manualMenuItem);

        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        aboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/about.gif"))); // NOI18N
        aboutMenuItem.setMnemonic('S');
        aboutMenuItem.setText("Sobre o e-Sinais");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void manualMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualMenuItemActionPerformed
        manual();
    }//GEN-LAST:event_manualMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        about();
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void translateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_translateMenuItemActionPerformed
        translate();
    }//GEN-LAST:event_translateMenuItemActionPerformed

    private void addWordMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addWordMenuItemActionPerformed
        AddSign(-1);
    }//GEN-LAST:event_addWordMenuItemActionPerformed

    private void undoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoMenuItemActionPerformed
        undo();
    }//GEN-LAST:event_undoMenuItemActionPerformed

    private void redoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoMenuItemActionPerformed
        redo();
    }//GEN-LAST:event_redoMenuItemActionPerformed

    private void decreaseFontMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decreaseFontMenuItemActionPerformed
        decreaseFont();
    }//GEN-LAST:event_decreaseFontMenuItemActionPerformed

    private void defaultFontMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultFontMenuItemActionPerformed
        defaultFont();
    }//GEN-LAST:event_defaultFontMenuItemActionPerformed

    private void increaseFontMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_increaseFontMenuItemActionPerformed
        increaseFont();
    }//GEN-LAST:event_increaseFontMenuItemActionPerformed

    private void clearMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMenuItemActionPerformed
        clear();
    }//GEN-LAST:event_clearMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        close();
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void jTextArea1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextArea1FocusGained
        if (!firstfocusdone) {
            jTextArea1.setText("");
            firstfocusdone = true;
        }
    }//GEN-LAST:event_jTextArea1FocusGained

    private void translateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_translateButtonActionPerformed
        translate();
    }//GEN-LAST:event_translateButtonActionPerformed

    private void cutMenuItemPopupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItemPopupActionPerformed
        jTextArea1.cut();
    }//GEN-LAST:event_cutMenuItemPopupActionPerformed

    private void copyMenuItemPopupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMenuItemPopupActionPerformed
        jTextArea1.copy();
    }//GEN-LAST:event_copyMenuItemPopupActionPerformed

    private void pasteMenuItemPopupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMenuItemPopupActionPerformed
        jTextArea1.paste();
    }//GEN-LAST:event_pasteMenuItemPopupActionPerformed

    private void addSignalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSignalMenuItemActionPerformed
        AddSign(idAtual);
    }//GEN-LAST:event_addSignalMenuItemActionPerformed

    private void saveImageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveImageMenuItemActionPerformed
        try {
            save(idAtual);
        } catch (IOException ex) {
            Logger.getLogger(ESinais.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_saveImageMenuItemActionPerformed

    private void jTextArea1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextArea1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (evt.isShiftDown()) {
                jTextArea1.setText(jTextArea1.getText() + "\n");
            } else {
                translate();
                evt.consume();
            }
        }
    }//GEN-LAST:event_jTextArea1KeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        close();
    }//GEN-LAST:event_formWindowClosing

    private void addWordToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addWordToolBarButtonActionPerformed
        AddSign(-1);
    }//GEN-LAST:event_addWordToolBarButtonActionPerformed

    private void undoToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoToolBarButtonActionPerformed
        undo();
    }//GEN-LAST:event_undoToolBarButtonActionPerformed

    private void redoToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoToolBarButtonActionPerformed
        redo();
    }//GEN-LAST:event_redoToolBarButtonActionPerformed

    private void translateToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_translateToolBarButtonActionPerformed
        translate();
    }//GEN-LAST:event_translateToolBarButtonActionPerformed

    private void clearToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearToolBarButtonActionPerformed
        clear();
    }//GEN-LAST:event_clearToolBarButtonActionPerformed

    private void manualToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualToolBarButtonActionPerformed
        manual();
    }//GEN-LAST:event_manualToolBarButtonActionPerformed

    private void aboutToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutToolBarButtonActionPerformed
        about();
    }//GEN-LAST:event_aboutToolBarButtonActionPerformed

    private void exitToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitToolBarButtonActionPerformed
        close();
    }//GEN-LAST:event_exitToolBarButtonActionPerformed

    private void decreaseFontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decreaseFontButtonActionPerformed
        decreaseFont();
    }//GEN-LAST:event_decreaseFontButtonActionPerformed

    private void defaultFontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultFontButtonActionPerformed
        defaultFont();
    }//GEN-LAST:event_defaultFontButtonActionPerformed

    private void increaseFontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_increaseFontButtonActionPerformed
        increaseFont();
    }//GEN-LAST:event_increaseFontButtonActionPerformed

    private void exportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportMenuItemActionPerformed
        export();
    }//GEN-LAST:event_exportMenuItemActionPerformed

    private void showImageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showImageMenuItemActionPerformed
        try {
            ShowImage(idAtual);
        } catch (SQLException ex) {
            Logger.getLogger(ESinais.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_showImageMenuItemActionPerformed

    private void manualMenuItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_manualMenuItemMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_manualMenuItemMouseClicked

    private void addNWordsToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNWordsToolBarButtonActionPerformed
        AddBathSign();
    }//GEN-LAST:event_addNWordsToolBarButtonActionPerformed

    private void delWordToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delWordToolBarButtonActionPerformed
        DelSign.getInstance(this, conexao).setVisible(true);
    }//GEN-LAST:event_delWordToolBarButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Metal look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ESinais.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ESinais.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ESinais.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ESinais.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ESinais().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu FontMenu;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton aboutToolBarButton;
    private javax.swing.JButton addNWordsToolBarButton;
    private javax.swing.JMenuItem addSignalMenuItem;
    private javax.swing.JMenuItem addWordMenuItem;
    private javax.swing.JButton addWordToolBarButton;
    private javax.swing.JMenuItem clearMenuItem;
    private javax.swing.JButton clearToolBarButton;
    private javax.swing.JMenuItem copyMenuItemPopup;
    private javax.swing.JMenuItem cutMenuItemPopup;
    private javax.swing.JButton decreaseFontButton;
    private javax.swing.JMenuItem decreaseFontMenuItem;
    private javax.swing.JButton defaultFontButton;
    private javax.swing.JMenuItem defaultFontMenuItem;
    private javax.swing.JButton delWordToolBarButton;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JButton exitToolBarButton;
    private javax.swing.JMenuItem exportMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPopupMenu imagePopupMenu;
    private javax.swing.JButton increaseFontButton;
    private javax.swing.JMenuItem increaseFontMenuItem;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JMenuItem manualMenuItem;
    private javax.swing.JButton manualToolBarButton;
    private javax.swing.JMenuItem pasteMenuItemPopup;
    private javax.swing.JMenuItem redoMenuItem;
    private javax.swing.JButton redoToolBarButton;
    private javax.swing.JMenuItem saveImageMenuItem;
    private javax.swing.JMenuItem showImageMenuItem;
    private javax.swing.JPopupMenu textPopupMenu;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JButton translateButton;
    private javax.swing.JMenuItem translateMenuItem;
    private javax.swing.JButton translateToolBarButton;
    private javax.swing.JMenuItem undoMenuItem;
    private javax.swing.JButton undoToolBarButton;
    private javax.swing.JLabel wordsLabel;
    // End of variables declaration//GEN-END:variables

}
