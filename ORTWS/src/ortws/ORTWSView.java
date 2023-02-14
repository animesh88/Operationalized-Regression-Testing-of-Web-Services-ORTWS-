/*
 * ORTWSView.java
 */
package ortws;

import CodeBreak.SeperateOperations;
import CodeBreak.VisitingClassMethod.MethodVisitor;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.*;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.io.FileInputStream;
import javax.swing.Timer;
import javax.swing.JDialog;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import ConstructDifferenceWSDL.Construction;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.qarks.util.Cancellable;
import com.qarks.util.CancellableImpl;
import com.qarks.util.files.diff.DirContentStatus;
import com.qarks.util.files.diff.DirDiffResult;
import com.qarks.util.files.diff.DirContentStatus.Status;
import com.qarks.util.files.diff.core.FolderComparator;
import com.qarks.util.files.diff.ui.DiffProvider;
import com.qarks.util.files.diff.ui.FileDiffDialog;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import javax.swing.DefaultListModel;
import ConstructRRTS.ParsingTestSuite;
import ConstructRRTS.TestCases;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * The application's main frame.
 */
public class ORTWSView extends FrameView {

    private SAXTreeBuilder saxTree2 = null;
    private SAXTreeBuilder saxTree3 = null;
    private File WSDL1;
    private File WSDL2;
    File WSDL3;
    File WSDL4, WSDL5;
    File testSuiteFile1 = null, testSuiteFile2 = null, testSuiteFile3 = null, testSuiteFile4 = null, testSuiteFile5 = null;
    Object X;
    String diffOp[] = new String[100];
    String reduceOp[] = new String[100];
    String combinedOp[] = new String[100];
    List<String> combinedUniqueOp = new ArrayList<String>();
    String inputList2[] = new String[100];
    int j = 0, x;
    Object Y, Z;
    String inputList3[] = new String[100];
    String inputList4[] = new String[100];
    String unitOp[] = new String[100];
    String buildTestCase[] = new String[100];
    int I = 0, J = 0;
    String TC[][] = new String[100][100];
    String TestCase = "";
    int randomInt;
    File oldFolder, newFolder;
    File temporaryRRTS1, temporaryRRTS2, temporaryRRTS3, temporaryRRTS4;
    String WSDLOp[] = new String[100];
    List<String> operation = new ArrayList<String>();
    String unitOperationWSDL[] = new String[100];

