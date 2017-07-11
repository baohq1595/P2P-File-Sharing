/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tracker;

import Common.FileInfo;
import Common.HashTable;
import Common.PeerInfo;
import Common.Sleep;
import Common.Tag;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.swing.JFrame;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
/**
 *
 * @author Mrkeys
 */
public class SeedOnline extends JFrame {
    private Sleep sleeper;
    /**
     * Creates new form SeedOnline
     */
    public SeedOnline() {
        initComponents();
        _hTable = new ArrayList<HashTable>();
        //_hTable = null;
        sleeper = new Sleep(200);
        try{
            File f = new File (Tag.HashFile);
            f.delete();
            f = new File (Tag.FileList);
            f.delete();
            sleeper.init();
            f = new File (Tag.HashFile);
            f.createNewFile();
            f = new File (Tag.FileList);
            f.createNewFile();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        //setText("tim");
        //Update();
    }
public void setText(String s){
    txtBox.setText(s);
    
}
public boolean Update(){    //System.out.println("Update");
//       ArrayList<HashTable> tempHash = new ArrayList<HashTable>();
//       try{
//                  // System.out.println("isRunDisplaySeed 1111");
//		   FileInputStream fin = new FileInputStream(Tag.HashFile);
//                   if (fin.available() == 0) return false;
//		   ObjectInputStream ois = new ObjectInputStream(fin);
//                  // if (ois.available() == 0) return false;
//		   tempHash = (ArrayList<HashTable>) ois.readObject();  
//                   boolean flag;
//                   flag = false;
//                   if (tempHash.size() == _hTable.size()) {
//                       for (int i = 0;i<tempHash.size();i++){
//                            if (tempHash.get(i).peerList.size() != _hTable.get(i).peerList.size()){
//                                flag = true;
//                                _hTable = tempHash;
//                                fin = new FileInputStream(Tag.FileList);
//                                ois = new ObjectInputStream(fin);
//                                this._fInfo = (ArrayList<FileInfo>) ois.readObject();
//                               // System.out.println("Hash Table "+_hTable.get(1).getHashValue());
//                                ois.close();	  
//                                break;
//                            }
//                       }
//                    return flag;
//                   }
//                   else {
//                   _hTable = tempHash;
            try{
                    File f = new File (Tag.HashFile);
                    if (f.length() == leng_old){
                        return false;
                    }
                    leng_old = f.length();
                    FileInputStream fin = new FileInputStream(Tag.HashFile);
                    ObjectInputStream ois = new ObjectInputStream(fin);
                    this._hTable = (ArrayList<HashTable>) ois.readObject();
                   fin = new FileInputStream(Tag.FileList);
		   ois = new ObjectInputStream(fin);
		   this._fInfo = (ArrayList<FileInfo>) ois.readObject();
                  // System.out.println("Hash Table "+_hTable.get(1).getHashValue());
		   ois.close();	                  
	   }catch(Exception ex){
		   ex.printStackTrace();
                   System.out.println("Error while read hashtable");
                  return false;
	   } 
       return true;
       
    } 
public void display(){
        if (_hTable == null) {
            GUI.setText("Null");
            return;
        }
        int num;
        int sum;
        sum = 0;
        for (HashTable hTable:_hTable){
            if (hTable == null) break;
            GUI.txtBox.append("Hash File: " +hTable.getHashValue()+"\n");    
            num = 0;
           // System.out.println("Hash "+hTable.getHashValue());
           for (FileInfo f : _fInfo){
               if (f.getHash().equals(hTable.getHashValue())){
                   GUI.txtBox.append(" File Name "+f.getName()+"\n");
                   break;
               }
           }
            for (PeerInfo pInfor:hTable.getPeerList()){
                if (pInfor == null) break;
                num++;
                GUI.txtBox.append("  - ID "+pInfor.getPeerID()+"\n");
                GUI.txtBox.append("\t + IP "+pInfor.getIpAddr()+"\n");
                GUI.txtBox.append("\t + Port "+pInfor.getPort()+"\n");          
                GUI.txtBox.append("\t + Alive "+pInfor.getAlive()+"\n");
                //System.out.println("PeerInfo "+pInfor.getIpAddr());
            }
            sum+=num;
            GUI.txtBox.append("This File: "+num+" Online \n");
            GUI.txtBox.append("_____________________________________________\n \n");
        }
        GUI.txtBox.append("Total: "+sum+" Online");
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtBox = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtBox.setColumns(20);
        txtBox.setRows(5);
        txtBox.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                txtBoxMouseMoved(evt);
            }
        });
        txtBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtBoxMouseClicked(evt);
            }
        });
        txtBox.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                txtBoxComponentShown(evt);
            }
        });
        jScrollPane1.setViewportView(txtBox);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBoxComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_txtBoxComponentShown
        // TODO add your handling code here:
       
    }//GEN-LAST:event_txtBoxComponentShown

    private void txtBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBoxMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_txtBoxMouseClicked

    private void txtBoxMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBoxMouseMoved
        // TODO add your handling code here:
//        txtBox.setText("");
//         Update();
//         display();
        
         
    }//GEN-LAST:event_txtBoxMouseMoved
    /**
     * @param args the command line arguments
     */
    public  void Interfaces() {
        GUI = new SeedOnline();
        GUI.setVisible(true);
        tasknew = new Timer();
      tasknew.scheduleAtFixedRate(new SeedOnline.remind(), 300, 300);
      leng_old = 0;
    }
    class  remind extends TimerTask {
	public void run() {            
            if (Update()){
                GUI.txtBox.setText("");
                System.out.println("Update GUi");
                display();
                }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtBox;
    // End of variables declaration//GEN-END:variables
    private ArrayList<HashTable> _hTable;
    private ArrayList<FileInfo> _fInfo;
    private JFrame frmBstapp;
    private static SeedOnline GUI;
    private static Timer tasknew;
    private static long leng_old;
}
