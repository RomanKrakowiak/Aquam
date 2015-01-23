package aquam;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.*;

/**
 *
 * @author RomanK
 */
public class GUIAquam extends JFrame implements ActionListener {

    static String address;
    static boolean newExperience = false;
    static boolean failFileChooser = false;

    // Menu 
    private JMenuBar jMenuBar;
    private JMenu jMenuExperience;
    private JMenuItem jExperienceNew;
    private JMenuItem jExperienceChange;
    private JMenuItem jExperienceErase;
    private JMenuItem jFileExit;
    private JMenu jMenuAbout;
    private JMenuItem jAboutInfo;
    private JMenuItem jAboutTutorial;
    private JMenu jMenuClose;
    private JMenuItem jCloseApp;

    // Paanel Text +Reset button
    private JPanel jPanelTexte;
    private JScrollPane jScrollPane;
    private JTextArea jZoneTexte;
    private JButton jResetTextButton;

    // |> + || buttons
    private JButton jPlayButton;
    private JButton jChooseFile;
    private JButton jStopButton;
    private JPanel jPanelPlayStop;

    /**
     *
     * Constructor
     */
    public GUIAquam() {
        // Creation de la GUI
        initGUIComponents();
        pack();
    }

    /**
     *
     * Creation of the Machine-User Interface
     */
    private void initGUIComponents() {
        // Creating the frame and the layout
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Aquam application");
        setMaximumSize(new java.awt.Dimension(1600, 1200));
        setMinimumSize(new java.awt.Dimension(320, 240));
        setPreferredSize(new java.awt.Dimension(640, 480));

        // Adding the menu
        jMenuBar = new javax.swing.JMenuBar();
        jMenuBar.setMinimumSize(new java.awt.Dimension(320, 22));
        jMenuBar.setSize(new java.awt.Dimension(320, 20));

        // Menu
        jMenuExperience = new JMenu("File");
        // Menu new
        jExperienceNew = new JMenuItem("New");
        jExperienceNew.addActionListener(this);
        // Menu Change
        jExperienceChange = new JMenuItem("Change");
        // Menu Erase 
        jExperienceErase = new JMenuItem("Erase");
        // Menu exit
        jFileExit = new JMenuItem("Exit");

        // Menu a propos
        jMenuAbout = new JMenu("About");
        jAboutInfo = new JMenuItem("Info");
        jAboutTutorial = new JMenuItem("Tutorial");

        // Menu close app
        jMenuClose = new JMenu("Close");
        jCloseApp = new JMenuItem("Close Application");
        jCloseApp.addActionListener(this);

        // Adding menu items to the menu
        jMenuExperience.add(jExperienceNew);
        jMenuExperience.add(jExperienceChange);
        jMenuExperience.add(jExperienceErase);
        jMenuExperience.add(jFileExit);

        jMenuAbout.add(jAboutInfo);
        jMenuAbout.add(jAboutTutorial);

        jMenuClose.add(jCloseApp);

        jMenuBar.add(jMenuExperience);
        jMenuBar.add(jMenuAbout);
        jMenuBar.add(jMenuClose);

        // Ajout du menu a la JFrame
        setJMenuBar(jMenuBar);

        //Layout setting
        getContentPane().setLayout(new BorderLayout());

        // Play/Pause button
        jPlayButton = new javax.swing.JButton("Process");
        jPlayButton.setVisible(true);
        jPlayButton.addActionListener(this);

        jChooseFile = new javax.swing.JButton("Choose File");
        jChooseFile.setVisible(false);
        jChooseFile.addActionListener(this);

        jStopButton = new JButton("Stop Process");
        jStopButton.addActionListener(this);
        //...
        // Adding the "Play"/Stop" buttons
        jPanelPlayStop = new JPanel(new FlowLayout());
        jPanelPlayStop.add(jPlayButton);
        jPanelPlayStop.add(jStopButton);
        jPanelPlayStop.add(jChooseFile);

        getContentPane().add(jPanelPlayStop, BorderLayout.SOUTH);

        // Un panneau permettant de gerer le defilement
        // et gestion des tailles minimales et preferees
        jScrollPane = new JScrollPane();
        jScrollPane.setMinimumSize(new java.awt.Dimension(300, 140));
        jScrollPane.setPreferredSize(new java.awt.Dimension(200, 100));

        // Text Area
        jZoneTexte = new JTextArea();
        jZoneTexte.setText("Test\n de texte dans \n la zone de texte !");

        // Ajout de la zone de texte dans le scroll pane
        jScrollPane.setViewportView(jZoneTexte);

        // Reset bouton        
        jResetTextButton = new JButton("Clear Text");
        jResetTextButton.setFocusable(false);
        jResetTextButton.addActionListener(this);

        // Ajout scroll pane/textarea
        jPanelTexte = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Ajout du scroll pane
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        jPanelTexte.add(jScrollPane, c);

        // Ajout du bouton
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 0.2;
        jPanelTexte.add(jResetTextButton, c);

        // Ajout the panel for the texte
        getContentPane().add(jPanelTexte, BorderLayout.CENTER);

        // Initialisation du plateau
        jZoneTexte.setText("");
    }

    /**
     *
     * Management of the ActionEvents
     *
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        System.out.println(cmd);

        switch (cmd) {
            case "New":
                newExperience = true;
                jChooseFile.setVisible(true);
                jZoneTexte.append("Choose a file\n");
                break;
            case "Choose File":
                address = fileChooser();
                if (failFileChooser) {
                    failFileChooser = false;
                    break;
                }
                jZoneTexte.append(address + " selected\n" + "Click on Process to import\n");
                break;
            case "Process":
                processCalled();
                break;
            case "Clear Text":
                jZoneTexte.setText("");
                break;
            case "Stop Process":
                jZoneTexte.append("Current process stopped\n");
                newExperience = false;
                jChooseFile.setVisible(false);
                failFileChooser = true;
                address = "";
                break;
            case "Close Application":
                this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                break;
            default:
                System.out.println("Trippy!");
                break;
        }
    }

    /**
     *
     * fileChooser() allows the user to search a file
     *
     * @return The adress of the file selected
     */
    private String fileChooser() {

        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
            if (file.getName().equals("xmlPropre.xml") || file.getName().equals("build.xml")) {
                jZoneTexte.append("You should not call the xml file: "+file.getName()+"\n" + "It is a xml used by the application to work.\n" + "Process canceled.\n");
                newExperience = false;
                jChooseFile.setVisible(false);
                failFileChooser = true;
            } else {
                return file.getName();
            }
        } else {
            jZoneTexte.append("Current process canceled by user.\n" + "Process canceled.\n");
            newExperience = false;
            jChooseFile.setVisible(false);
            failFileChooser = true;
        }
        return null;
    }

    /**
     *
     * This function manages what happens when we click on process
     */
    private void processCalled() {
        if (newExperience) {
            try {
                if (ExperienceXML.cleanXMLFile(address)) {
                    ExperienceXML.operationPrincipale(System.getProperty("user.dir") + "\\xmlPropre.xml");
                    jZoneTexte.append("The xml file has been imported in the DataBase successfully\n");
                }
            } catch (Exception exc) {
                jZoneTexte.append("Fail about inserting the xml file into the DataBase\n");
            }
            address = "";
            newExperience = false;
            jChooseFile.setVisible(false);
        }
    }
}