    public ORTWSView(SingleFrameApplication app) {
        super(app);

        initComponents();

        listModel = new DefaultListModel();
        modifiedFiles = new JList(listModel);
        leftRoot = new DefaultMutableTreeNode();
        leftTreeModel = new DefaultTreeModel(leftRoot);
        leftTree = new JTree(leftTreeModel);
        leftTree.setRootVisible(false);
        leftTree.setShowsRootHandles(true);
        leftScroll = new JScrollPane(leftTree);
        //leftTree.setCellRenderer(renderer);
        rightRoot = new DefaultMutableTreeNode();
        rightTreeModel = new DefaultTreeModel(rightRoot);
        rightTree = new JTree(rightTreeModel);
        rightTree.setRootVisible(false);
        rightTree.setShowsRootHandles(true);
        rightScroll = new JScrollPane(rightTree);
        //rightTree.setCellRenderer(renderer);
        compareFolders = new JButton("Changed Operations ");
        compareFiles = new JButton("Compare Individual Operations changes");
        compareFiles.setEnabled(false);
        leftBorder = new TitledBorder("Old Operations");
        rightBorder = new TitledBorder("New Operations");

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        toolbar.setBorder(new EmptyBorder(2, 2, 2, 2));
        toolbar.add(compareFolders);
        toolbar.add(compareFiles);
        //JPanel mainPanel = new JPanel(new BorderLayout(5,5));
        JPanel leftPanel = new JPanel(new BorderLayout(2, 2));
        leftPanel.setBorder(leftBorder);
        leftPanel.add(leftScroll, BorderLayout.CENTER);
        JPanel rightPanel = new JPanel(new BorderLayout(3, 3));
        rightPanel.setBorder(rightBorder);
        rightPanel.add(rightScroll, BorderLayout.CENTER);
        jPanel4.setBorder(new EmptyBorder(0, 5, 5, 5));
        JPanel treesPanel = new JPanel(new GridLayout(1, 1, 2, 2));
        treesPanel.add(leftPanel, 0);
        treesPanel.add(rightPanel, 1);

        //JPanel listPanel = new JPanel(new BorderLayout(1, 1));
        //listPanel.add(new JScrollPane(modifiedFiles), BorderLayout.CENTER);
        //listPanel.setBorder(new TitledBorder("Modified Operations"));


        jPanel4.setLayout(new GridLayout(2, 1, 2, 2));
        jPanel4.add(treesPanel, BorderLayout.NORTH);
        //jPanel4.add(listPanel, BorderLayout.SOUTH);
        //mainPanel.setLayout(new BorderLayout(5, 5));
        jPanel4.add(toolbar, BorderLayout.NORTH);
        //mainPanel.add(toolbar, BorderLayout.CENTER);

        compareFolders.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                compareFolders();
            }
        });
        compareFiles.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                compareFiles();
            }
        });
        leftTree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent arg0) {
                if (!selection) {
                    selection = true;
                    rightTree.getSelectionModel().setSelectionPath(null);
                    modifiedFiles.getSelectionModel().clearSelection();
                    checkSelection();
                    selection = false;
                }
            }
        });
        modifiedFiles.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (!selection) {
                    selection = true;
                    rightTree.getSelectionModel().setSelectionPath(null);
                    leftTree.getSelectionModel().setSelectionPath(null);
                    checkSelection();
                    selection = false;
                }
            }
        });

        rightTree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent arg0) {
                if (!selection) {
                    selection = true;
                    leftTree.getSelectionModel().setSelectionPath(null);
                    modifiedFiles.getSelectionModel().clearSelection();
                    checkSelection();
                    selection = false;
                }
            }
        });

        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = ORTWSApp.getApplication().getMainFrame();
            aboutBox = new ORTWSAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        ORTWSApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        mainPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        scrollPane1 = new java.awt.ScrollPane();
        scrollPane2 = new java.awt.ScrollPane();
        scrollPane3 = new java.awt.ScrollPane();
        jButton18 = new javax.swing.JButton();
        scrollPane5 = new java.awt.ScrollPane();
        scrollPane6 = new java.awt.ScrollPane();
        jButton26 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        mainPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton4 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        scrollPane4 = new java.awt.ScrollPane();
        scrollPane7 = new java.awt.ScrollPane();
        scrollPane8 = new java.awt.ScrollPane();
        jButton27 = new javax.swing.JButton();
        scrollPane19 = new java.awt.ScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        scrollPane9 = new java.awt.ScrollPane();
        scrollPane10 = new java.awt.ScrollPane();
        scrollPane11 = new java.awt.ScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jButton28 = new javax.swing.JButton();
        scrollPane14 = new java.awt.ScrollPane();
        jLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        scrollPane15 = new java.awt.ScrollPane();
        scrollPane16 = new java.awt.ScrollPane();
        scrollPane17 = new java.awt.ScrollPane();
        scrollPane18 = new java.awt.ScrollPane();
        jButton33 = new javax.swing.JButton();
        mainPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList();
        scrollPane12 = new java.awt.ScrollPane();
        scrollPane13 = new java.awt.ScrollPane();
        jButton22 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList5 = new javax.swing.JList();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList6 = new javax.swing.JList();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jButton25 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jButton9 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jPanel5 = new javax.swing.JPanel();

        mainPanel.setName("mainPanel"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        mainPanel1.setName("mainPanel1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ortws.ORTWSApp.class).getContext().getResourceMap(ORTWSView.class);
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setName("jButton8"); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        scrollPane1.setName("scrollPane1"); // NOI18N

        scrollPane2.setName("scrollPane2"); // NOI18N

        scrollPane3.setName("scrollPane3"); // NOI18N

        jButton18.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton18.setName("jButton9"); // NOI18N
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        scrollPane5.setName("scrollPane5"); // NOI18N

        scrollPane6.setName("scrollPane6"); // NOI18N

        jButton26.setText(resourceMap.getString("jButton26.text")); // NOI18N
        jButton26.setName("jButton26"); // NOI18N
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanel1Layout = new javax.swing.GroupLayout(mainPanel1);
        mainPanel1.setLayout(mainPanel1Layout);
        mainPanel1Layout.setHorizontalGroup(
            mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jButton1))
                    .addComponent(scrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanel1Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(jButton3))
                    .addComponent(scrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanel1Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jButton8))
                    .addComponent(scrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanel1Layout.createSequentialGroup()
                        .addComponent(jButton18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(jButton26))
                    .addComponent(scrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        mainPanel1Layout.setVerticalGroup(
            mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(scrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(scrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanel1Layout.createSequentialGroup()
                        .addGroup(mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2)
                            .addComponent(jButton3)
                            .addComponent(jButton8)
                            .addComponent(jButton18)
                            .addComponent(jButton26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(scrollPane6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(mainPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(754, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(mainPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(287, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        mainPanel2.setName("mainPanel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jList1.setName("jList1"); // NOI18N
        jScrollPane1.setViewportView(jList1);

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jList2.setName("jList2"); // NOI18N
        jScrollPane2.setViewportView(jList2);

        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setName("jButton10"); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText(resourceMap.getString("jButton11.text")); // NOI18N
        jButton11.setName("jButton11"); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        scrollPane4.setName("scrollPane4"); // NOI18N

        scrollPane7.setName("scrollPane7"); // NOI18N

        scrollPane8.setName("scrollPane8"); // NOI18N

        jButton27.setText(resourceMap.getString("jButton27.text")); // NOI18N
        jButton27.setName("jButton27"); // NOI18N
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        scrollPane19.setName("scrollPane19"); // NOI18N

        javax.swing.GroupLayout mainPanel2Layout = new javax.swing.GroupLayout(mainPanel2);
        mainPanel2.setLayout(mainPanel2Layout);
        mainPanel2Layout.setHorizontalGroup(
            mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7)
                    .addComponent(scrollPane19, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton4)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
                .addGroup(mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanel2Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton5)
                    .addComponent(scrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanel2Layout.createSequentialGroup()
                        .addComponent(jButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton27))
                    .addComponent(scrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(182, 182, 182))
        );
        mainPanel2Layout.setVerticalGroup(
            mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanel2Layout.createSequentialGroup()
                .addGroup(mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanel2Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton6)
                            .addComponent(jButton4)
                            .addComponent(jButton7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(scrollPane19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanel2Layout.createSequentialGroup()
                        .addGroup(mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton11)
                            .addComponent(jButton27)
                            .addComponent(jButton10)
                            .addComponent(jButton5))
                        .addGap(13, 13, 13)
                        .addGroup(mainPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(scrollPane8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(scrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                            .addComponent(scrollPane7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(306, 306, 306)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(mainPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(513, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(mainPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
        jButton12.setName("jButton12"); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText(resourceMap.getString("jButton13.text")); // NOI18N
        jButton13.setName("jButton13"); // NOI18N
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText(resourceMap.getString("jButton14.text")); // NOI18N
        jButton14.setName("jButton14"); // NOI18N
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText(resourceMap.getString("jButton15.text")); // NOI18N
        jButton15.setName("jButton15"); // NOI18N
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setText(resourceMap.getString("jButton16.text")); // NOI18N
        jButton16.setName("jButton16"); // NOI18N
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton17.setText(resourceMap.getString("jButton17.text")); // NOI18N
        jButton17.setName("jButton17"); // NOI18N
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        scrollPane9.setName("scrollPane9"); // NOI18N

        scrollPane10.setName("scrollPane10"); // NOI18N

        scrollPane11.setName("scrollPane11"); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 711, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jButton28.setText(resourceMap.getString("jButton28.text")); // NOI18N
        jButton28.setName("jButton28"); // NOI18N
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        scrollPane14.setName("scrollPane14"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(scrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(scrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(15, 15, 15))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jButton17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton28))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(942, 942, 942))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton28)
                    .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(scrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(scrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(scrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(scrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 583, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(212, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N

        jButton29.setText(resourceMap.getString("jButton29.text")); // NOI18N
        jButton29.setName("jButton29"); // NOI18N
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jButton30.setText(resourceMap.getString("jButton30.text")); // NOI18N
        jButton30.setName("jButton30"); // NOI18N
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        jButton31.setText(resourceMap.getString("jButton31.text")); // NOI18N
        jButton31.setName("jButton31"); // NOI18N
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jButton32.setText(resourceMap.getString("jButton32.text")); // NOI18N
        jButton32.setName("jButton32"); // NOI18N
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        scrollPane15.setName("scrollPane15"); // NOI18N

        scrollPane16.setName("scrollPane16"); // NOI18N

        scrollPane17.setName("scrollPane17"); // NOI18N

        scrollPane18.setName("scrollPane18"); // NOI18N

        jButton33.setText(resourceMap.getString("jButton33.text")); // NOI18N
        jButton33.setName("jButton33"); // NOI18N
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton29)
                    .addComponent(scrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane17, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(scrollPane18, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1043, 1043, 1043))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton29)
                    .addComponent(jButton30)
                    .addComponent(jButton31)
                    .addComponent(jButton32)
                    .addComponent(jButton33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane17, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                    .addComponent(scrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                    .addComponent(scrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                    .addComponent(scrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE))
                .addGap(412, 412, 412))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

        mainPanel3.setName("mainPanel3"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jList3.setName("jList3"); // NOI18N
        jScrollPane3.setViewportView(jList3);

        jButton19.setText(resourceMap.getString("jButton19.text")); // NOI18N
        jButton19.setName("jButton19"); // NOI18N
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setText(resourceMap.getString("jButton20.text")); // NOI18N
        jButton20.setName("jButton20"); // NOI18N
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setText(resourceMap.getString("jButton21.text")); // NOI18N
        jButton21.setName("jButton21"); // NOI18N
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jList4.setName("jList4"); // NOI18N
        jList4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList4MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jList4);

        scrollPane12.setName("scrollPane12"); // NOI18N

        scrollPane13.setName("scrollPane13"); // NOI18N

        jButton22.setText(resourceMap.getString("jButton22.text")); // NOI18N
        jButton22.setName("jButton22"); // NOI18N
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        jList5.setName("jList5"); // NOI18N
        jScrollPane5.setViewportView(jList5);

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        jList6.setName("jList6"); // NOI18N
        jScrollPane6.setViewportView(jList6);

        jButton23.setText(resourceMap.getString("jButton23.text")); // NOI18N
        jButton23.setName("jButton23"); // NOI18N
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jButton24.setText(resourceMap.getString("jButton24.text")); // NOI18N
        jButton24.setName("jButton24"); // NOI18N
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        jButton25.setText(resourceMap.getString("jButton25.text")); // NOI18N
        jButton25.setName("jButton25"); // NOI18N
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanel3Layout = new javax.swing.GroupLayout(mainPanel3);
        mainPanel3.setLayout(mainPanel3Layout);
        mainPanel3Layout.setHorizontalGroup(
            mainPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanel3Layout.createSequentialGroup()
                        .addComponent(jButton19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                        .addComponent(jButton20)
                        .addGap(18, 18, 18)
                        .addComponent(jButton21))
                    .addGroup(mainPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(mainPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(298, 298, 298)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(mainPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(mainPanel3Layout.createSequentialGroup()
                        .addGroup(mainPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton23)
                            .addComponent(jLabel25))
                        .addGap(85, 85, 85)
                        .addGroup(mainPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(mainPanel3Layout.createSequentialGroup()
                                .addComponent(jButton24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton25)))))
                .addGroup(mainPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanel3Layout.createSequentialGroup()
                        .addGap(132, 132, 132)
                        .addComponent(jButton22))
                    .addGroup(mainPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(scrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(733, 733, 733))
        );
        mainPanel3Layout.setVerticalGroup(
            mainPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton21)
                    .addComponent(jButton20)
                    .addComponent(jButton19)
                    .addComponent(jButton23)
                    .addComponent(jButton24)
                    .addComponent(jButton25)
                    .addComponent(jButton22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5)
                    .addComponent(jScrollPane6)
                    .addComponent(jScrollPane4)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                    .addComponent(scrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE))
                .addContainerGap(249, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("mainPanel3.TabConstraints.tabTitle"), mainPanel3); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ortws.ORTWSApp.class).getContext().getActionMap(ORTWSView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1904, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1734, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setName("jButton9"); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMenu1.setText("File");
        jMenu1.setName("jMenu1"); // NOI18N
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenu2.setName("jMenu2"); // NOI18N
        jMenuBar1.add(jMenu2);

        jPanel5.setName("jPanel5"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        testSuiteFile5 = temporaryRRTS1;
        //        jTextField16.setText(testSuiteFile5.getPath() + File.separator + testSuiteFile5.getName());
        I = 0;
        J = 0;

        TestCase = "";
        final String inputList[] = new String[100];
        for (int i = 0; i < 100; i++) {
            inputList[i] = null;
            inputList3[i] = null;
            inputList4[i] = null;
            buildTestCase[i] = null;
        }
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                TC[i][j] = null;
            }
        }

        jList4.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        jList5.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        jList6.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile5);
        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(RWsaxTree3);
            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile5)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }
        JTree tree = new JTree(RWsaxTree3.getTree());
        scrollPane12.add(tree);
        ParsingTestSuite obj = new ParsingTestSuite();

        //String [] output = null;
        try {
            TC = obj.testCases(testSuiteFile5);
        } catch (Exception ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
        final String[] output;
        output = new String[100];
        int j = 0;

        for (; TC[j][0] != null; j++) {
            output[j] = TC[j][0];
            //System.reduceOp.println(TC[j]);
        }

        jList3.setModel(new javax.swing.AbstractListModel() {

            String[] strings = output;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

//        inputList3 = diffOp;
//        jList4.setModel(new javax.swing.AbstractListModel() {
//
//            String[] strings = inputList3;
//
//            public int getSize() {
//                return strings.length;
//            }
//
//            public Object getElementAt(int i) {
//                return strings[i];
//            }
//        });
}//GEN-LAST:event_jButton26ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        try {
            File temporaryRRTSFolder1 = new File(testSuiteFile1.getParent() + "\\temporaryRRTS\\");
            temporaryRRTSFolder1.mkdirs();
            temporaryRRTS1 = File.createTempFile("RRTS", ".xml", new File(temporaryRRTSFolder1.getAbsolutePath()));
            //        JFileChooser fileopen = new JFileChooser();
            //        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
            //        fileopen.addChoosableFileFilter(filter);
            //        int ret = fileopen.showDialog(null, "Open file");
            //        if (ret == JFileChooser.APPROVE_OPTION) {
            //            ReduceTS = fileopen.getSelectedFile();
            ////            jTextField7.setText(ReduceTS.getPath() + File.separator + ReduceTS.getName());
            //        }
            String[] s = diffOp; //{"Index", "editFile"};
            SoapUIReduceTestCaseNew.Construction TC = new SoapUIReduceTestCaseNew.Construction();
            try {
                TC.ConstructReduceTC(testSuiteFile1, s, temporaryRRTS1);
            } catch (IOException ex) {
                Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
            }
            DefaultMutableTreeNode top = new DefaultMutableTreeNode(temporaryRRTS1);
            SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);
            try {
                SAXParser saxParser = new SAXParser();
                saxParser.setContentHandler(RWsaxTree3);
                saxParser.parse(new InputSource(new FileInputStream(temporaryRRTS1)));
            } catch (Exception ex) {
                top.add(new DefaultMutableTreeNode(ex.getMessage()));
            }
            JTree tree = new JTree(RWsaxTree3.getTree());
            scrollPane6.add(tree);
        } catch (IOException ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        JFileChooser fileopen = new JFileChooser(WSDL1.getParentFile().getParent());
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            testSuiteFile1 = fileopen.getSelectedFile();
            //            jTextField6.setText(testSuiteFile1.getPath() + File.separator + testSuiteFile1.getName());
        }
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile1);
        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(RWsaxTree3);
            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile1)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }
        JTree tree = new JTree(RWsaxTree3.getTree());
        scrollPane5.add(tree);
}//GEN-LAST:event_jButton8ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        for (int i = 0; diffOp[i] != null; i++) {
            diffOp[i] = null;
        }
        try {
            File temporaryDWSDL = new File(WSDL1.getParent() + "\\temporaryDWSDL\\");
            temporaryDWSDL.mkdirs();
            File file = File.createTempFile("DifferenceWSDL", ".wsdl", new File(temporaryDWSDL.getAbsolutePath()));
            //            jTextField3.setText(file.getPath());
            //            JFileChooser fileopen = new JFileChooser();
            //            FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
            //            fileopen.addChoosableFileFilter(filter);
            //            int ret = fileopen.showDialog(null, "Save file");
            //            if (ret == JFileChooser.APPROVE_OPTION) {
            //                file = fileopen.getSelectedFile();
            //                jTextField3.setText("file:///" + file.getPath());
            //            }

            Construction differenceWSDL = new Construction();
            //readingpatch111112.Construction differWSDL = new readingpatch111112.Construction();
            //            String WSDL1name = jTextField1.getText();
            //            System.out.println(" XXXXXX " + WSDL1name);
            //            String WSDL2name = jTextField2.getText();
            //            System.out.println(" XXXXXX " + WSDL2name);
            //            String diffWSDL = jTextField3.toString();
            //            String contains = null;
            //
            //            StringBuffer stringBufferOfData = new StringBuffer();

            try {
                //                fileToRead = new Scanner(WSDL2); //point the scanner method to a file
                //                check if there is a next line and it is not null and then read it in
                //                for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
                //                    stringBufferOfData.append(line).append("\r\n");
                //                }
                //                y = stringBufferOfData.toString().contains("wsdl:definition");
                //                if (y) {
                diffOp = differenceWSDL.ConstructDIfferenceWSDL(WSDL1, WSDL2, file);

                //              } else {
                //              differWSDL.differenceConstruct(WSDL1, WSDL2, file);
                //              }

                //              fileToRead.close();//this is used to release the scanner from file
            } catch (FileNotFoundException ex) {
            }

            //          TODO add your handling code here:

            DefaultMutableTreeNode top = new DefaultMutableTreeNode(file);

            saxTree3 = new SAXTreeBuilder(top);

            try {
                SAXParser saxParser = new SAXParser();
                saxParser.setContentHandler(saxTree3);
                saxParser.parse(new InputSource(new FileInputStream(file)));
            } catch (Exception ex) {
                top.add(new DefaultMutableTreeNode(ex.getMessage()));
            }
            JTree tree = new JTree(saxTree3.getTree());
            scrollPane3.add(tree);
        } catch (IOException ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        //File file = null;
        JFileChooser fileopen = new JFileChooser(WSDL1);
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showDialog(null, "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            WSDL2 = fileopen.getSelectedFile();
            //            jTextField2.setText(WSDL2.getPath());
        }

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(WSDL2);

        saxTree2 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(saxTree2);
            saxParser.parse(new InputSource(new FileInputStream(WSDL2)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }
        JTree tree = new JTree(saxTree2.getTree());
        scrollPane2.add(tree);
}//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //File file = null;
        JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            WSDL1 = fileopen.getSelectedFile();
            //            jTextField1.setText(WSDL1.getPath());
        }

        SAXTreeBuilder saxTree1 = null;

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(WSDL1);
        saxTree1 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(saxTree1);
            saxParser.parse(new InputSource(new FileInputStream(WSDL1)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }

        JTree tree = new JTree(saxTree1.getTree());
        scrollPane1.add(tree);                       // TODO add your handling code here:
}//GEN-LAST:event_jButton1ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        int x = jList4.getAnchorSelectionIndex();
        Z = jList4.getModel().getElementAt(x);
        buildTestCase[0] = Z.toString();

        for (int i = 0; inputList4[i] != null; i++) {
            buildTestCase[i + 1] = inputList4[i];
        }

        jList6.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList4;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {

                return strings[i];
            }
        });
        try {
            TestCases obj = new TestCases();
            String z = obj.buildTestCase(testSuiteFile5, buildTestCase);
            TestCase = TestCase.concat(z);
        } catch (Exception e) {
        }//(testSuiteFile5, buildTestCase);
}//GEN-LAST:event_jButton25ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        int x = jList6.getAnchorSelectionIndex();
        System.out.println(" X  " + Z.toString() + " x " + x);
        Object X = jList6.getModel().getElementAt(x);

        System.out.print(X.toString());
        for (int i = 0; inputList4[i] != null; i++) {
            if (inputList4[i] == X.toString()) {
                inputList4[i] = inputList4[i + 1];
                J--;
                System.out.println("if inside " + inputList4[i] + inputList4[i + 1]);
                while (inputList4[i] != null) {
                    inputList4[i] = inputList4[i + 1];
                    i++;

                }
                inputList4[i] = null;

            }
            System.out.println("for loop " + inputList4[i]);

        }
        jList6.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList4;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
}//GEN-LAST:event_jButton24ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        int x = jList5.getAnchorSelectionIndex();
        Z = jList5.getModel().getElementAt(x);

        System.out.print(Z.toString());
        inputList4[J] = Z.toString();
        J++;

        jList6.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList4;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });        // TODO add your handling code here:
}//GEN-LAST:event_jButton23ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        try {
            File temporaryRRTS = new File(testSuiteFile5.getParent() + "\\temporaryRRTS\\");
            temporaryRRTS.mkdirs();
            File ReduceTS = File.createTempFile("ReduceTestStep_RRTS", ".xml", new File(temporaryRRTS.getAbsolutePath()));
            //        JFileChooser fileopen = new JFileChooser();
            //        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
            //        fileopen.addChoosableFileFilter(filter);
            //        int ret = fileopen.showDialog(null, "Open file");
            //        if (ret == JFileChooser.APPROVE_OPTION) {
            //            ReduceTS = fileopen.getSelectedFile();
            ////            jTextField4.setText(ReduceTS.getPath() + File.separator + ReduceTS.getName());
            //        }
            String[] s;
            s = new String[100];
            for (int i = 0; inputList3[i] != null; i++) {
                s[i] = inputList3[i];
            }
            ConstructRRTS.Construction TC = new ConstructRRTS.Construction();
            try {
                TC.ConstructReduceTC(testSuiteFile5, TestCase, ReduceTS);
            } catch (IOException ex) {
                Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
            }
            DefaultMutableTreeNode top = new DefaultMutableTreeNode(ReduceTS);
            SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);
            try {
                SAXParser saxParser = new SAXParser();
                saxParser.setContentHandler(RWsaxTree3);
                saxParser.parse(new InputSource(new FileInputStream(ReduceTS)));
            } catch (Exception ex) {
                top.add(new DefaultMutableTreeNode(ex.getMessage()));
            }
            JTree tree = new JTree(RWsaxTree3.getTree());
            scrollPane13.add(tree);
        } catch (IOException ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButton22ActionPerformed

    private void jList4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList4MouseClicked
        int x = jList4.getAnchorSelectionIndex();
        Object Y = jList4.getModel().getElementAt(x);
        int i = 0;
        final String s[] = new String[100];
        buildTestCase[0] = Y.toString();
        for (; TC[i][0] != null; i++) {
            if (Y.toString() == TC[i][0] && TC[i][0] != null) {
                for (int j = 1; TC[i][j] != null; j++) {
                    s[j - 1] = TC[i][j];
                }
            }
        }

        final String inputList[] = new String[100];
        for (int j = 0; j < 100; j++) {
            inputList[j] = null;
            inputList4[j] = null;
            buildTestCase[j] = null;
        }
        J = 0;
        jList6.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        jList5.setModel(new javax.swing.AbstractListModel() {

            String[] strings = s;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
}//GEN-LAST:event_jList4MouseClicked

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        int x = jList4.getAnchorSelectionIndex();
        System.out.println(" X  " + Y.toString() + " x " + x);
        Object X = jList4.getModel().getElementAt(x);

        System.out.print(X.toString());
        for (int i = 0; inputList3[i] != null; i++) {
            if (inputList3[i] == X.toString()) {
                inputList3[i] = inputList3[i + 1];
                I--;
                System.out.println("if inside " + inputList3[i] + inputList3[i + 1]);
                while (inputList3[i] != null) {
                    inputList3[i] = inputList3[i + 1];
                    i++;
                }
                inputList3[i] = null;

            }
            System.out.println("for loop " + inputList3[i]);

        }
        jList4.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList3;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
}//GEN-LAST:event_jButton21ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        int x = jList3.getAnchorSelectionIndex();
        Y = jList3.getModel().getElementAt(x);

        //System.out.print(Y.toString());
        inputList3[I] = Y.toString();
        I++;

        jList4.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList3;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
}//GEN-LAST:event_jButton20ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        //File testSuiteFile5;
        I = 0;
        J = 0;

        TestCase = "";
        final String inputList[] = new String[100];

        for (int i = 0; i < 100; i++) {
            inputList[i] = null;
            inputList3[i] = null;
            inputList4[i] = null;
            buildTestCase[i] = null;
        }

        for (int i = 0; i < 100; i++) {
            for (int j = 0; i < 100; i++) {
                TC[i][j] = null;
            }
        }

        jList4.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        jList5.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        jList6.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            testSuiteFile5 = fileopen.getSelectedFile();
            //            jTextField16.setText(testSuiteFile5.getPath() + File.separator + testSuiteFile5.getName());
        }
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile5);
        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(RWsaxTree3);
            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile5)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }

        JTree tree = new JTree(RWsaxTree3.getTree());
        scrollPane12.add(tree);
        ParsingTestSuite obj = new ParsingTestSuite();

        //String [] output = null;
        try {
            TC = obj.testCases(testSuiteFile5);
        } catch (Exception ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }

        final String[] output;
        output = new String[100];
        int j = 0;

        for (; TC[j][0] != null; j++) {
            output[j] = TC[j][0];
            //System.reduceOp.println(TC[j]);
        }

        jList3.setModel(new javax.swing.AbstractListModel() {

            String[] strings = output;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        //    I = 0; J = 0;
        //
        //    TestCase = "";
        //    final String inputList[] = new String[100];
        //    for (int i = 0; i <100 ; i++) {
        //            inputList[i] = null;
        //            inputList3[i] =null;
        //            inputList4[i] =null;
        //            buildTestCase[i] = null;
        //               }
        //     for (int i = 0; i <100 ; i++) {
        //          for (int j = 0; i <100 ; i++) {
        //            TC[i][j] = null;
        //           }
        //     }
        //
        //    jList2.setModel(new javax.swing.AbstractListModel() {
        //
        //            String[] strings = inputList;
        //
        //            public int getSize() {
        //                return strings.length;
        //            }
        //
        //            public Object getElementAt(int i) {
        //                return strings[i];
        //            }
        //        });
        //
        //        jList3.setModel(new javax.swing.AbstractListModel() {
        //
        //            String[] strings = inputList;
        //
        //            public int getSize() {
        //                return strings.length;
        //            }
        //
        //            public Object getElementAt(int i) {
        //                return strings[i];
        //            }
        //        });
        //
        //        jList4.setModel(new javax.swing.AbstractListModel() {
        //
        //            String[] strings = inputList;
        //
        //            public int getSize() {
        //                return strings.length;
        //            }
        //
        //            public Object getElementAt(int i) {
        //                return strings[i];
        //            }
        //        });
        //
        //        JFileChooser fileopen = new JFileChooser();
        //        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        //        fileopen.addChoosableFileFilter(filter);
        //        int ret = fileopen.showDialog(null, "Open file");
        //        if (ret == JFileChooser.APPROVE_OPTION) {
        //            testSuiteFile5 = fileopen.getSelectedFile();
        //            jTextField1.setText(testSuiteFile5.getPath() + File.separator + testSuiteFile5.getName());
        //        }
        //        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile5);
        //        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);
        //
        //        try {
        //            SAXParser saxParser = new SAXParser();
        //            saxParser.setContentHandler(RWsaxTree3);
        //            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile5)));
        //        } catch (Exception ex) {
        //            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        //        }
        //        JTree tree = new JTree(RWsaxTree3.getTree());
        //        scrollPane1.add(tree);
        //        ParsingTestSuite obj = new ParsingTestSuite();
        //
        //        //String [] output = null;
        //        try {
        //            TC = obj.testCases(testSuiteFile5);
        //        } catch (Exception ex) {
        //            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        //        }
        //        final String[] output;
        //        output = new String[100];
        //        int j = 0;
        //
        //        for (; TC[j][0] != null; j++) {
        //            output[j] = TC[j][0];
        //            //System.out.println(TC[j]);
        //        }
        //
        //        jList1.setModel(new javax.swing.AbstractListModel() {
        //
        //            String[] strings = output;
        //
        //            public int getSize() {
        //                return strings.length;
        //            }
        //
        //            public Object getElementAt(int i) {
        //                return strings[i];
        //            }
        //        });
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        try {
            File temporaryRRTSFolder4 = new File(testSuiteFile4.getParent() + "\\temporaryRRTS\\");
            temporaryRRTSFolder4.mkdirs();
            temporaryRRTS4 = File.createTempFile("CRRTS_CWSDL", ".xml", new File(temporaryRRTSFolder4.getAbsolutePath()));
            //        JFileChooser fileopen = new JFileChooser();
            //        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
            //        fileopen.addChoosableFileFilter(filter);
            //        int ret = fileopen.showDialog(null, "Open file");
            //        if (ret == JFileChooser.APPROVE_OPTION) {
            //            ReduceTS = fileopen.getSelectedFile();
            ////            jTextField21.setText(ReduceTS.getPath() + File.separator + ReduceTS.getName());
            //        }
            for (String s : combinedUniqueOp) {
                System.out.print("unique combined op \n" + s);
                combinedOp[x] = s;
                x++;
            }
            SoapUIReduceTestCaseNew.Construction TC = new SoapUIReduceTestCaseNew.Construction();
            try {
                TC.ConstructReduceTC(testSuiteFile4, combinedOp, temporaryRRTS4);
            } catch (IOException ex) {
                Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
            }
            DefaultMutableTreeNode top = new DefaultMutableTreeNode(temporaryRRTS4);
            SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);
            try {
                SAXParser saxParser = new SAXParser();
                saxParser.setContentHandler(RWsaxTree3);
                saxParser.parse(new InputSource(new FileInputStream(temporaryRRTS4)));
            } catch (Exception ex) {
                top.add(new DefaultMutableTreeNode(ex.getMessage()));
            }
            JTree tree = new JTree(RWsaxTree3.getTree());
            scrollPane18.add(tree);
        } catch (IOException ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButton32ActionPerformed

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        JFileChooser fileopen = new JFileChooser(WSDL5.getParentFile().getParent());
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            testSuiteFile4 = fileopen.getSelectedFile();
            //            jTextField20.setText(testSuiteFile5.getPath() + File.separator + testSuiteFile5.getName());
        }
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile4);
        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(RWsaxTree3);
            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile4)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }

        JTree tree = new JTree(RWsaxTree3.getTree());
        scrollPane17.add(tree);
}//GEN-LAST:event_jButton31ActionPerformed

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        System.out.print("\n diff op " + diffOp.toString() + "\nreduce op " + reduceOp.toString() + "\nunit op " + unitOp.toString());
        combinedUniqueOp = new ArrayList<String>();
        if (diffOp != null) {
            for (int y = 0; diffOp[y] != null; y++) {
                combinedUniqueOp.add(diffOp[y]);
                System.out.print(" \ndiff combined op " + diffOp[y]);
            }
        }
        if (reduceOp != null) {
            for (int y = 0; reduceOp[y] != null; y++) {
                combinedUniqueOp.add(reduceOp[y]);
                System.out.print("\nreduce combined op " + reduceOp[y]);
            }
        }
        if (unitOperationWSDL != null) {
            for (int y = 0; unitOperationWSDL[y] != null; y++) {
                combinedUniqueOp.add(unitOperationWSDL[y]);
                System.out.print("\nunit combined op " + combinedOp[y]);
            }
        }
        combinedUniqueOp = uniqueVar(combinedUniqueOp);

        try {
            File temporaryCWSDL = new File(WSDL5.getParent() + "\\temporaryCWSDL\\");
            temporaryCWSDL.mkdirs();
            File combinedFile = File.createTempFile("CombinedWSDL", ".wsdl", new File(temporaryCWSDL.getAbsolutePath()));
            //            JFileChooser fileopen = new JFileChooser();
            //            FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
            //            fileopen.addChoosableFileFilter(filter);
            //            int ret = fileopen.showDialog(null, "Save file");
            //            if (ret == JFileChooser.APPROVE_OPTION) {
            //                combinedFile = fileopen.getSelectedFile();
            ////                jTextField19.setText("file:///" + combinedFile.getPath());
            //            }

            int a = 0;
            for (String s : combinedUniqueOp) {
                System.out.print("\nunique combined op " + s);
                combinedOp[a] = s;
                a++;
            }

            ConstructReducedWSDL.Construction ReduceWSDL = new ConstructReducedWSDL.Construction();
            ReduceWSDL.ConstructReduceWSDL(WSDL5, combinedOp, combinedFile); // TODO add your handling code here:
            DefaultMutableTreeNode top = new DefaultMutableTreeNode(combinedFile);
            SAXTreeBuilder CUsaxTree3 = new SAXTreeBuilder(top);

            try {
                SAXParser saxParser = new SAXParser();
                saxParser.setContentHandler(CUsaxTree3);
                saxParser.parse(new InputSource(new FileInputStream(combinedFile)));
            } catch (Exception ex) {
                top.add(new DefaultMutableTreeNode(ex.getMessage()));
            }

            JTree tree = new JTree(CUsaxTree3.getTree());
            scrollPane16.add(tree);

        } catch (IOException ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButton30ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        //File file = null;
        JFileChooser fileopen = new JFileChooser(WSDL4);
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            WSDL5 = fileopen.getSelectedFile();
            //            jTextField18.setText(WSDL5.getPath());
        }

        SAXTreeBuilder saxTree1 = null;

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(WSDL5);
        saxTree1 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(saxTree1);
            saxParser.parse(new InputSource(new FileInputStream(WSDL5)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }

        JTree tree = new JTree(saxTree1.getTree());
        scrollPane15.add(tree);                       // TODO add your handling code here:
}//GEN-LAST:event_jButton29ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        testSuiteFile5 = temporaryRRTS3;
        //        jTextField16.setText(testSuiteFile5.getPath() + File.separator + testSuiteFile5.getName());
        I = 0;
        J = 0;

        TestCase = "";
        final String inputList[] = new String[100];
        for (int i = 0; i < 100; i++) {
            inputList[i] = null;
            inputList3[i] = null;
            inputList4[i] = null;
            buildTestCase[i] = null;
        }
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                TC[i][j] = null;
            }
        }

        jList4.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        jList5.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        jList6.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile5);
        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(RWsaxTree3);
            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile5)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }
        JTree tree = new JTree(RWsaxTree3.getTree());
        scrollPane12.add(tree);
        ParsingTestSuite obj = new ParsingTestSuite();

        //String [] output = null;
        try {
            TC = obj.testCases(testSuiteFile5);
        } catch (Exception ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
        final String[] output;
        output = new String[100];
        int j = 0;

        for (; TC[j][0] != null; j++) {
            output[j] = TC[j][0];
            //System.reduceOp.println(TC[j]);
        }

        jList3.setModel(new javax.swing.AbstractListModel() {

            String[] strings = output;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

//        inputList3 = unitOp;
//        jList4.setModel(new javax.swing.AbstractListModel() {
//
//            String[] strings = inputList3;
//
//            public int getSize() {
//                return strings.length;
//            }
//
//            public Object getElementAt(int i) {
//                return strings[i];
//            }
//        });
}//GEN-LAST:event_jButton28ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        try {
            //modifiedFiles.getModel().getElementAt(x);
            String[] inputList5 = new String[100];
            Object A;
            File temporaryRRTSFolder3 = new File(testSuiteFile3.getParent() + "\\temporaryRRTS\\");
            temporaryRRTSFolder3.mkdirs();
            temporaryRRTS3 = File.createTempFile("UnitWSDL", ".wsdl", new File(temporaryRRTSFolder3.getAbsolutePath()));
            //        JFileChooser fileopen = new JFileChooser();
            //        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
            //        fileopen.addChoosableFileFilter(filter);
            //        int ret = fileopen.showDialog(null, "Open file");
            //        if (ret == JFileChooser.APPROVE_OPTION) {
            //            UnitTS = fileopen.getSelectedFile();
            ////            jTextField15.setText(UnitTS.getPath() + File.separator + UnitTS.getName());
            //        }
            for (int x = 0; x < modifiedFiles.getModel().getSize(); x++) {
                //= jList1.getAnchorSelectionIndex();
                String UnitOperation;
                A = modifiedFiles.getModel().getElementAt(x);
                System.out.print("A " + A.toString());
                int start = A.toString().lastIndexOf("\\");
                int end = A.toString().indexOf(".");
                UnitOperation = A.toString().substring(start + 1, end);
                inputList5[x] = UnitOperation;
                unitOp[x] = UnitOperation;
            }

            //String s[] = {"India"};
            SoapUIReduceTestCaseNew.Construction TC = new SoapUIReduceTestCaseNew.Construction();
            try {
                TC.ConstructReduceTC(testSuiteFile3, unitOperationWSDL, temporaryRRTS3);
            } catch (IOException ex) {
                Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
            }
            DefaultMutableTreeNode top = new DefaultMutableTreeNode(temporaryRRTS3);
            SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);
            try {
                SAXParser saxParser = new SAXParser();
                saxParser.setContentHandler(RWsaxTree3);
                saxParser.parse(new InputSource(new FileInputStream(temporaryRRTS3)));
            } catch (Exception ex) {
                top.add(new DefaultMutableTreeNode(ex.getMessage()));
            }
            JTree tree = new JTree(RWsaxTree3.getTree());
            scrollPane11.add(tree); // TODO add your handling code here:
        } catch (IOException ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButton17ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        JFileChooser fileopen = new JFileChooser(WSDL4.getParentFile().getParent());
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            testSuiteFile3 = fileopen.getSelectedFile();
            //            jTextField14.setText(testSuiteFile3.getPath() + File.separator + testSuiteFile3.getName());
        }
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile3);
        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(RWsaxTree3);
            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile3)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }
        JTree tree = new JTree(RWsaxTree3.getTree());
        scrollPane10.add(tree);
}//GEN-LAST:event_jButton16ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        for (int i = 0; unitOp[i] != null; i++) {
            unitOp[i] = null;
        }
        try {
            File temporaryUWSDL = new File(WSDL4.getParent() + "\\temporaryUWSDL\\");
            temporaryUWSDL.mkdirs();
            File reduceFile = File.createTempFile("UnitWSDL", ".wsdl", new File(temporaryUWSDL.getAbsolutePath()));
            //            JFileChooser fileopen = new JFileChooser();
            //            FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
            //            fileopen.addChoosableFileFilter(filter);
            //            int ret = fileopen.showDialog(null, "Save file");
            //            if (ret == JFileChooser.APPROVE_OPTION) {
            //                reduceFile = fileopen.getSelectedFile();
            ////                jTextField13.setText(reduceFile.getPath());
            //            }
            /*List list = jList2.getSelectedValuesList();
            Iterator it = list.iterator();
            int j = 0;
            for (; it.hasNext(); it.next()) {
            Object element = it.next();
            reduceOp[j] = element.toString();
            j++;
            System.reduceOp.println(" element ");
            System.reduceOp.print(reduceOp[j] + "reduceOp[j] ");
            }*/
            ConstructReducedWSDL.Search searchObject = new ConstructReducedWSDL.Search();
            try {
                WSDLOp = searchObject.search(WSDL4.getPath());
            } catch (IOException ex) {
                Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
            }


            String[] inputList5 = new String[100];
            Object A;
            for (int x = 0; x < modifiedFiles.getModel().getSize(); x++) {       //= jList1.getAnchorSelectionIndex();
                String UnitOperation;
                A = modifiedFiles.getModel().getElementAt(x);
                System.out.print("A " + A.toString());

                int start = A.toString().lastIndexOf("\\");
                int end = A.toString().indexOf(".");
                UnitOperation = A.toString().substring(start + 1, end);

                inputList5[x] = UnitOperation;
                unitOp[x] = UnitOperation;
            }

            for (int i = 0; WSDLOp.length > i && WSDLOp[i] != null; i++) {
                for (int j = 0; unitOp[j] != null; j++) {
                    System.out.println("WSDL operation " + WSDLOp[i]);
                    System.out.println("unit affected " + unitOp[j]);
                    if (unitOp[j].equals(WSDLOp[i])) {
                        System.out.println("operation " + WSDLOp[i]);
                        operation.add(WSDLOp[i]);
                    }
                }
            }
            operation = uniqueVar(operation);

            int a = 0;
            for (String s : operation) {
                System.out.print("\nunique combined op " + s);
                unitOperationWSDL[a] = s;
                a++;
            }

            ConstructReducedWSDL.Construction UnitWSDL = new ConstructReducedWSDL.Construction();

            //            String reduceWSDL = jTextField5.getText();
            //            System.out.println(" YYYYYYYY " + reduceWSDL);
            UnitWSDL.ConstructReduceWSDL(WSDL4, unitOperationWSDL, reduceFile); // TODO add your handling code here:

            DefaultMutableTreeNode top = new DefaultMutableTreeNode(reduceFile);
            SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

            try {
                SAXParser saxParser = new SAXParser();
                saxParser.setContentHandler(RWsaxTree3);
                saxParser.parse(new InputSource(new FileInputStream(reduceFile)));
            } catch (Exception ex) {
                top.add(new DefaultMutableTreeNode(ex.getMessage()));
            }
            JTree tree = new JTree(RWsaxTree3.getTree());
            scrollPane14.add(tree);
        } catch (IOException ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButton15ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        //File file = null;
        JFileChooser fileopen = new JFileChooser(oldFolder.getParentFile().getParentFile().getParent());
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            WSDL4 = fileopen.getSelectedFile();
            //            jTextField12.setText(WSDL4.getPath() + File.separator + WSDL4.getName());
        }

        SAXTreeBuilder saxTree1 = null;

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(WSDL4);
        saxTree1 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(saxTree1);
            saxParser.parse(new InputSource(new FileInputStream(WSDL4)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }

        JTree tree = new JTree(saxTree1.getTree());
        scrollPane9.add(tree);
}//GEN-LAST:event_jButton14ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        try {
            File newFile = null;
            JFileChooser fileopen = new JFileChooser(oldFolder.getParentFile().getParent());

            int ret = fileopen.showDialog(null, "Open file");
            if (ret == JFileChooser.APPROVE_OPTION) {
                newFile = fileopen.getSelectedFile();
            }

            newFolder = new File(newFile.getParent() + "\\CodeBreak\\New" + randomInt);
            newFolder.mkdirs();

            if (newFile.toString().contains(".c")) {
                SeperateOperations obj = new SeperateOperations();
                obj.CodeBreakOperation(newFile, newFolder.getAbsolutePath());
            }

            if (newFile.toString().contains(".java")) {
                CompilationUnit cu;
                cu = JavaParser.parse(newFile);
                new MethodVisitor().visit(cu, newFolder.getAbsolutePath());
            }
        } catch (Exception e) {
        }
}//GEN-LAST:event_jButton13ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        try {
            File oldFile = null;
            JFileChooser fileopen = new JFileChooser(WSDL3);

            int ret = fileopen.showDialog(null, "Open file");
            if (ret == JFileChooser.APPROVE_OPTION) {
                oldFile = fileopen.getSelectedFile();
            }

            Random randomGenerator = new Random();
            randomInt = randomGenerator.nextInt(100);

            oldFolder = new File(oldFile.getParent() + "\\CodeBreak\\Old" + randomInt);
            oldFolder.mkdirs();

            if (oldFile.toString().contains(".c")) {
                SeperateOperations obj = new SeperateOperations();
                obj.CodeBreakOperation(oldFile, oldFolder.getAbsolutePath());
            }

            if (oldFile.toString().contains(".java")) {
                CompilationUnit cu;
                cu = JavaParser.parse(oldFile);
                new MethodVisitor().visit(cu, oldFolder.getAbsolutePath());
            }

        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        testSuiteFile5 = temporaryRRTS2;
        //        jTextField16.setText(testSuiteFile5.getPath() + File.separator + testSuiteFile5.getName());
        I = 0;
        J = 0;

        TestCase = "";
        final String inputList[] = new String[100];
        for (int i = 0; i < 100; i++) {
            inputList[i] = null;
            inputList3[i] = null;
            inputList4[i] = null;
            buildTestCase[i] = null;
        }
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                TC[i][j] = null;
            }
        }

        jList4.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        jList5.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        jList6.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile5);
        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(RWsaxTree3);
            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile5)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }
        JTree tree = new JTree(RWsaxTree3.getTree());
        scrollPane12.add(tree);
        ParsingTestSuite obj = new ParsingTestSuite();

        //String [] output = null;
        try {
            TC = obj.testCases(testSuiteFile5);
        } catch (Exception ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
        final String[] output;
        output = new String[100];
        int j = 0;

        for (; TC[j][0] != null; j++) {
            output[j] = TC[j][0];
            //System.reduceOp.println(TC[j]);
        }

        jList3.setModel(new javax.swing.AbstractListModel() {

            String[] strings = output;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

//        inputList3 = inputList2;
//        jList4.setModel(new javax.swing.AbstractListModel() {
//
//            String[] strings = inputList3;
//
//            public int getSize() {
//                return strings.length;
//            }
//
//            public Object getElementAt(int i) {
//                return strings[i];
//            }
//        });
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        try {
            File temporaryRRTSFolder2 = new File(testSuiteFile2.getParent() + "\\temporaryRRTS\\");
            temporaryRRTSFolder2.mkdirs();
            temporaryRRTS2 = File.createTempFile("RRTS_ReduceWSDL", ".xml", new File(temporaryRRTSFolder2.getAbsolutePath()));
            //        JFileChooser fileopen = new JFileChooser();
            //        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
            //        fileopen.addChoosableFileFilter(filter);
            //        int ret = fileopen.showDialog(null, "Open file");
            //        if (ret == JFileChooser.APPROVE_OPTION) {
            //            ReduceTS = fileopen.getSelectedFile();
            ////            jTextField9.setText(ReduceTS.getPath() + File.separator + ReduceTS.getName());
            //        }
            String[] s;
            s = new String[100];
            for (int i = 0; inputList2[i] != null; i++) {
                s[i] = inputList2[i];
            }
            SoapUIReduceTestCaseNew.Construction TC = new SoapUIReduceTestCaseNew.Construction();
            try {
                TC.ConstructReduceTC(testSuiteFile2, s, temporaryRRTS2);
            } catch (IOException ex) {
                Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
            }
            DefaultMutableTreeNode top = new DefaultMutableTreeNode(temporaryRRTS2);
            SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);
            try {
                SAXParser saxParser = new SAXParser();
                saxParser.setContentHandler(RWsaxTree3);
                saxParser.parse(new InputSource(new FileInputStream(temporaryRRTS2)));
            } catch (Exception ex) {
                top.add(new DefaultMutableTreeNode(ex.getMessage()));
            }
            JTree tree = new JTree(RWsaxTree3.getTree());
            scrollPane8.add(tree);
        } catch (IOException ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButton11ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        JFileChooser fileopen = new JFileChooser(WSDL3.getParentFile().getParent());
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            testSuiteFile2 = fileopen.getSelectedFile();
            //            jTextField8.setText(testSuiteFile2.getPath() + File.separator + testSuiteFile2.getName());
        }
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile2);
        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(RWsaxTree3);
            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile2)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }
        JTree tree = new JTree(RWsaxTree3.getTree());
        scrollPane7.add(tree);
}//GEN-LAST:event_jButton10ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        JFileChooser fileopen = new JFileChooser(WSDL1);
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            WSDL3 = fileopen.getSelectedFile();
            //            jTextField4.setText(WSDL3.getPath() + File.separator + WSDL3.getName());
        }
        final String inputList[] = new String[100];
        final String[] output;
        output = new String[100];
        for (int i = 0; i < 100; i++) {
            inputList[i] = null;
            output[i] = null;
        }
        jList2.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        ConstructReducedWSDL.Search searchObject = new ConstructReducedWSDL.Search();
        //String[] reduceOp = null;
        String out[];
        out = new String[100];
        //String [] output = null;
        try {
            out = searchObject.search(WSDL3.getPath());
        } catch (IOException ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }

        int j = 0;
        for (; out.length >= j && out[j] != null; j++) {
            output[j] = out[j];
            System.out.println(out[j]);
        }

        jList1.setModel(new javax.swing.AbstractListModel() {

            String[] strings = output;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        SAXTreeBuilder saxTree1 = null;

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(WSDL3);
        saxTree1 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(saxTree1);
            saxParser.parse(new InputSource(new FileInputStream(WSDL3)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }

        JTree tree = new JTree(saxTree1.getTree());
        scrollPane19.add(tree);
        // TODO add your handling code here:
}//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        int x = jList2.getAnchorSelectionIndex();
        System.out.println(" X  " + X.toString() + " x " + x);
        Object X = jList2.getModel().getElementAt(x);

        System.out.print(X.toString());
        for (int i = 0; inputList2[i] != null; i++) {
            if (inputList2[i] == null ? X.toString() == null : inputList2[i].equals(X.toString())) {
                inputList2[i] = inputList2[i + 1];
                j--;
                System.out.println("if inside " + inputList2[i] + inputList2[i + 1]);
                while (inputList2[i] != null) {
                    inputList2[i] = inputList2[i + 1];
                    i++;
                }
                inputList2[i] = null;
            }
            System.out.println("for loop " + inputList2[i]);

        }
        jList2.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList2;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
}//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        for (int i = 0; reduceOp[i] != null; i++) {
            reduceOp[i] = null;
        }
        try {
            File temporaryRWSDL = new File(WSDL3.getParent() + "\\temporaryRWSDL\\");
            temporaryRWSDL.mkdirs();
            File reduceFile = File.createTempFile("ReduceWSDL", ".wsdl", new File(temporaryRWSDL.getAbsolutePath()));
            //            JFileChooser fileopen = new JFileChooser();
            //            FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
            //            fileopen.addChoosableFileFilter(filter);
            //            int ret = fileopen.showDialog(null, "Save file");
            //            if (ret == JFileChooser.APPROVE_OPTION) {
            //                reduceFile = fileopen.getSelectedFile();
            ////              jTextField5.setText(reduceFile.getPath());
            //            }

            for (int i = 0; inputList2[i] != null; i++) {
                reduceOp[i] = inputList2[i];
            }/*List list = jList2.getSelectedValuesList();
            Iterator it = list.iterator();
            int j = 0;
            for (; it.hasNext(); it.next()) {
            Object element = it.next();
            reduceOp[j] = element.toString();
            j++;
            System.reduceOp.println(" element ");
            System.reduceOp.print(reduceOp[j] + "reduceOp[j] ");
            }*/

            for (int z = 0; reduceOp[z] != null; z++) {
                //String startmessage = "<wsdl:message name=\"" + s[i] + "Response\">";
                System.out.println("\n out value" + reduceOp[z]);
            }

            ConstructReducedWSDL.Construction ReduceWSDL = new ConstructReducedWSDL.Construction();

            //            String reduceWSDL = jTextField5.getText();
            //            System.out.println(" YYYYYYYY " + reduceWSDL);
            ReduceWSDL.ConstructReduceWSDL(WSDL3, reduceOp, reduceFile); // TODO add your handling code here:

            DefaultMutableTreeNode top = new DefaultMutableTreeNode(reduceFile);
            SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

            try {
                SAXParser saxParser = new SAXParser();
                saxParser.setContentHandler(RWsaxTree3);
                saxParser.parse(new InputSource(new FileInputStream(reduceFile)));
            } catch (Exception ex) {
                top.add(new DefaultMutableTreeNode(ex.getMessage()));
            }
            JTree tree = new JTree(RWsaxTree3.getTree());
            scrollPane4.add(tree);
        } catch (IOException ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int x = jList1.getAnchorSelectionIndex();
        X = jList1.getModel().getElementAt(x);

        System.out.print(X.toString());
        inputList2[j] = X.toString();
        j++;

        jList2.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList2;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
}//GEN-LAST:event_jButton4ActionPerformed

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        testSuiteFile5 = temporaryRRTS4;
        //        jTextField16.setText(testSuiteFile5.getPath() + File.separator + testSuiteFile5.getName());
        I = 0;
        J = 0;

        TestCase = "";
        final String inputList[] = new String[100];
        for (int i = 0; i < 100; i++) {
            inputList[i] = null;
            inputList3[i] = null;
            inputList4[i] = null;
            buildTestCase[i] = null;
        }
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                TC[i][j] = null;
            }
        }

        jList4.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        jList5.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        jList6.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile5);
        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(RWsaxTree3);
            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile5)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }
        JTree tree = new JTree(RWsaxTree3.getTree());
        scrollPane12.add(tree);
        ParsingTestSuite obj = new ParsingTestSuite();

        //String [] output = null;
        try {
            TC = obj.testCases(testSuiteFile5);
        } catch (Exception ex) {
            Logger.getLogger(ORTWSView.class.getName()).log(Level.SEVERE, null, ex);
        }
        final String[] output;
        output = new String[100];
        int j = 0;

        for (; TC[j][0] != null; j++) {
            output[j] = TC[j][0];
            //System.reduceOp.println(TC[j]);
        }

        jList3.setModel(new javax.swing.AbstractListModel() {

            String[] strings = output;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
    }//GEN-LAST:event_jButton33ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JList jList4;
    private javax.swing.JList jList5;
    private javax.swing.JList jList6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel mainPanel1;
    private javax.swing.JPanel mainPanel2;
    private javax.swing.JPanel mainPanel3;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private java.awt.ScrollPane scrollPane1;
    private java.awt.ScrollPane scrollPane10;
    private java.awt.ScrollPane scrollPane11;
    private java.awt.ScrollPane scrollPane12;
    private java.awt.ScrollPane scrollPane13;
    private java.awt.ScrollPane scrollPane14;
    private java.awt.ScrollPane scrollPane15;
    private java.awt.ScrollPane scrollPane16;
    private java.awt.ScrollPane scrollPane17;
    private java.awt.ScrollPane scrollPane18;
    private java.awt.ScrollPane scrollPane19;
    private java.awt.ScrollPane scrollPane2;
    private java.awt.ScrollPane scrollPane3;
    private java.awt.ScrollPane scrollPane4;
    private java.awt.ScrollPane scrollPane5;
    private java.awt.ScrollPane scrollPane6;
    private java.awt.ScrollPane scrollPane7;
    private java.awt.ScrollPane scrollPane8;
    private java.awt.ScrollPane scrollPane9;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    private JButton compareFolders, compareFiles;
    private DefaultMutableTreeNode leftRoot, rightRoot;
    private JTree leftTree, rightTree;
    private JList modifiedFiles;
    private DefaultListModel listModel;
    private DefaultTreeModel leftTreeModel, rightTreeModel;
    private JScrollPane leftScroll, rightScroll;
    private FolderComparatorMonitor comparator;
    private DiffProvider diffProvider;
    private String currentDir = "";
    private TitledBorder leftBorder, rightBorder;
    private boolean selection = false;

    private List<String> uniqueVar(List<String> combinedUniqueOp) {
        List<String> uniqueVariable = new ArrayList<String>();

        // List<String> list = Arrays.asList(variable);
        Set<String> set = new HashSet<String>(combinedUniqueOp);

        // System.out.print("Remove duplicate result: ");

        String[] result = new String[set.size()];
        set.toArray(result);

        for (String s : result) {
            uniqueVariable.add(s);
            // System.out.println(s + ", ");
        }
        return uniqueVariable;
    }

    private class FolderTreeRenderer extends DefaultTreeCellRenderer {

        private Icon dirIcon = UIManager.getIcon("FileView.directoryIcon");
        private Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

        public FolderTreeRenderer() {
            setOpaque(true);
        }
    }

    private class FolderComparatorMonitor extends Thread {

        private DirDiffResult result = null;
        private Cancellable cancellable;
        private File leftFolder, rightFolder;

        public FolderComparatorMonitor(File leftFolder, File rightFolder) {
            cancellable = new CancellableImpl();
            this.leftFolder = leftFolder;
            this.rightFolder = rightFolder;
        }

        public void cancel() {
            cancellable.cancel();
        }

        public void run() {
            try {
                result = FolderComparator.compareFolders(leftFolder, rightFolder, cancellable);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!cancellable.isCancelled()) {
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            onFinish(result);
                        }
                    });
                }
            }
        }
    }

    private void onFinish(DirDiffResult result) {
        mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        compareFolders.setEnabled(true);
        if (result == null) {
            // an error occured, display it
        } else {
            // build the trees
            leftBorder.setTitle(result.getLeftContent().getFile().getName());
            rightBorder.setTitle(result.getRightContent().getFile().getName());
            listModel.clear();
            leftRoot.removeAllChildren();
            leftTreeModel.nodeStructureChanged(leftRoot);
            rightRoot.removeAllChildren();
            rightTreeModel.nodeStructureChanged(rightRoot);

            buildModifiedFiles(result.getLeftContent());
            addNode(leftRoot, leftTreeModel, result.getLeftContent());
            addNode(rightRoot, rightTreeModel, result.getRightContent());
            leftTree.expandPath(new TreePath(new Object[]{leftRoot, leftRoot.getChildAt(0)}));
            rightTree.expandPath(new TreePath(new Object[]{rightRoot, rightRoot.getChildAt(0)}));

            mainPanel.repaint();
        }
    }

    private void buildModifiedFiles(DirContentStatus content) {
        if (content.getStatus() == Status.MODIFIED && !content.getFile().isDirectory()) {

            listModel.addElement(content);//.getFile().getName());
        }
        for (DirContentStatus child : content.getChildren()) {
            buildModifiedFiles(child);
        }
    }

    private void addNode(DefaultMutableTreeNode node, DefaultTreeModel treeModel, DirContentStatus dirContent) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(dirContent);//.getFile().getName());
        node.add(newNode);
        newNode.setParent(node);
        treeModel.nodesWereInserted(node, new int[]{node.getChildCount() - 1});

        dirContent.sort();
        List<DirContentStatus> children = dirContent.getChildren();
        for (DirContentStatus child : children) {
            addNode(newNode, treeModel, child);
        }
    }

    private void checkSelection() {
        TreePath leftPath = leftTree.getSelectionModel().getSelectionPath();
        if (leftPath != null) {
            DefaultMutableTreeNode leftNode = (DefaultMutableTreeNode) leftPath.getLastPathComponent();
            if (leftNode != null) {
                DirContentStatus status = (DirContentStatus) leftNode.getUserObject();
                compareFiles.setEnabled(status.getStatus() == Status.MODIFIED);
            } else {
                compareFiles.setEnabled(false);
            }
        } else {
            TreePath rightPath = rightTree.getSelectionModel().getSelectionPath();
            if (rightPath != null) {
                DefaultMutableTreeNode rightNode = (DefaultMutableTreeNode) rightPath.getLastPathComponent();
                if (rightNode != null) {
                    DirContentStatus status = (DirContentStatus) rightNode.getUserObject();
                    compareFiles.setEnabled(status.getStatus() == Status.MODIFIED);
                } else {
                    compareFiles.setEnabled(false);
                }
            } else if (modifiedFiles.getSelectedValue() != null) {
                DirContentStatus content = (DirContentStatus) modifiedFiles.getSelectedValue();
                compareFiles.setEnabled(content != null);
            } else {
                compareFiles.setEnabled(false);
            }
        }
    }

    public void compareFolders() {
                Window window = SwingUtilities.getWindowAncestor(mainPanel);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(currentDir));
        fileChooser.setDialogTitle("Select first folder");

        if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
            File leftFolder = fileChooser.getSelectedFile();
            currentDir = leftFolder.getParent();
            fileChooser.setDialogTitle("Select second folder");

            if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
                File rightFolder = fileChooser.getSelectedFile();
                compareFolders(
                        leftFolder, rightFolder);

            }
        }
