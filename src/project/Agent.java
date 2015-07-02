/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.io.IOException;
import javax.swing.JTextArea;

/**
 *
 * @author kbarahona
 */
public class Agent extends Thread {

    private final int QUANTUM_UNIT = 1;
    public String LOG;

    protected int id;

    protected double[][] matrix;
    protected double[][] guaranteeMatrix;
    protected boolean[][] positionMatrix;
    protected double GoalNum;
    protected double NextGoalNum;
    protected int[] Goal;
    protected int LastCompensation;
    protected boolean isAlive;
    protected boolean isRunning;
    protected Agent OtherAgent;

    private JTextArea LOG_OUTPUT;

    public JTextArea getLOG_OUTPUT() {
        return LOG_OUTPUT;
    }

    public void setLOG_OUTPUT(JTextArea LOG_OUTPUT) {
        this.LOG_OUTPUT = LOG_OUTPUT;
    }

    public Agent getOtherAgent() {
        return OtherAgent;
    }

    public void setOtherAgent(Agent OtherAgent) {
        this.OtherAgent = OtherAgent;
    }

    public Agent(int id, double[][] matrix) {
        this.id = id;
        this.matrix = new double[2][2];
        this.guaranteeMatrix = new double[2][2];
        this.positionMatrix = new boolean[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                positionMatrix[i][j] = false;
            }
        }
        LastCompensation = 0;
        this.matrix = matrix;
        isAlive = true;
        isRunning = true;
        LOG = "";
        getGoal();
    }

    protected boolean checkCompensate() throws IOException {
        int I = OtherAgent.Goal[0];
        int J = OtherAgent.Goal[1];
        int CompensateValue = OtherAgent.LastCompensation;

        if ((matrix[I][J] + CompensateValue) >= (GoalNum - LastCompensation)) {
            LOG_OUTPUT.append("Agent " + id + " accepts compensation of Agent " + OtherAgent.id+ " \n");

            Goal[0] = OtherAgent.Goal[0];
            Goal[1] = OtherAgent.Goal[1];
            GoalNum = matrix[Goal[0]][Goal[1]];

            LastCompensation = 0;

            return false;
        } else {
            LOG_OUTPUT.append("Agent " + id + " rejects compensation of Agent " + OtherAgent.id + " \n");
            return true;
        }

    }

    public boolean sendCompensation() throws IOException, InterruptedException {

        if (OtherAgent.checkCompensate()) {
            if ((GoalNum - LastCompensation) < NextGoalNum) {
                LastCompensation = QUANTUM_UNIT;
                getGoal();
                //System.out.println("GolNum C" + (GoalNum - LastCompensation));
                //System.out.println("I id:" + id + " was rejected");
                //System.out.println(LastCompensation + " id:" + id);

            } else {
                //System.out.println("I id:" + id + " was rejected");
                //System.out.println(GoalNum + " Goal");
                //System.out.println("GolNum C" + (GoalNum - LastCompensation));
                LastCompensation += QUANTUM_UNIT;
                //System.out.println(LastCompensation + " id:" + id);

            }

            return false;
        } else {
            return true;
        }
    }

    protected void getGoal() {
        GoalNum = Double.MIN_VALUE;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (!positionMatrix[i][j]) {
                    if (GoalNum <= matrix[i][j]) {
                        GoalNum = matrix[i][j];
                        Goal = new int[2];
                        Goal[0] = i;
                        Goal[1] = j;
                    }
                }
            }
        }

        positionMatrix[Goal[0]][Goal[1]] = true;
        getNextGoal();
    }

    protected void getNextGoal() {
        NextGoalNum = Double.MIN_VALUE;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (!positionMatrix[i][j]) {
                    if (NextGoalNum <= matrix[i][j]) {
                        NextGoalNum = matrix[i][j];
                    }
                }
            }
        }
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean isIsAlive() {
        return isAlive;
    }

    public boolean isIsRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        while (isAlive) {
            if (isRunning) {
                try {
                    //System.out.println((id == 0 ? "P" : "Q"));
                    isRunning = false;
                    if (sendCompensation()) {
                        System.out.println("CHEQUE");
                        LOG = "";
                        printMatrix(matrix, LOG);
                        LOG_OUTPUT.append(LOG+ " \n");
                        LOG = "";
                        OtherAgent.printMatrix(OtherAgent.matrix, LOG);
                        LOG_OUTPUT.append(LOG+ " \n");
                        LOG = "";
                        //System.out.println("***************");
                        getGuarantee();
                        OtherAgent.getGuarantee();
                        isAlive = false;
                        isRunning = false;
                        OtherAgent.isAlive = false;
                        OtherAgent.isRunning = false;

                        System.out.println(LOG);
                    }
                    OtherAgent.setIsRunning(true);
                } catch (IOException ex) {
                    // Logger.getLogger(AgentOne.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    // Logger.getLogger(AgentOne.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ///Logger.getLogger(AgentOne.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        printMatrix(guaranteeMatrix, LOG);
        LOG_OUTPUT.append(LOG+ " \n");
        LOG = "";
        System.out.println("GoalNum: " + (GoalNum - LastCompensation + OtherAgent.LastCompensation));

    }

    private void printMatrix(double[][] matrix, String retVal) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                retVal += ("[" + matrix[i][j] + "]");
            }
            retVal += "\n";
        }
    }

    private void getGuarantee() {

        if (Goal[0] == 0) {
            if (Goal[1] == 0) {
                if (matrix[0][1] < (GoalNum - LastCompensation + OtherAgent.LastCompensation)) {
                    guaranteeMatrix[0][1] = GoalNum - LastCompensation + OtherAgent.LastCompensation - matrix[0][1];
                }
                if (matrix[1][0] < (GoalNum - LastCompensation + OtherAgent.LastCompensation)) {
                    guaranteeMatrix[1][0] = GoalNum - LastCompensation + OtherAgent.LastCompensation - matrix[1][0];
                }
            } else {
                if (matrix[0][0] < (GoalNum - LastCompensation + OtherAgent.LastCompensation)) {
                    guaranteeMatrix[0][0] = GoalNum - LastCompensation + OtherAgent.LastCompensation - matrix[0][0];
                }
                if (matrix[1][1] < (GoalNum - LastCompensation + OtherAgent.LastCompensation)) {
                    guaranteeMatrix[1][1] = GoalNum - LastCompensation + OtherAgent.LastCompensation - matrix[1][1];
                }
            }
        } else {
            if (Goal[1] == 0) {
                if (matrix[0][0] < (GoalNum - LastCompensation + OtherAgent.LastCompensation)) {
                    guaranteeMatrix[0][0] = GoalNum - LastCompensation + OtherAgent.LastCompensation - matrix[0][0];
                }
                if (matrix[1][1] < (GoalNum - LastCompensation + OtherAgent.LastCompensation)) {
                    guaranteeMatrix[1][1] = GoalNum - LastCompensation + OtherAgent.LastCompensation - matrix[1][1];
                }
            } else {
                if (matrix[0][1] < (GoalNum - LastCompensation + OtherAgent.LastCompensation)) {
                    guaranteeMatrix[0][1] = GoalNum - LastCompensation + OtherAgent.LastCompensation - matrix[0][1];
                }
                if (matrix[1][0] < (GoalNum - LastCompensation + OtherAgent.LastCompensation)) {
                    guaranteeMatrix[1][0] = GoalNum - LastCompensation + OtherAgent.LastCompensation - matrix[1][0];
                }
            }
        }
    }

}
