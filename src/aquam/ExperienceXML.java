/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aquam;

/**
 *
 * @author rkrakowi
 */
import java.io.*;
import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.filter.*;
import java.util.List;
import java.util.Iterator;
import org.jdom2.output.*;
import java.util.regex.*;
import java.sql.*;
import org.postgresql.*;

public class ExperienceXML {

    /**
     * Document file containing the parsed xml file
     */
    static org.jdom2.Document documentdoc;
    /**
     * Have to be the racine of the xml file
     */
    static Element racinedoc;

    /**
     * Constructor
     */
    private ExperienceXML() {
    }

    /**
     * The main equivalent of ExperienceXML. It loads and parses the xml file.
     * Then it gets the racine Element and call the next function
     */
    public static void operationPrincipale() {
        //On crée une instance de SAXBuilder
        SAXBuilder sxb = new SAXBuilder();
        try {
            //On crée un nouveau document JDOM avec en argument le fichier XML
            //Le parsing est terminé 
            documentdoc = sxb.build(new File(System.getProperty("user.dir") + "\\easyaquamxml.xml"));

        } catch (Exception e) {

        }

        //On initialise un nouvel élément racine avec l'élément racine du document.
        racinedoc = documentdoc.getRootElement();

        insertExperience(racinedoc);

    }

    /**
     * Could be useful later. It permits to display the whole xml file
     */
    static void affiche() {
        try {
            //On utilise ici un affichage classique avec getPrettyFormat()
            XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
            sortie.output(documentdoc, System.out);
        } catch (java.io.IOException e) {
        }
    }

    /**
     *
     * Could be an useful function later. It is a recurssive function for
     * exploring the xml file
     *
     * @param source Have to be the element you wantto explore
     */
    static void afficheDatas(Element source) {

        List listDescendants = source.getChildren();
        Iterator i = listDescendants.iterator();

        while (i.hasNext()) {
            Element testSuivant = (Element) i.next();
            if ("Data".equals(testSuivant.getName())) {
                switch (testSuivant.getAttributeValue("Type")) {
                    case "Number":
                        System.out.println(testSuivant.getValue());
                        break;
                    case "String":
                        String nom = testSuivant.getTextTrim();
                        nom = makeStringSQLReadable(nom);
                        System.out.println(nom);
                        break;
                    default:
                        break;
                }

            } else {
                afficheDatas(testSuivant);
            }

        }

    }

    /**
     *
     * @param source Have to be the racine of the xml file
     * @return The first row element, it is only useful if all the parameters
     * name are in the first row (and the values in the others)
     */
    static Element searchFirstRow(Element source) {

        List listDescendants = source.getChildren();
        Iterator i = listDescendants.iterator();

        while (i.hasNext()) {
            Element testSuivant = (Element) i.next();
            if ("Row".equals(testSuivant.getName())) {

                if (testSuivant.getAttributeValue("Index").equals(String.valueOf(1))) {
                    return testSuivant;
                }

            } else {
                Element answer = searchFirstRow(testSuivant);
                if (answer != null) {
                    return answer;
                }
            }

        }
        return null;
    }

    /**
     *
     * This function give the final String to put in postgreSQL to insert an
     * experience Datas. It uses insertEtape for each etape
     *
     * @param source Have to be the racine of the xml file
     */
    static void insertExperience(Element source) {
        String experienceSQLCode;
        experienceSQLCode = "";
        try {

            List listDescendants = source.getChildren();
            Iterator i = listDescendants.iterator();
            String rowIndexValue;

            boolean count = false;

            while (i.hasNext()) {
                Element testSuivant = (Element) i.next();
                if ("Row".equals(testSuivant.getName())) {
                    rowIndexValue = testSuivant.getAttributeValue("Index");
                    if (Integer.valueOf(rowIndexValue) > 1) {
                        if (count) {
                            experienceSQLCode = experienceSQLCode + "; ";
                        } else {
                            count = true;
                        }
                        experienceSQLCode = experienceSQLCode + insertEtape(testSuivant.getChildren("Cell").iterator());
                    }

                } else {
                    insertExperience(testSuivant);
                }

            }

        } catch (Exception e) {

        }
        exportToDataBase(experienceSQLCode);
    }

