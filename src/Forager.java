import java.util.*;

public class Forager {

    //bruker priority queue til å sortere ut de x beste biene som skal gjøre waggle dance

    public double profitability_rating;
    public double preferred_tour_indicator;
    public List<Operation> preferred_tour;
    public Operation next_preferred_op;
    public Operation current_op;
    public List<Operation> tour;
    public List<Operation> legal_ops;
    public int num_jobs;
    public int num_machines;
    public JSSP jssp;
    public List<List<Operation>> graph;
    public int makespan;
    public double r;
    public double waggle_duration;
    public Operation preferred_op;


    public Forager(JSSP jssp){
        this.tour = new ArrayList<Operation>();
        this.preferred_tour = new ArrayList<Operation>();
        this.legal_ops = new ArrayList<Operation>();
        this.graph = new ArrayList<List<Operation>>();
        this.jssp = jssp;
        this.num_jobs = jssp.num_jobs;
        this.num_machines = jssp.num_machines;
        this.graph = jssp.getOperations();
        this.current_op = jssp.sink;
        this.preferred_op = null;
        this.legal_ops = jssp.source.getNeigbourhood();
    }

    public Operation select_next_op() {
        double total_prob;
        int num_legal = this.legal_ops.size(); // k from paper
        double alpha = 0.9; // alpha from paper
        if (this.preferred_tour.size() > 0) {
            this.preferred_op = this.preferred_tour.get(0);
        }

        List<Double> ratings = new ArrayList<Double>();
        if (this.preferred_tour_indicator != num_legal && this.preferred_tour_indicator < num_legal) {
            for (Operation operation : this.legal_ops) { // should improve this...
                if (operation.equals(this.preferred_op)) {
                    ratings.add((1 - this.preferred_tour_indicator * 0.9) / (num_legal - this.preferred_tour_indicator));
                } else {
                    ratings.add(1.0/num_legal);
                }
            }
        } else {
            for (Operation operation : this.legal_ops) {
                ratings.add(alpha);
            }
        }

        List<Double> probabilities = new ArrayList<Double>();
        for (int i = 0 ; i < num_legal ; i++){
            probabilities.add(ratings.get(i)*(1.0/legal_ops.get(i).getDuration()));
        }

        System.out.println(probabilities);

        // roulette wheel selection for next op
        Operation next_op = legal_ops.get(0);
        double total = ratings.get(0);
        Random rnd = new Random();

        for( int i = 1; i < num_legal; i++ ) {
            total += probabilities.get(i);
            if(rnd.nextDouble() <= (probabilities.get(i) / total)) {
                next_op = this.legal_ops.get(i);
            }
        }

        if (next_op != this.next_preferred_op) {
            this.preferred_tour_indicator = 0;
        }
        if (this.preferred_tour.size() > 0){
            this.preferred_tour.remove(0);
        }

        return next_op;
    }


    public int calculate_tour_length() {

        List<List<Integer>> completion_times = new ArrayList<List<Integer>>();

        for (int i = 0 ; i < num_jobs ; i++){
            List<Integer> zeros = new ArrayList<Integer>(Collections.nCopies(this.num_machines, 0));
            completion_times.add((zeros));
        }
        int makespan = 0;
        for (Operation operation : this.tour) {
            int job_no = operation.job;
            int machine_no = operation.machine;
            int duration = operation.duration;

            int max_job = Collections.max(completion_times.get(job_no));
            int max_machine = 0;
            for (List<Integer> list : completion_times) {
                if (list.get(machine_no) > max_machine) {
                    max_machine = list.get(machine_no);
                }
            }
            int max = Math.max(max_job, max_machine);
            completion_times.get(machine_no).set(job_no, max+duration);
            if (max+duration > makespan) {
                makespan = max+duration;
            }
        }
        this.makespan = makespan;
        return makespan;
    }

    public void move(Operation operation_to, Operation operation_from) {
        this.tour.add(operation_to); // add operation to tour
        this.legal_ops.remove(operation_to); // add all neigbouring operations to the operation we're moving to
        for (Operation neigbour : operation_to.getNeigbourhood()) {
            if (operation_to.getNeigbourhood().size() != 0) {
                this.legal_ops.add(neigbour);
            }
            this.current_op = operation_to;
        }
    }

    public void update_profitability_rating() {
        int makespan = this.calculate_tour_length();
        this.profitability_rating = 1.0/makespan;
    }


    @Override
    public String toString() {
        String out = "";
        out += "Forager with makespan of: " + this.makespan;
        return out;
    }
}
