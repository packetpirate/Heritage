package heritage;

import heritage.tree.Tree;
import heritage.tree.node.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class MainFrame extends javax.swing.JFrame {
    public MainFrame() {
        initComponents();
        
        familyTree = new Tree<>();
        familyTree.getCurrent().setData(new Person("You"));
        ancestorList.setListData(new Object[]{familyTree.getCurrent().getData().getName()});
        
        try {
            nationalities = populateNationalities();
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            System.exit(1);
        }
        
        addAncestorButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addAncestor();
                }
            }
        );
        
        deleteAncestorButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteAncestor();
                }   
            }
        );
        
        showChildButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showChild();
                }
            }
        );
        
        showFatherButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showFather();
                }
            }
        );
        
        showMotherButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showMother();
                }
            }        
        );
        
        addNationalityButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addNationality();
                }
            }
        );
        
        deleteNationalityButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteNationality();
                }
            }
        );
        
        calculateHeritageButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    calculateHeritage();
                }
            }
        );
    }
    
    private void addAncestor() {
        Object[] options = {"Father", "Mother"};
        int parent = JOptionPane.showOptionDialog(this, 
                                                  "Is it your mother or father?",
                                                  "Which parent?", 
                                                  JOptionPane.YES_NO_OPTION, 
                                                  JOptionPane.QUESTION_MESSAGE, 
                                                  null, 
                                                  options,
                                                  options[0]);
        if((parent == JOptionPane.YES_OPTION)&&
           (familyTree.getCurrent().getLeft() != null)) {
            JOptionPane.showMessageDialog(this, ("A Father is already on record.\n" + "Overwriting this ancestor could cause problems."));
            return;
        }
        if((parent == JOptionPane.NO_OPTION)&&
           (familyTree.getCurrent().getRight() != null)) {
            JOptionPane.showMessageDialog(this, ("A Mother is already on record.\n" + "Overwriting this ancestor could cause problems."));
            return;
        }
        String name = (String) JOptionPane.showInputDialog("Ancestor\'s Name");
        familyTree.insert(new Person(name), 
                          ((parent == JOptionPane.YES_OPTION)?
                                      Tree.Parent.LEFT:
                                      Tree.Parent.RIGHT));
    }
    
    private void deleteAncestor() {
        Object[] options = {"Father", "Mother"};
        int parent = JOptionPane.showOptionDialog(this, 
                                                  "Delete which parent?",
                                                  "Which parent?", 
                                                  JOptionPane.YES_NO_OPTION, 
                                                  JOptionPane.QUESTION_MESSAGE, 
                                                  null, 
                                                  options,
                                                  options[0]);
        if(parent == JOptionPane.YES_OPTION) {
            if(familyTree.getCurrent().getLeft() != null) {
                familyTree.getCurrent().setLeft(null);
            } else {
                JOptionPane.showMessageDialog(this, "No Father on Record!");
            }
        } else {
            if(familyTree.getCurrent().getRight() != null) {
                familyTree.getCurrent().setRight(null);
            } else {
                JOptionPane.showMessageDialog(this, "No Mother on Record!");
            }
        }
    }
    
    private void showChild() {
        if(familyTree.getCurrent().getChild() != null) {
            familyTree.setCurrent(familyTree.getCurrent().getChild());
            updateLists();
        }
    }
    
    private void showFather() {
        if(familyTree.getCurrent().getLeft() != null) {
            familyTree.setCurrent(Tree.Parent.LEFT);
            updateLists();
        } else {
            JOptionPane.showMessageDialog(this, "No Father on Record!");
        }
    }
    
    private void showMother() {
        if(familyTree.getCurrent().getRight() != null) {
            familyTree.setCurrent(Tree.Parent.RIGHT);
            updateLists();
        } else {
            JOptionPane.showMessageDialog(this, "No Mother on Record!");
        }
    }
    
    private void addNationality() {
        if(nationalities != null) {
            String[] demonyms = nationalities.toArray(new String[]{});
            String nationalityChoice = (String) JOptionPane.showInputDialog(this,
                                                                            "Choose a nationality.",
                                                                            "Nationalities",
                                                                            JOptionPane.INFORMATION_MESSAGE,
                                                                            null,
                                                                            demonyms,
                                                                            demonyms[0]);
            familyTree.getCurrent().getData().addNationality(nationalityChoice);
            updateLists();
        } else {
            System.err.println("Nationalities list could not be loaded.");
            System.exit(1);
        }
    }
    
    private void deleteNationality() {
        String selected = nationalitiesList.getSelectedValue().toString();
        familyTree.getCurrent().getData().removeNationality(selected);
        updateLists();
    }
    
    private void calculateHeritage() {
        // Create a list with everyone's nationalities. Duplicates required. Do not change list type. A set will give incorrect results.
        List<String> familyNationalities = traverseTree(familyTree.getRoot(), (new ArrayList<String>()));
        
        // Create a HashMap containing unique nationality values and the number of times they occur.
        HashMap uniqueNationalities = new HashMap();
        for(String s : familyNationalities) {
            // If the nationality is already in the HashMap, increment its count by 1.
            if(uniqueNationalities.containsKey(s)) uniqueNationalities.put(s, (new Integer((Integer)uniqueNationalities.get(s)+1)));
            else uniqueNationalities.put(s, new Integer(1)); // If not, add it with the default value of 1.
        }
        
        // Create a Set containing the values of the HashMap.
        Set uniqueNationalitiesSet = uniqueNationalities.entrySet();
        // Create an iterator of the Set.
        Iterator i = uniqueNationalitiesSet.iterator();
        // The total number of nationalities in the tree. Required to compute percentage of each nationality.
        int totalNationalityOccurances = 0;
        
        // Iterate through the HashMap, counting the total number of nationalities.
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            totalNationalityOccurances += ((Integer)me.getValue()).intValue();
        }
        
        // Iterate through the HashMap again, this time calculating the percentage of each nationality and adding it to the JList.
        i = uniqueNationalitiesSet.iterator(); // Reset the iterator.
        List<String> messages = new ArrayList<>(); // List containing the messages to add to the JList.
        messages.add("Your Heritage");
        messages.add("-------------------");
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            double currentNationalityPercent = ((Double.parseDouble(me.getValue().toString())) / (double)totalNationalityOccurances); // Values should be between 0 and 1.
            messages.add(me.getKey() + " - " + (currentNationalityPercent * 100) + "%"); // Create a message in the messages list to add to the JList.
        }
        resultsList.setListData(messages.toArray());
    }
    
    private ArrayList<String> traverseTree(Node<Person> current, ArrayList<String> nationalitiesList) {
        // Get nationalities from Father's side of the family tree.
        if(current.getLeft() != null) { // If the current node has a Father.
            nationalitiesList.addAll(traverseTree(current.getLeft(), nationalitiesList)); // Do the same for the Father.
        } else {
            return new ArrayList<>(current.getData().getNationalities()); // Return the Father's nationalities.
        }
        
        // Get nationalities from Mother's side of the family tree.
        if(current.getRight() != null) { // If the current node has a Mother.
            nationalitiesList.addAll(traverseTree(current.getRight(), nationalitiesList)); // Do the same for the Mother.
        } else {
            return new ArrayList<>(current.getData().getNationalities()); // Return the Mother's nationalities.
        }
        
        return nationalitiesList; // Return the current list containing the Mother and Father's nationalities.
    }
    
    private void updateLists() {
        Object[] currentName = {familyTree.getCurrent().getData().getName()};
        ancestorList.setListData(currentName);
        String[] currentNationalities = familyTree.getCurrent().getData().getNationalities().toArray(new String[]{});
        nationalitiesList.setListData(currentNationalities);
    }
    
    private Set<String> populateNationalities() throws FileNotFoundException {
        File inFile = new File("demonyms.txt");
        String line = "";
        Set<String> demonyms = new HashSet<>();
                
        BufferedReader reader = new BufferedReader(new FileReader(inFile));
        
        try {
            while((line = reader.readLine()) != null) {
                demonyms.add(line);
            }
            
            System.out.println("Nationalities file read. Printing nationalities now.");
            System.out.println("Nationalities: " + demonyms);
            
            reader.close();
        } catch(IOException io) {
            System.err.println(io);
            System.exit(1);
        }
        
        return demonyms;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ancestorList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        nationalitiesList = new javax.swing.JList();
        ancestorLabel = new javax.swing.JLabel();
        nationalitiesLabel = new javax.swing.JLabel();
        addAncestorButton = new javax.swing.JButton();
        deleteAncestorButton = new javax.swing.JButton();
        addNationalityButton = new javax.swing.JButton();
        deleteNationalityButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        resultsList = new javax.swing.JList();
        resultsLabel = new javax.swing.JLabel();
        showChildButton = new javax.swing.JButton();
        calculateHeritageButton = new javax.swing.JButton();
        showFatherButton = new javax.swing.JButton();
        showMotherButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Heritage");
        setResizable(false);

        titleLabel.setFont(new java.awt.Font("Agency FB", 0, 36)); // NOI18N
        titleLabel.setText("Find Your Heritage");

        jScrollPane1.setViewportView(ancestorList);

        jScrollPane2.setViewportView(nationalitiesList);

        ancestorLabel.setText("Ancestor");

        nationalitiesLabel.setText("Nationalities");

        addAncestorButton.setText("Add");

        deleteAncestorButton.setText("Delete");

        addNationalityButton.setText("Add");

        deleteNationalityButton.setText("Delete");

        jScrollPane3.setViewportView(resultsList);

        resultsLabel.setText("Results");

        showChildButton.setText("Child");

        calculateHeritageButton.setText("Calculate Heritage");

        showFatherButton.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        showFatherButton.setText("F");

        showMotherButton.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        showMotherButton.setText("M");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(34, 34, 34))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(resultsLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addComponent(addAncestorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(deleteAncestorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addComponent(showChildButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(showFatherButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(showMotherButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(ancestorLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(nationalitiesLabel)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(addNationalityButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(deleteNationalityButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(calculateHeritageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ancestorLabel)
                    .addComponent(nationalitiesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addAncestorButton)
                    .addComponent(deleteAncestorButton)
                    .addComponent(addNationalityButton)
                    .addComponent(deleteNationalityButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(showChildButton)
                            .addComponent(calculateHeritageButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resultsLabel))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(showFatherButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(showMotherButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /*
//         * Set the Nimbus look and feel
//         */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /*
//         * If Nimbus (introduced in Java SE 6) is not available, stay with the
//         * default look and feel. For details see
//         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /*
//         * Create and display the form
//         */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
//                new MainFrame().setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAncestorButton;
    private javax.swing.JButton addNationalityButton;
    private javax.swing.JLabel ancestorLabel;
    private javax.swing.JList ancestorList;
    private javax.swing.JButton calculateHeritageButton;
    private javax.swing.JButton deleteAncestorButton;
    private javax.swing.JButton deleteNationalityButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel nationalitiesLabel;
    private javax.swing.JList nationalitiesList;
    private javax.swing.JLabel resultsLabel;
    private javax.swing.JList resultsList;
    private javax.swing.JButton showChildButton;
    private javax.swing.JButton showFatherButton;
    private javax.swing.JButton showMotherButton;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
    private Tree<Person> familyTree;
    private Set<String> nationalities;
}
