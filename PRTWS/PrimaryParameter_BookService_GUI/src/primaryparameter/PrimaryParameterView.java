/*
 * PrimaryParameterView.java
 */
package primaryparameter;

import reducedtestcasedata.ParsingTestSuite;
import reducedtestcasedata.TestCases;

import CodeBreak.VisitingClassMethod.MethodVisitor;
import CodeBreak.stat.JCallGraph;
import TEST.CallFileParse;

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
import java.io.FileNotFoundException;
import javax.swing.DefaultListModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * The application's main frame.
 */
public class PrimaryParameterView extends FrameView {

    Object Y, Z;
    String inputList3[] = new String[100];
    String inputList4[] = new String[100];
    String buildTestCase[] = new String[100];
    int I = 0, J = 0;
    String TC[][] = new String[100][100];
    String testCase = "";
    String parameterOp[] = new String[100];
    List<String> operation = new ArrayList<String>();
    String opArray[] = new String[100];
    File WSDLCFG;
    File testSuiteFile7;
    Object X;
    String diffOp[] = new String[100];
    String reduceOp[] = new String[100];
    String combinedOp[] = new String[100];
    List<String> combinedUniqueOp = new ArrayList<String>();
    String inputList2[] = new String[100];
    int j = 0, x;
    File testSuiteFile1 = null, testSuiteFile2 = null, testSuiteFile6 = null;
    private String op;
    String WSDLOp[] = new String[100];
    List<String> methodsAffected = new ArrayList<String>();
    int randomInt;
    File oldFolder, newFolder;
    String callGraphFile;
    File temporaryRRTS6;