    /**
     *
     * @param dataTraveler which is an iterator of Cells in a Row. Each Cell
     * contain a Data to put in the "values" String
     * @return a String which is an iteration of "Insert
     * into(parameters)values(values);" of the .xml doc
     */
    static String insertEtape(Iterator dataTraveler) {

        //the "parameters" String
        String requestAttributeName;
        //the "values" String
        String requestAttributeValue;
        requestAttributeName = "";
        requestAttributeValue = "";

        //used to have the parameters and values in the same order
        String stringIndex;
        int numIndex;
        //"values" temporary String for different tests
        String temp;
        temp = "";
        //"parameters" temporary String for different tests
        String tempName;
        tempName = "";

        //permits to not put a "," at the very beginning of "parameters" ans "values"
        boolean count;
        //permits to stop searching for a name in "parameters" if u already found it 
        boolean search;
        //permit to ignore a Cell-Data if the Data is null
        boolean notNullValue;
        count = false;
        notNullValue = true;

        //Element nécessaire pour parcourir le Row1 contenant les noms de "parameters" lorsqu'on parcourera les autres Row contenant les données
        Element firstRow = searchFirstRow(racinedoc);
        while (dataTraveler.hasNext()) {
            Iterator nameData = firstRow.getChildren("Cell").iterator();
            notNullValue = true;
            Element newData = (Element) dataTraveler.next();
            stringIndex = newData.getAttributeValue("Index");
            numIndex = Integer.valueOf(stringIndex);

            //récupération de la Data destinée à remplir "values"
            Element data = (Element) newData.getChild("Data");
            switch (data.getAttributeValue("Type")) {
                case "Number":
                    temp = data.getValue();
                    break;
                case "String":
                    temp = data.getTextTrim();
                    temp = makeStringSQLReadable(temp);
                    break;
                default:
                    break;

            }
            if ("".equals(temp) || temp.isEmpty()) {
                notNullValue = false;
            }

            if (notNullValue) {
                if (count) {
                    requestAttributeName = requestAttributeName + ", ";
                    requestAttributeValue = requestAttributeValue + ", ";
                } else {
                    count = true;
                }

                //recherche du nom à mettre dans "parameters" correspondant
                search = true;
                while (nameData.hasNext() && search) {

                    Element cellPossibility = (Element) nameData.next();
                    if (cellPossibility.getAttributeValue("Index").equals(stringIndex)) {
                        search = false;
                        tempName = cellPossibility.getChildTextTrim("Data");
                        tempName = makeStringSQLReadable(tempName);
                        requestAttributeName = requestAttributeName + tempName;
                    }

                }

                //pour la grammaire postgreSQL
                if ("String".equals(data.getAttributeValue("Type"))) {
                    temp = "'" + temp + "'";
                }
                requestAttributeValue = requestAttributeValue + temp;
            }

        }

        return "\n\n" + "INSERT INTO\n" + "etape(" + requestAttributeName + ")\n" + "VALUES\n" + "(" + requestAttributeValue + ")";
    }

    /**
     *
     * @param a String you want to convert into a postgreSQL ready command
     * @return String which can be read by postgreSQL
     */
    static String makeStringSQLReadable(String a) {
        String l = a;
        l = l.replace("%", "percent");
        l = l.replace("(", "");
        l = l.replace(")", "");
        l = l.replace("/", "_per_");
        l = l.replace("°", "degre");
        l = l.replace("²", "square");
        l = l.replace("->", "_to_");
        l = l.replace("Group_Type", "GroupType");
        l = l.replace("Group", "Group_");
        return l;
    }
    
    /**
     * 
     * This function inserts the datas in the database
     * @param query is the String of the final SQL query
     */
    static void exportToDataBase(String query) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234");
            con.setAutoCommit(false);
            System.out.println("Opened database successfully");

            Statement stmt;
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            System.out.println(query);

            stmt.close();
            con.commit();
            con.close();
        } catch (java.lang.ClassNotFoundException e) {
            System.err.println("ClassNotFoundException:" + e.getMessage());
        } catch (SQLException ex) {
            System.err.println("SQLException:" + ex.getMessage());
        }
        System.out.println("Records created successfully");
    }

}
