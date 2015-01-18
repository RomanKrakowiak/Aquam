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

public class ExperienceUML {
   static org.jdom2.Document documentdoc;
   static Element racinedoc;

    private ExperienceUML() {
    }
   
   

   public static void operationPrincipale(){
      //On crée une instance de SAXBuilder
      SAXBuilder sxb = new SAXBuilder();
      try{
         //On crée un nouveau document JDOM avec en argument le fichier XML
         //Le parsing est terminé 
         documentdoc = sxb.build(new File("C:\\Users\\RomanK\\Desktop\\easyaquamuml.xml"));
         
      } catch(Exception e){
          
      }
      
      //On initialise un nouvel élément racine avec l'élément racine du document.
      racinedoc = documentdoc.getRootElement();
      
      

      
      insertExperience(racinedoc);
      
   }

static void affiche(){
   try{
      //On utilise ici un affichage classique avec getPrettyFormat()
      XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
      sortie.output(documentdoc, System.out);
   }catch (java.io.IOException e){
   }
}

static void afficheDatas(Element source){
    
    
    
    List listDescendants = source.getChildren();
    Iterator i = listDescendants.iterator();
    
    while(i.hasNext()){
        Element testSuivant = (Element)i.next();
        if ("Data".equals(testSuivant.getName())){
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

static Element searchFirstRow(Element source){
    
    
    
    List listDescendants = source.getChildren();
    Iterator i = listDescendants.iterator();
    
    while(i.hasNext()){
        Element testSuivant = (Element)i.next();
        if ("Row".equals(testSuivant.getName())){
            
            if(testSuivant.getAttributeValue("Index").equals(String.valueOf(1))){
                return testSuivant;
            }
            
            
        } else {
            Element answer = searchFirstRow(testSuivant);
            if (answer != null){
                return answer;
            }
        }
        
    }
    return null;
}
    
static void insertExperience(Element source){
    try{
    
    
    List listDescendants = source.getChildren();
    Iterator i = listDescendants.iterator();
    String rowIndexValue;
    String experienceSQLCode;
    experienceSQLCode="";
    boolean count = false;
    
    while(i.hasNext()){
        Element testSuivant = (Element)i.next();
        if ("Row".equals(testSuivant.getName())){ 
            rowIndexValue = testSuivant.getAttributeValue("Index");
            if(Integer.valueOf(rowIndexValue)>1){
                if (count){
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
    System.out.println(experienceSQLCode);
    } catch (Exception e){
        
    }
}

static String insertEtape(Iterator dataTraveler){
    String requestAttributeName;
    String requestAttributeValue;
    requestAttributeName = "";
    requestAttributeValue = "";
    String stringIndex;
    int numIndex;
    String temp;
    temp = "";
    String tempName;
    tempName = "";
    boolean count;
    boolean search;
    boolean notNullValue;
    count = false;
    notNullValue = true;
    
    Element firstRow = searchFirstRow(racinedoc);
    Iterator nameData = firstRow.getChildren("Cell").iterator();
    while(dataTraveler.hasNext()){
        notNullValue = true;
        Element newData = (Element)dataTraveler.next();
        stringIndex = newData.getAttributeValue("Index");
        numIndex = Integer.valueOf(stringIndex);
        Element data = (Element)newData.getChild("Data");
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
        if("".equals(temp) || temp.isEmpty()){
            notNullValue=false;
        }
        
        if(notNullValue){
            if(count){
                requestAttributeName = requestAttributeName + ", ";
                requestAttributeValue = requestAttributeValue +", ";
            }else{
                count=true;
            }
        
        
        
            search = true;
            while(nameData.hasNext() && search){
            
                Element cellPossibility =(Element)nameData.next();
                if(cellPossibility.getAttributeValue("Index").equals(stringIndex)){
                    search = false;
                    tempName = cellPossibility.getChildTextTrim("Data");
                    tempName = makeStringSQLReadable(tempName);
                    requestAttributeName = requestAttributeName + tempName;
                }
            
            }
            if("String".equals(data.getAttributeValue("Type"))){
                temp = "'"+temp+"'";
            }
            requestAttributeValue = requestAttributeValue + temp;
        }
        
        
    }
    
    
    return "INSERT INTO etape(" + requestAttributeName + ") VALUES(" + requestAttributeValue + ")";
}

static String makeStringSQLReadable(String a){
    String l = a;
    l = l.replace("%","percent");
    l = l.replace("(","");
    l = l.replace(")","");
    l = l.replace("/","_per_");
    l = l.replace("°","degre");
    l = l.replace("²","square");
    l = l.replace("->","_to_");
    l = l.replace("Group_Type","GroupType");
    l = l.replace("Group","Group_");
    return l;
}

}
