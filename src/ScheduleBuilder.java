/**

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduleBuilder {

    List<Operation> tour;
    List<List<Integer>> completion_times;
    JSSP jssp;


    public ScheduleBuilder(List<Operation> tour, JSSP jssp) {
        this.tour = tour;
        this.jssp = jssp;
    }

    public List<List<Double>> compute_matrix() {
        int num_jobs = jssp.num_jobs;
        int num_machines = jssp.num_machines;
        List<List<Double>> completion_times = new ArrayList<List<Double>>();

        for (int i = 0 ; i < num_machines ; i++){
            List<Double> zeros = new ArrayList<Double>(Collections.nCopies(num_jobs, 0.0));
            completion_times.add((zeros));
        }

        for (int j = 0 ; j < this.tour.size() ; j++){
            int job_no = this.tour.get(j).job;
            int machine_no = this.tour.get(j).machine;
            double duration = this.tour.get(j).duration;



        }
    }
}

**/