    public PrimaryParameterView(SingleFrameApplication app) {
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
            JFrame mainFrame = PrimaryParameterApp.getApplication().getMainFrame();
            aboutBox = new PrimaryParameterAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        PrimaryParameterApp.getApplication().show(aboutBox);
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
        scrollPane14 = new java.awt.ScrollPane();
        jButton5 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        scrollPane2 = new java.awt.ScrollPane();
        jButton6 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList();
        scrollPane1 = new java.awt.ScrollPane();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jButton7 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
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

        mainPanel.setName("mainPanel"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(primaryparameter.PrimaryParameterApp.class).getContext().getResourceMap(PrimaryParameterView.class);
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
            .addGap(0, 464, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 492, Short.MAX_VALUE)
        );

        scrollPane14.setName("scrollPane14"); // NOI18N

        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setName("jButton9"); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton13)
                        .addGap(15, 15, 15)
                        .addComponent(jButton5))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton15)
                        .addGap(48, 48, 48))
                    .addComponent(scrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton17))
                    .addComponent(scrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addComponent(scrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton15)
                    .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton13)
                    .addComponent(jButton5)
                    .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                    .addComponent(scrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                    .addComponent(scrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
                .addGap(109, 109, 109))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        scrollPane2.setName("scrollPane2"); // NOI18N

        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jList4.setName("jList4"); // NOI18N
        jScrollPane4.setViewportView(jList4);

        scrollPane1.setName("scrollPane1"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

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

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jList2.setName("jList2"); // NOI18N
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jList2);

        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jList3.setName("jList3"); // NOI18N
        jScrollPane3.setViewportView(jList3);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jList1.setName("jList1"); // NOI18N
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(104, 104, 104)
                                .addComponent(jLabel2)))
                        .addGap(34, 34, 34))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                        .addGap(23, 23, 23)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton6)
                                .addGap(68, 68, 68)
                                .addComponent(jButton7))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6)))
                        .addGap(62, 62, 62)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton8)
                            .addComponent(jLabel3))
                        .addGap(99, 99, 99)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(138, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                        .addGap(15, 15, 15))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2)
                            .addComponent(jButton3)
                            .addComponent(jButton6)
                            .addComponent(jButton7)
                            .addComponent(jButton8)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(6, 6, 6)
                        .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2))
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE))))
                .addGap(59, 59, 59))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1151, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 673, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(primaryparameter.PrimaryParameterApp.class).getContext().getActionMap(PrimaryParameterView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1161, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 991, Short.MAX_VALUE)
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
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //File testSuiteFile6;
        I = 0;
        J = 0;

        testCase = "";
        final String inputList[] = new String[100];
        for (int i = 0; i < 100; i++) {
            inputList[i] = null;
            inputList3[i] = null;
            inputList4[i] = null;
            buildTestCase[i] = null;
        }
        for (int i = 0; i < 100; i++) {
            for (int k = 0; i < 100; i++) {
                TC[i][k] = null;
            }
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

        jList3.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        jList4.setModel(new javax.swing.AbstractListModel() {

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
            testSuiteFile7 = fileopen.getSelectedFile();

        }
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile7);
        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(RWsaxTree3);
            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile7)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }
        JTree tree = new JTree(RWsaxTree3.getTree());
        scrollPane1.add(tree);
        ParsingTestSuite obj = new ParsingTestSuite();

        //String [] output = null;
        try {
            TC = obj.testCases(testSuiteFile7);
        } catch (Exception ex) {
            Logger.getLogger(PrimaryParameterView.class.getName()).log(Level.SEVERE, null, ex);
        }
        final String[] output;
        output = new String[100];
        int k = 0;

        for (; TC[k][0] != null; k++) {
            output[k] = TC[k][0];
            //System.out.println(TC[j]);
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
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int jList1Anchor = jList1.getAnchorSelectionIndex();
        Y = jList1.getModel().getElementAt(jList1Anchor);

        System.out.print(Y.toString());
        inputList3[I] = Y.toString();
        I++;

        jList2.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList3;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        int jList2Anchor = jList2.getAnchorSelectionIndex();
        System.out.println(" X  " + Y.toString() + " x " + jList2Anchor);
        Object jListAnchorObj = jList2.getModel().getElementAt(jList2Anchor);

        System.out.print(jListAnchorObj.toString());
        for (int i = 0; inputList3[i] != null; i++) {
            if (inputList3[i] == jListAnchorObj.toString()) {
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
        jList2.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList3;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            File temporaryRRTS = new File(testSuiteFile7.getParent() + "\\temporaryRRTS\\");
            temporaryRRTS.mkdirs();
            File ReduceTS = File.createTempFile("ReduceTestStep_RRTS", ".xml", new File(temporaryRRTS.getAbsolutePath()));
            String[] s;
            s = new String[100];
            for (int i = 0; inputList3[i] != null; i++) {
                s[i] = inputList3[i];
                System.out.println(" s " + i + " " + s[i]);
            }

//            try {
//                TestCases obj = new TestCases();
//
//                String z = obj.buildTestCase(testSuiteFile7, s);
//                testCase = testCase.concat(z);
//                System.out.println("testCase" + testCase);
//            } catch (Exception e) {
//            }

            reducedtestcasedata.Construction testCaseConstructionObj = new reducedtestcasedata.Construction();
            try {
                testCaseConstructionObj.ConstructReduceTC(testSuiteFile7, testCase, ReduceTS);
            } catch (IOException ex) {
                Logger.getLogger(PrimaryParameterView.class.getName()).log(Level.SEVERE, null, ex);
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
            scrollPane2.add(tree);
        } catch (IOException ex) {
            Logger.getLogger(PrimaryParameterView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseClicked
        int jList2Anchor = jList2.getAnchorSelectionIndex();
        Object jList2AnchorObj = jList2.getModel().getElementAt(jList2Anchor);
        int i = 0;
        final String s[] = new String[100];
        buildTestCase[0] = jList2AnchorObj.toString();
        for (; TC[i][0] != null; i++) {
            if (jList2AnchorObj.toString() == TC[i][0] && TC[i][0] != null) {
                for (int k = 1; TC[i][k] != null; k++) {
                    s[k - 1] = TC[i][k];
                }
            }
        }

        final String inputList[] = new String[100];
        for (int k = 0; k < 100; k++) {
            inputList[k] = null;
            inputList4[k] = null;
            buildTestCase[k] = null;
        }
        J = 0;
        jList4.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        jList3.setModel(new javax.swing.AbstractListModel() {

            String[] strings = s;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
    }//GEN-LAST:event_jList2MouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        int jList3Anchor = jList3.getAnchorSelectionIndex();
        Z = jList3.getModel().getElementAt(jList3Anchor);

        System.out.print(Z.toString());
        inputList4[J] = Z.toString();
        J++;

        jList4.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList4;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        int jList4Anchor = jList4.getAnchorSelectionIndex();
        System.out.println(" X  " + Z.toString() + " x " + jList4Anchor);
        Object jList4AnchorObj = jList4.getModel().getElementAt(jList4Anchor);

        System.out.print(jList4AnchorObj.toString());
        for (int i = 0; inputList4[i] != null; i++) {
            if (inputList4[i] == jList4AnchorObj.toString()) {
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
        jList4.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList4;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        int jList2Anchor = jList2.getAnchorSelectionIndex();
        Z = jList2.getModel().getElementAt(jList2Anchor);
        buildTestCase[0] = Z.toString();

        for (int i = 0; inputList4[i] != null; i++) {
            buildTestCase[i + 1] = inputList4[i];
        }

        jList4.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList4;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        try {
            for (int i = 0; buildTestCase[i] != null; i++) {
                System.out.println(" test cases/steps " + buildTestCase[i]);
            }

            TestCases obj = new TestCases();
            String z = obj.buildTestCase(testSuiteFile7, buildTestCase);
            testCase = testCase.concat(z);
        } catch (Exception e) {
        }//(testSuiteFile6, buildTestCase);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        try {
            File oldDir = null;
            JFileChooser fileopen = new JFileChooser();
            fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = fileopen.showDialog(null, "Open file");
            if (ret == JFileChooser.APPROVE_OPTION) {
                oldDir = fileopen.getSelectedFile();
            }

            File[] oldFile = oldDir.listFiles();
            Random randomGenerator = new Random();
            randomInt = randomGenerator.nextInt(100);

            oldFolder = new File(oldDir.getParent() + "\\temporaryCodeBreak\\Old" + randomInt);
            oldFolder.mkdirs();
            System.out.println("OLDFILE " + oldFile[0]);
            for (int i = 0; oldFile[i] != null; i++) {
                System.out.println("OLDFILE " + oldFile[i]);
                CompilationUnit cu;
                cu = JavaParser.parse(oldFile[i]);
                new MethodVisitor().visit(cu, oldFolder.getAbsolutePath());
            }
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        try {
            File newDir = null;
            JFileChooser fileopen = new JFileChooser(oldFolder.getParentFile().getParent());
            fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int ret = fileopen.showDialog(null, "Open file");
            if (ret == JFileChooser.APPROVE_OPTION) {
                newDir = fileopen.getSelectedFile();
            }

            File[] newFile = newDir.listFiles();
            newFolder = new File(newDir.getParent() + "\\temporaryCodeBreak\\New" + randomInt);
            newFolder.mkdirs();
            System.out.println("NEWFILE " + newFile[0]);
            for (int i = 0; newFile[i] != null; i++) {
                System.out.println("NEWFILE " + newFile[i]);
                CompilationUnit cu;
                cu = JavaParser.parse(newFile[i]);
                new MethodVisitor().visit(cu, newFolder.getAbsolutePath());
            }
        } catch (Exception e) {
        }
}//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        //File file = null;
        JFileChooser fileopen = new JFileChooser(oldFolder.getParentFile().getParentFile().getParent());
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            WSDLCFG = fileopen.getSelectedFile();
        }

        SAXTreeBuilder saxTree1 = null;

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(WSDLCFG);
        saxTree1 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(saxTree1);
            saxParser.parse(new InputSource(new FileInputStream(WSDLCFG)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }

        BuildingReducedWSDL.Search searchObject = new BuildingReducedWSDL.Search();
        //String[] reduceOp = null;
        //String [] output = null;
        try {
            WSDLOp = searchObject.search(WSDLCFG.getPath());
        } catch (IOException ex) {
            Logger.getLogger(PrimaryParameterView.class.getName()).log(Level.SEVERE, null, ex);
        }

        JTree tree = new JTree(saxTree1.getTree());
        scrollPane9.add(tree);
}//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        try {
            File temporaryPWSDL = new File(WSDLCFG.getParent() + "\\temporaryPWSDL\\");
            temporaryPWSDL.mkdirs();
            File parameterWSDL = File.createTempFile("ParameterWSDL", ".wsdl", new File(temporaryPWSDL.getAbsolutePath()));

            String[] inputList5 = new String[100];
            Object A;
            for (int k = 0; k < modifiedFiles.getModel().getSize(); k++) {       //= jList1.getAnchorSelectionIndex();
                String parameterOperation;
                A = modifiedFiles.getModel().getElementAt(k);
                System.out.print("A " + A.toString());

                int start = A.toString().lastIndexOf("\\");
                int end = A.toString().indexOf(".");
                parameterOperation = A.toString().substring(start + 1, end);

                inputList5[k] = parameterOperation;
                parameterOp[k] = parameterOperation;
            }

            File dir = new File(callGraphFile);
            Scanner fileToRead = null;
            StringBuffer callGraphBuffer = new StringBuffer();

            try {
                fileToRead = new Scanner(dir); //point the scanner method to a file
                //check if there is a next line and it is not null and then read it in
                for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
                    callGraphBuffer.append(line).append("\r\n");//this small line here is to appened all text read in from the file to a string buffer which will be used to edit the contents of the file
                }
                fileToRead.close();//this is used to release the scanner from file
            } catch (FileNotFoundException ex) {
            }

            methodsAffected = CallFileParse.recursion(callGraphBuffer.toString(), parameterOp);
            for (int z = 0; parameterOp.length > z && parameterOp[z] != null; z++) {
                methodsAffected.add(parameterOp[z]);
                System.out.println("parameterOp[i] " + parameterOp[z]);
            }
            System.out.println(methodsAffected);

            for (int i = 0; WSDLOp.length > i && WSDLOp[i] != null; i++) {
                for (String string : methodsAffected) {
                    System.out.println("WSDL operation " + WSDLOp[i]);
                    System.out.println("method affected " + string);
                    if (string.equals(WSDLOp[i])) {
                        System.out.println("operation" + WSDLOp[i]);
                        operation.add(WSDLOp[i]);
                    }
                }
            }
            BuildingReducedWSDL.Construction constructParameterWSDL = new BuildingReducedWSDL.Construction();
            operation = uniqueVar(operation);

            int a = 0;
            for (String s : operation) {
                System.out.print("\nunique combined op " + s);
                opArray[a] = s;
                a++;
            }

            constructParameterWSDL.ConstructReduceWSDL(WSDLCFG, opArray, parameterWSDL); // TODO add your handling code here:
            DefaultMutableTreeNode top = new DefaultMutableTreeNode(parameterWSDL);
            SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

            try {
                SAXParser saxParser = new SAXParser();
                saxParser.setContentHandler(RWsaxTree3);
                saxParser.parse(new InputSource(new FileInputStream(parameterWSDL)));
            } catch (Exception ex) {
                top.add(new DefaultMutableTreeNode(ex.getMessage()));
            }
            JTree tree = new JTree(RWsaxTree3.getTree());
            scrollPane14.add(tree);
        } catch (IOException ex) {
            Logger.getLogger(PrimaryParameterView.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        JFileChooser fileopen = new JFileChooser(WSDLCFG.getParentFile().getParent());
        FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showDialog(null, "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            testSuiteFile6 = fileopen.getSelectedFile();
        }
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile6);
        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(RWsaxTree3);
            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile6)));

        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));

        }
        JTree tree = new JTree(RWsaxTree3.getTree());
        scrollPane10.add(tree);
}//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        try {
            //modifiedFiles.getModel().getElementAt(x);
            File temporaryRRTSFolder6 = new File(testSuiteFile6.getParent() + "\\temporaryRRTS\\");
            temporaryRRTSFolder6.mkdirs();
            temporaryRRTS6 = File.createTempFile("PRRTS", ".xml", new File(temporaryRRTSFolder6.getAbsolutePath()));

            reducedtestcasedata.Construction testCaseConstructionObj = new reducedtestcasedata.Construction();
            //        WSDLOp[0] = "GetAllVerseByBookAndChapterNumber";
            //        WSDLOp[1] = "FindBookNumber";
            //        WSDLOp[2] = "GetVerseByBooKAndChapterAndVerseNumber";
            //        WSDLOp[3] = "GetAbstractOfChapter";
            testCase = "";
            for (String string : methodsAffected) {
                buildTestCase[0] = string;
                System.out.println("buildtestCase[0] " + buildTestCase[0]);
                for (int i = 0; parameterOp[i] != null && parameterOp.length > i; i++) {
                    buildTestCase[i + 1] = parameterOp[i];
                    System.out.println("buildTestCase[i + 1] " + buildTestCase[i + 1]);
                }
                try {
                    TestCases obj = new TestCases();
                    String z = obj.buildTestCase(testSuiteFile6, buildTestCase);
                    testCase = testCase.concat(z);
                    System.out.println("testCase" + testCase);
                } catch (Exception e) {
                } //(testSuiteFile6, buildTestCase);
                //(testSuiteFile6, buildTestCase);
            }
            try {
                testCaseConstructionObj.ConstructReduceTC(testSuiteFile6, testCase, temporaryRRTS6);
            } catch (IOException ex) {
                Logger.getLogger(PrimaryParameterView.class.getName()).log(Level.SEVERE, null, ex);
            }
            DefaultMutableTreeNode top = new DefaultMutableTreeNode(temporaryRRTS6);
            SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);
            try {
                SAXParser saxParser = new SAXParser();
                saxParser.setContentHandler(RWsaxTree3);
                saxParser.parse(new InputSource(new FileInputStream(temporaryRRTS6)));
            } catch (Exception ex) {
                top.add(new DefaultMutableTreeNode(ex.getMessage()));
            }
            JTree tree = new JTree(RWsaxTree3.getTree());
            scrollPane11.add(tree); // TODO add your handling code here:
        } catch (IOException ex) {
            Logger.getLogger(PrimaryParameterView.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButton17ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        File warFile = null;
        JCallGraph obj = new JCallGraph();
        JFileChooser fileopen = new JFileChooser(oldFolder.getParentFile().getParentFile().getParent());
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            warFile = fileopen.getSelectedFile();
        }

        obj.callGraphFile(warFile.getAbsolutePath(), randomInt);
        callGraphFile = warFile.getParent() + "//CallGraph//" + randomInt + ".txt";
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        testSuiteFile7 = temporaryRRTS6;
        //        jTextField16.setText(testSuiteFile7.getPath() + File.separator + testSuiteFile7.getName());
        I = 0;
        J = 0;

        testCase = "";
        final String inputList[] = new String[100];
        for (int i = 0; i < 100; i++) {
            inputList[i] = null;
            inputList3[i] = null;
            inputList4[i] = null;
            buildTestCase[i] = null;
        }
        for (int i = 0; i < 100; i++) {
            for (int k = 0; k < 100; k++) {
                TC[i][k] = null;
            }
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

        jList3.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        jList4.setModel(new javax.swing.AbstractListModel() {

            String[] strings = inputList;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(testSuiteFile7);
        SAXTreeBuilder RWsaxTree3 = new SAXTreeBuilder(top);

        try {
            SAXParser saxParser = new SAXParser();
            saxParser.setContentHandler(RWsaxTree3);
            saxParser.parse(new InputSource(new FileInputStream(testSuiteFile7)));
        } catch (Exception ex) {
            top.add(new DefaultMutableTreeNode(ex.getMessage()));
        }
        JTree tree = new JTree(RWsaxTree3.getTree());
        scrollPane1.add(tree);
        ParsingTestSuite obj = new ParsingTestSuite();

        //String [] output = null;
        try {
            TC = obj.testCases(testSuiteFile7);
        } catch (Exception ex) {
            Logger.getLogger(PrimaryParameterView.class.getName()).log(Level.SEVERE, null, ex);
        }
        final String[] output;
        output = new String[100];


        for (int k = 0; TC[k][0] != null; k++) {
            output[k] = TC[k][0];
            //System.reduceOp.println(TC[j]);
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
    }//GEN-LAST:event_jButton9ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JList jList4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private java.awt.ScrollPane scrollPane1;
    private java.awt.ScrollPane scrollPane10;
    private java.awt.ScrollPane scrollPane11;
    private java.awt.ScrollPane scrollPane14;
    private java.awt.ScrollPane scrollPane2;
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

    private List<String> uniqueVar(List<String> operation) {
        List<String> uniqueVariable = new ArrayList<String>();

        // List<String> list = Arrays.asList(variable);
        Set<String> set = new HashSet<String>(operation);

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

            buildModifiedFiles(
                    result.getLeftContent());
            addNode(
                    leftRoot, leftTreeModel, result.getLeftContent());
            addNode(
                    rightRoot, rightTreeModel, result.getRightContent());
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
//        Window window = SwingUtilities.getWindowAncestor(mainPanel);
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        fileChooser.setCurrentDirectory(new File(currentDir));
//        fileChooser.setDialogTitle("Select first folder");
//
//        if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
//            File leftFolder = fileChooser.getSelectedFile();
//            currentDir = leftFolder.getParent();
//            fileChooser.setDialogTitle("Select second folder");
//
//            if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
//                File rightFolder = fileChooser.getSelectedFile();
//                compareFolders(
//                        leftFolder, rightFolder);
//
//            }
//        }
        System.out.println(" old Folder " + oldFolder);
        System.out.println(" new Folder " + newFolder);
        compareFolders(oldFolder, newFolder);
    }

    private void compareFiles() {
        TreePath leftPath = leftTree.getSelectionModel().getSelectionPath();

        if (leftPath != null) {
            DefaultMutableTreeNode leftNode = (DefaultMutableTreeNode) leftPath.getLastPathComponent();

            if (leftNode != null) {
                DirContentStatus leftStatus = (DirContentStatus) leftNode.getUserObject();
                compareFiles(
                        leftStatus);
            }
        } else {
            TreePath rightPath = rightTree.getSelectionModel().getSelectionPath();

            if (rightPath != null) {
                DefaultMutableTreeNode rightNode = (DefaultMutableTreeNode) rightPath.getLastPathComponent();

                if (rightNode != null) {
                    DirContentStatus rightStatus = (DirContentStatus) rightNode.getUserObject();
                    compareFiles(
                            rightStatus);
                }
            } else if (modifiedFiles.getSelectedValue() != null) {
                DirContentStatus content = (DirContentStatus) modifiedFiles.getSelectedValue();
                compareFiles(
                        content);
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
