public class main {

    public static void main(String[] args) {
        int num_foragers = 5;
        int num_iterations = 2;
        int num_best = 3;


        FileReader file_reader = new FileReader();
        JSSP jssp = file_reader.read("data/2.txt");
        //System.out.println(jssp.operations.size());
        file_reader.make_graph(jssp);

        BeeColony BC = new BeeColony(num_foragers, num_best, jssp);

        BC.run(num_iterations);



    }
}
