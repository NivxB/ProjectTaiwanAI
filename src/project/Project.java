/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kbarahona
 */
public class Project {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            double[][] MA = {{20, 15}, {24, 30}};
            double[][] MB = {{12, 20}, {19, 10}};

/*
            AgentOne one = null;
            AgentTwo two = null;
            one = new AgentOne(1, MA);
            two = new AgentTwo(2, MB);
            two.setIsRunning(false);
            
            one.setOtherAgent(two);
            two.setOtherAgent(one);
            
            Thread a = new Thread(one);
            Thread b = new Thread(two);
            a.start();
            b.start();
        */
            
            Agent A1 = new Agent(0,MA);
            Agent A2 = new Agent(1,MB);
            A2.setIsRunning(false);
            A1.setOtherAgent(A2);
            A2.setOtherAgent(A1);
            
            A1.start();
            A2.start();
            

        } catch (Exception ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
