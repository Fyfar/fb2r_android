/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fb2parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Александр
 */
public class FB2Parser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Collection<String> list = null;
        if (args.length != 0){
            try {
                //list = DataAccess.OpenFile(args[0]);
                //Iterator iter = list.iterator();
                //while (iter.hasNext()){
                //    System.out.println(iter.next());
                //}
                //DataAccess.SaveFileLikeTXT(list);
                //DataAccess.tryToParseXML(args[0]);
                //list = new ArrayList<>();
                BookInfo book = new BookInfo(DataAccess.getParsedToDocumentBook(args[0]));
                book.printAllInfoAboutBook();
            }
            catch (Exception ex){
                System.out.print(ex.toString());
            }
            finally {
                System.out.print("End of program");
            }
        }
        else System.out.print("command line dont have arguments");
    }
    
}
