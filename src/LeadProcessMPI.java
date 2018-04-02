import mpi.MPI;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LeadProcessMPI {
    private static final int master = 0;

    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int[] sendSlaveArray = new int[2];

        if (rank > master) {
            Random rand = new Random();
            sendSlaveArray[0] = rand.nextInt();
            sendSlaveArray[1] = rank;
            MPI.COMM_WORLD.Send(sendSlaveArray, 0, 2, MPI.INT, master, 0);
        }
        if (rank == 0) {
            HashMap<Integer, Integer> map = new HashMap<>();

            for (int dest = 1; dest < size; dest++) {
                MPI.COMM_WORLD.Recv(sendSlaveArray, 0, 2, MPI.INT, dest, 0);
                map.put(sendSlaveArray[1], sendSlaveArray[0]);
            }
            int maxValueInMap = (Collections.max(map.values()));
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                if (entry.getValue() == maxValueInMap) {
                    System.out.println("The lead process is: " + entry.getKey()
                            + " and the maximum value is " + maxValueInMap);
                }
            }
        }

        MPI.Finalize();
    }
}
