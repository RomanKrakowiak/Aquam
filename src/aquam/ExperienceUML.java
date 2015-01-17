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

    public ExperienceUML() {
    }
   
   

   public static void afficherDatas()
   {
      //On crée une instance de SAXBuilder
      SAXBuilder sxb = new SAXBuilder();
      try
      {
         //On crée un nouveau document JDOM avec en argument le fichier XML
         //Le parsing est terminé 
         documentdoc = sxb.build(new File("C:\\Users\\RomanK\\Desktop\\easyaquamuml.xml"));
         
      }
      catch(Exception e){
          e.printStackTrace();
      }
      
      //On initialise un nouvel élément racine avec l'élément racine du document.
      racinedoc = documentdoc.getRootElement();
      
      

      
      afficheDatas(racinedoc);
      
   }
/*   
static void afficheALL()
{
    
    List listWorkbook = racinedoc.getChildren("Workbook");
    Iterator m = listWorkbook.iterator();
    while(m.hasNext())
    {
        
    
    Element courantWorkbook = (Element)m.next();
   
    List listWorksheet = courantWorkbook.getChildren("Worksheet");

    //On crée un Iterator sur notre liste
    Iterator i = listWorksheet.iterator();
    while(i.hasNext())
    {
      
       
       Element courantWorksheet = (Element)i.next();
       List listTable = courantWorksheet.getChildren("Table");

        //On crée un Iterator sur notre liste
       Iterator j = listTable.iterator();
       while(j.hasNext())
       {
           Element courantTable = (Element)j.next();
           List listRow = courantTable.getChildren("Row");

           //On crée un Iterator sur notre liste
           Iterator k = listRow.iterator();
           while(k.hasNext())
           {
               Element courantRow = (Element)k.next();
               List listCell = courantRow.getChildren("Cell");
               
               Iterator l = listCell.iterator();
               while(l.hasNext())
               {
                   Element courantCell = (Element)l.next();
                   Element courantData = courantCell.getChild("Data");
                   System.out.println(courantData.getValue());
                   System.out.println(courantData.getTextTrim());
                   switch (courantData.getAttributeValue("Type")) {
                       case "Number":
                           System.out.println(courantData.getValue());
                           break;
                       case "String":
                           System.out.println(courantData.getTextTrim());
                           break;
                   }
               }
                   
               
           }
            
        }  
    }
    }
}
*/
static void affiche()
{
   try
   {
      //On utilise ici un affichage classique avec getPrettyFormat()
      XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
      sortie.output(documentdoc, System.out);
   }
   catch (java.io.IOException e){}
}

static void afficheDatas(Element source){
    
    
    
    List listDescendants = source.getChildren();
    Iterator i = listDescendants.iterator();
    
    while(i.hasNext()){
        Element testSuivant = (Element)i.next();
        if (testSuivant.getName().equals("Data")){
            switch (testSuivant.getAttributeValue("Type")) {
                       case "Number":
                           System.out.println(testSuivant.getValue());
                           break;
                       case "String":
                           String nom = testSuivant.getTextTrim();
                           nom = nom.replace("%","percent");
                           nom = nom.replace("(","");
                           nom = nom.replace(")","");
                           nom = nom.replace("/","_per_");
                           nom = nom.replace("°","degre");
                           nom = nom.replace("²","square");
                           System.out.println(nom);
                           break;
                   }
            
            
            
            
        } else {
            afficheDatas(testSuivant);
        }
        
    }
    
    /*try{
    while(descendants.hasNext()){
        
        Element courantDescendant = (Element)descendants.next();
        
        if(courantDescendant.getName().equals("Data"))
        {
            switch (courantDescendant.getAttributeValue("Type")) {
                       case "Number":
                           System.out.println(courantDescendant.getValue());
                           break;
                       case "String":
                           System.out.println(courantDescendant.getTextTrim());
                           break;
                   }
        }
    }
    } catch(Exception e){e.printStackTrace();}*/
}
}
