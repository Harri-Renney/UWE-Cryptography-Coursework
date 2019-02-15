/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptography.rainbow.cracker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Harri Renney
 */
public class MainForm extends javax.swing.JFrame {
    private char[] alphabet = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
    private HashMap rainbowTable = new HashMap();
    private int chainLength = 5000;
    private int passwordLength = 9;
    private int sizeOfPasswordSpace;
    private int rainbowTableSize;
    BigInteger primeModulus;
    
    //Takes a byte array as message digest givees and converts to string of hex represenation.
    private static String convertToHex(byte[] data) 
    { 
        StringBuffer buf = new StringBuffer(); 
        for (int i = 0; i < data.length; i++) { 
                int halfbyte = (data[i] >>> 4) & 0x0F; 
                int two_halfs = 0; 
                do { 
                    if ((0 <= halfbyte) && (halfbyte <= 9)) 
                        buf.append((char) ('0' + halfbyte)); 
                    else 
                        buf.append((char) ('a' + (halfbyte - 10))); 
                    halfbyte = data[i] & 0x0F; 
                } while(two_halfs++ < 1); 
        } 
        return buf.toString(); 
    } 
    
    //Produces SHA-1 hash of text given.
    public static String SHA1(String text)  
    throws NoSuchAlgorithmException, UnsupportedEncodingException  
    { 
	MessageDigest md; 
	md = MessageDigest.getInstance("SHA-1"); //Get SHA-1 as instance for message digest.
	byte[] sha1hash = new byte[40]; 
	md.update(text.getBytes("iso-8859-1"), 0, text.length()); //Update message digest with the text.
	sha1hash = md.digest();
	return convertToHex(sha1hash); //Convert the byte array to string of hex values.
    }
    
    //Converts the integer of hashed value into a password.
    public String intToPassword(int n)
    {
        int base = alphabet.length;
        int r;
        String ret = "";
        while(n >= 0)
        {
            r = n % base;
            n = n / base;
            ret = alphabet[r] + ret;
            n = n - 1;  //Take 1 each time to stop 1 to 1 mappings.
        }
        return ret;
    }
    
    //Reduce function converts hashed value into some plain text.
    public String reduce(String hash, int pos)
    {
       //Take hash string and convert it to big integer.
       BigInteger bigNumber = new BigInteger(Integer.toString(hash.hashCode()));
       //Add the position in chain to avoid collisions.
       bigNumber = bigNumber.add(new BigInteger(Integer.toString(pos))); 
       //Mod value by prime greater than search space.
       int n = bigNumber.mod(primeModulus).intValue();       
       return intToPassword(n); 
    }
    
    //Chain reduce that works along the chain applying reduction and ahshing until it reaches the end.
    public String chainReduce(String hash, int pos)
    {
        String password = reduce(hash, pos);        
        try {
     
            while(pos != chainLength-1)
            {
                password = SHA1(password);
                ++pos;
                password = reduce(password, pos);
            }
        
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }      
        return password;
    }
    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();
        
        ////////////////////////////////////////
        //Initalize ready for rainbow cracking//
        ////////////////////////////////////////
        
        //Work out size of search space.
        BigInteger sizeOfPasswordSpaceBI = new BigInteger("0");
        for(int i = 0; i != passwordLength; ++i)
        {
            BigInteger value = new BigInteger(Integer.toString(alphabet.length));
            int exponent = i;
            sizeOfPasswordSpaceBI = sizeOfPasswordSpaceBI.add(value.pow(exponent));
        }
        sizeOfPasswordSpace = sizeOfPasswordSpaceBI.intValue();
        rainbowTableSize = (int)((sizeOfPasswordSpace / chainLength) * 1.5);
        
        //Work out the next greatest prime above search space.
        primeModulus = new BigInteger(Integer.toString(sizeOfPasswordSpace));
        while(!primeModulus.isProbablePrime(100))
            {primeModulus = primeModulus.add(new BigInteger("1"));}
        
        //Open the rainbow table file and read into hash map.
        rainbowTable = null;
        try {
           FileInputStream fileIn = new FileInputStream("RainbowTable.map");
           ObjectInputStream in = new ObjectInputStream(fileIn);
           rainbowTable = (HashMap) in.readObject();
           in.close();
           fileIn.close();
        } catch (IOException i) {
           i.printStackTrace();
           return;
        } catch (ClassNotFoundException c) {
           System.out.println("Employee class not found");
           c.printStackTrace();
           return;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Crack Password");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField2)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(jTextField1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //////////////////////////////////////////
    //Button Cracks Hash using Rainbow Table//
    //////////////////////////////////////////
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            
            boolean plaintextFound = false;
            String initialHashCode = jTextField1.getText();
            
            if(initialHashCode.length() == 40)
            {
                //Starting from end of chain, iterates through incresing how far up it starts until reaches the first node.
                for(int i = 0; i != chainLength; ++i)
                {
                    String previousString = "";
                    String lastChainCode = chainReduce(initialHashCode, (chainLength-1) - i);

                    //If a value found in has map from the tested key.
                    String rainbowKey = (String)rainbowTable.get(lastChainCode);
                    if(rainbowKey != null)
                    {
                        String reducedCode = rainbowKey;
                        String hashCode;
                        //Move through from beginning of chain.
                        for(int j = 0; j != chainLength; ++j)
                        {
                            previousString = reducedCode;
                            hashCode = SHA1(reducedCode);
                            reducedCode = reduce(hashCode, j);
                            //Then if it is found when moving through chain from beginning.
                            if(hashCode.equals(initialHashCode))
                            {
                                jTextField2.setText(previousString);
                                break;

                            }
                        }
                        if( jTextField2.getText().equals(previousString))
                        {   plaintextFound = true; break;   }            
                    }
                }

                //If not found then inform.
                if(!plaintextFound)
                    jTextField2.setText("Could not find hash in current rainbow table.");
            }
            else
                jTextField2.setText("Invaid hash value. Should be length of 40 characters.");
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}