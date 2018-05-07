import java.util.*;

public class BeeColony {

    public List<Forager> foragers = new ArrayList<Forager>();
    public List<Forager> best_foragers = new ArrayList<Forager>();
    public double colony_profitability_rating;
    public int num_best;
    public int num_foragers;
    public JSSP jssp;
    public int problem_size;
    public Forager best_forager;
    public int best_makespan;
    //public int num_iterations;

    public BeeColony(int num_foragers, int num_best, JSSP jssp){
        this.num_best = num_best;
        this.num_foragers = num_foragers;
        this.jssp = jssp;
        this.problem_size = jssp.num_jobs*jssp.num_machines;
        this.best_makespan = 100000;
        //this.populate_foragers();
    }

    public void populate_foragers() {
        for (int i = 0 ; i < this.num_foragers ; i++) {
            this.foragers.add(new Forager(this.jssp));
        }

    }

    public void run(int iterations) {

        this.populate_foragers();

        for (int i = 0 ; i < iterations ; i++){
            for (int j = 0 ; j < this.problem_size ; j++) {
                this.step();
            }
            for (Forager forager : this.foragers) {
                forager.update_profitability_rating();
            }

            this.populate_best_foragers();
            this.update_colony_profitability_rating();
            this.assign_follow_probabilities();
            this.waggle_dance(100);

            if (this.best_foragers.get(0).makespan < this.best_makespan){
                this.best_forager = this.best_foragers.get(0);
                this.best_makespan = this.best_foragers.get(0).makespan;
            }

            if (i != iterations-1) {
                this.reset_foragers();
            }
        }

        System.out.println(this.best_forager);

    }

    public void step() {
        if (this.foragers.get(0).legal_ops.size() == 0){
            System.out.println("Bees probably reached the end");
        }

        for (Forager forager : this.foragers) {
            Operation next_op = forager.select_next_op();
            forager.move(next_op, forager.current_op);
        }
    }

    public void reset_foragers(){
        for(Forager forager : foragers){
            forager.r = 0;
            forager.profitability_rating = 0;
            forager.current_op = null;
            forager.tour = null;
        }
    }

    public void update_colony_profitability_rating() {
        double total_profitability_rating = 0.0;
        for (Forager forager : this.best_foragers) {
            total_profitability_rating += forager.profitability_rating;
        }
        this.colony_profitability_rating = total_profitability_rating / this.best_foragers.size();
    }

    public void populate_best_foragers() {
        PriorityQueue<Forager> foragers = new PriorityQueue<Forager>(new Comparator<Forager>() {
            @Override
            public int compare(Forager o1, Forager o2) {
                if (o1.makespan < o2.makespan) {
                    return 1;
                } if (o1.makespan > o2.makespan) {
                    return -1;
                }
                return 0;
            }
        });

        for (Forager forager : this.foragers) {
            foragers.add(forager);
        }

        for (int i = 0 ; i < this.num_best ; i++) {
            this.best_foragers.add(foragers.poll());
        }
    }

    public void assign_follow_probabilities(){

        for (Forager forager : this.foragers){
            double profitability = forager.profitability_rating;
            if (!this.best_foragers.contains(forager)){
                if (profitability < 0.9*this.colony_profitability_rating) {
                    forager.r = 0.6;
                }else if (profitability < 0.95*this.colony_profitability_rating){
                    forager.r = 0.2;
                }else if (profitability < 1.15*this.colony_profitability_rating){
                    forager.r = 0.02;
                }else {
                    forager.r = 0.00;
                }
            }
        }
    }

    public void waggle_dance(int scaling_factor) {
        Random rnd = new Random();
        for (Forager forager : this.best_foragers) {
            forager.waggle_duration = scaling_factor * (forager.profitability_rating / this.colony_profitability_rating);
        }

        for (Forager forager : this.foragers) {
            if (!this.best_foragers.contains(forager)) {
                double follow = rnd.nextDouble();
                if (follow < forager.r) {
                    forager.preferred_tour_indicator = 1;
                    Forager follow_forager = this.best_foragers.get(rnd.nextInt(num_best - 1));
                    List<Operation> temp_tour = follow_forager.tour;
                    List<Operation> preferred_tour = new ArrayList<Operation>();
                    for (Operation operation : temp_tour) {
                        preferred_tour.add(operation);
                    }
                    forager.preferred_tour = preferred_tour;
                }
            } else {
                List<Operation> preferred_tour = new ArrayList<Operation>();
                for (Operation operation : forager.tour) {
                    preferred_tour.add(operation);
                    forager.preferred_tour_indicator = 1;
                    forager.preferred_tour = preferred_tour;
                }
            }
        }
    }


}