//        System.out.println(" old Folder " + oldFolder);
//        System.out.println(" new Folder " + newFolder);
//        compareFolders(oldFolder, newFolder);
    }

    private void compareFiles() {
        TreePath leftPath = leftTree.getSelectionModel().getSelectionPath();
        if (leftPath != null) {
            DefaultMutableTreeNode leftNode = (DefaultMutableTreeNode) leftPath.getLastPathComponent();
            if (leftNode != null) {
                DirContentStatus leftStatus = (DirContentStatus) leftNode.getUserObject();
                compareFiles(leftStatus);
            }
        } else {
            TreePath rightPath = rightTree.getSelectionModel().getSelectionPath();
            if (rightPath != null) {
                DefaultMutableTreeNode rightNode = (DefaultMutableTreeNode) rightPath.getLastPathComponent();
                if (rightNode != null) {
                    DirContentStatus rightStatus = (DirContentStatus) rightNode.getUserObject();
                    compareFiles(rightStatus);
                }
            } else if (modifiedFiles.getSelectedValue() != null) {
                DirContentStatus content = (DirContentStatus) modifiedFiles.getSelectedValue();
                compareFiles(content);
            }
        }
    }

    private void compareFiles(DirContentStatus content) {
        if (diffProvider != null) {
            diffProvider.compareFiles(mainPanel, content.getFile(), content.getOtherFile());
        } else {
            Window window = SwingUtilities.getWindowAncestor(mainPanel);
            FileDiffDialog.showDiffDialog(window, content.getFile(), content.getOtherFile());
        }
    }

    public void compareFolders(File leftFolder, File rightFolder) {
        if (comparator != null) {
            comparator.cancel();
        }
        comparator = new FolderComparatorMonitor(leftFolder, rightFolder);
        comparator.start();
        mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        compareFolders.setEnabled(false);
    }
}