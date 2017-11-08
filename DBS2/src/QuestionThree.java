/*
 * Implementation of question number one
 * 
 * Only contains specific code for the question. Handling of statistics gathering
 * is inherited from QuestionRunner
 */

import java.sql.*;

public class QuestionThree extends QuestionRunner {

   public QuestionThree(Connection iconn) {

      this.conn = iconn;
   }

   public void runCase() throws SQLException {

      Statement stmtMother = conn.createStatement();
      Statement stmtChild = conn.createStatement();
      int noOfChildren = 0;
      
      stmtMother.executeQuery("ALTER SYSTEM FLUSH SHARED_POOL");
      stmtChild.executeQuery("ALTER SYSTEM FLUSH SHARED_POOL");
      
      ResultSet rsMother = stmtMother.executeQuery("select id, hairColor from mother where hairColor='blond'");

     while (rsMother.next()) {
        /* if (rsMother.getString("hairColor").equals("blond")){*/ 
        
            ResultSet rsChild = stmtChild
                  .executeQuery("select id, yearBorn from" + " child where motherID = " + rsMother.getInt("id"));
            while (rsChild.next()) {
               if (rsChild.getInt("yearBorn") == 2012) {
                  
                  
                  noOfChildren++;
               }
            }
            rsChild.close();
            rsChild = null;
         }
      

    /*  rsMother.close();*/
      rsMother = null;
      stmtMother.close();
      stmtChild.close();

      System.out.println("number of children born in 2012 by blond mothers: " + noOfChildren);

   }
}

   


