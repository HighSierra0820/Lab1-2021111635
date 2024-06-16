import java.io.*;
import java.util.*;

public class TextGraph {
    private Map<String, Map<String, Integer>> graph; // �洢���߼�Ȩ��
    private Map<String, Set<String>> inboundGraph; // �洢���

    public TextGraph() {
        graph = new HashMap<>();
        inboundGraph = new HashMap<>();
    }

    // ��ȡ�ı��ļ�����������ͼ
    public void buildGraph(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String previousWord = null; // ���ڴ洢��һ�����һ��������
            while ((line = br.readLine()) != null) {
                //��ȡ����
                String[] words = line.toLowerCase().replaceAll("[^a-z\\s]", "").split("\\s+");
                for (int i = 0; i < words.length - 1; i++) {
                    String currentWord = words[i];
                    String nextWord = words[i + 1];
                    updateGraph(currentWord, nextWord);
                }
                if (previousWord != null) {
                    updateGraph(previousWord, words[0]);
                }
                previousWord = words[words.length - 1];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ��������ͼ��Ȩ��
    private void updateGraph(String from, String to) {
        graph.computeIfAbsent(from, k -> new HashMap<>());
        graph.computeIfAbsent(to, k -> new HashMap<>());
        Map<String, Integer> neighbors = graph.get(from);//��ȡ�ڽӽڵ�ӳ��
        neighbors.put(to, neighbors.getOrDefault(to, 0) + 1); // ����Ȩ��
        // ���������Ϣ
        inboundGraph.computeIfAbsent(from, k -> new HashSet<>());
        inboundGraph.computeIfAbsent(to, k -> new HashSet<>());
        Set<String> inboundNeighbors = inboundGraph.get(to);
        inboundNeighbors.add(from);
    }

    // ��ѯ�ŽӴ�
    public String queryBridgeWords(String word1, String word2) {
        // ��� word1 �� word2 �Ƿ���ͼ��
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        // ��ȡ word1 �� word2 ���ڽ�ӳ��,���ǵ�����
        Set<String> word1Outbound = graph.get(word1).keySet();
        Set<String> word2Inbound = inboundGraph.get(word2);

        // �ҳ� word1 �� word2 �Ĺ�ͬ�ڽӴ�
        Set<String> bridgeWords = new HashSet<>(word1Outbound);
        bridgeWords.retainAll(word2Inbound);

        // ���ݽ���������
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else {
            return "The bridge words from " + word1 + " to " + word2 + " are: " + String.join(", ", bridgeWords);
        }
    }

    // ����bridge word�������ı�
    public String generateNewText(String inputText) {
        String[] words = inputText.toLowerCase().split("\\s+");
        StringBuilder newText = new StringBuilder();
        Random rand = new Random();

        // ���������ı��ĵ��ʣ�����bridge words���������ı�
        for (int i = 0; i < words.length - 1; i++) {
            String currentWord = words[i];
            String nextWord = words[i + 1];

            // ��ȡ��ǰ���ʵĳ����ڽӴ�
            Map<String, Integer> currentWordAdj = graph.getOrDefault(currentWord, new HashMap<>());
            // ��ȡ��һ�����ʵ�����ڽӴ�
            Set<String> nextWordInbound = inboundGraph.getOrDefault(nextWord, new HashSet<>());

            // ���ҹ�ͬ���ڽӴ���Ϊbridge words
            Set<String> bridgeWords = new HashSet<>(currentWordAdj.keySet());
            bridgeWords.retainAll(nextWordInbound);

            if (!bridgeWords.isEmpty()) {
                // �������bridge words�����ѡ��һ�����뵽���ı���
                String bridgeWord = bridgeWords.toArray(new String[0])[rand.nextInt(bridgeWords.size())];
                newText.append(currentWord).append(" ").append(bridgeWord).append(" ");
            } else {
                // ���ԭʼ���ʶԣ����û��bridge word����bridge word�Ѿ�����
                newText.append(currentWord).append(" ");
            }
        }
        // ������һ������
        newText.append(words[words.length - 1]);

        return newText.toString().trim();
    }

    // ������������֮������·��
    public String calcShortestPath(String word1, String word2) {
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No path exists between " + word1 + " and " + word2 + ".";
        }

        Queue<String> queue = new LinkedList<>();//����
        Set<String> visited = new HashSet<>();//�ѷ��ʽڵ㼯��
        Map<String, String> prev = new HashMap<>();//ǰ���ڵ�ӳ��
        Map<String, Integer> distance = new HashMap<>();//����ӳ��

        // ��ʼ��
        queue.offer(word1);
        visited.add(word1);
        distance.put(word1, 0);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            for (Map.Entry<String, Integer> neighborEntry : graph.get(current).entrySet()) {
                String neighbor = neighborEntry.getKey();
                int weight = neighborEntry.getValue();
                int distanceThroughCurrent = distance.getOrDefault(current, 0) + 1;
                if (!visited.contains(neighbor) && (!distance.containsKey(neighbor) || distanceThroughCurrent < distance.get(neighbor))) {
                    visited.add(neighbor);
                    prev.put(neighbor, current);
                    distance.put(neighbor, distanceThroughCurrent);
                    queue.offer(neighbor);
                }
            }
        }
        // ����Ƿ񵽴�Ŀ��ڵ�
        if (distance.containsKey(word2)) {
            return reconstructPath(prev, word1, word2) + " with length " + distance.get(word2) + ".";
        }
        return "No path exists between " + word1 + " and " + word2 + ".";
    }

    // ���� prev ӳ���ع�·��
    private String reconstructPath(Map<String, String> prev, String start, String end) {
        LinkedList<String> path = new LinkedList<>();
        for (String current = end; current != null; current = prev.get(current)) {
            path.addFirst(current);
        }
        return "Shortest path from " + start + " to " + end + " is: " + path ;
    }

    // �������
    public void randomWalk(Scanner scanner) {
        Map<String, Integer> edgeCount = new HashMap<>(); // ��¼�ߵĳ��ִ���
        Random rand = new Random();
        // ���ѡ����ʼ�ڵ�
        List<String> nodes = new ArrayList<>(graph.keySet());
        String startNode = nodes.get(rand.nextInt(nodes.size()));
        // ��¼����·��
        StringBuilder walk = new StringBuilder(startNode);

        while (true) {
            if (!graph.containsKey(startNode) || graph.get(startNode).isEmpty()) {
                // �����ǰ�ڵ�û�г��ߣ���������
                break;
            }

            // ѡ������ڽӽڵ�
            Map<String, Integer> neighbors = graph.get(startNode);
            List<String> neighborKeys = new ArrayList<>(neighbors.keySet());
            String nextNode = neighborKeys.get(rand.nextInt(neighborKeys.size()));

            // ��¼����·��
            walk.append(" > ").append(nextNode);
            startNode = nextNode; // �ƶ�����һ���ڵ�

            // �����ǰ·��
            System.out.println("Current Path: " + walk.toString());

            // ���±ߵļ���
            String edge = startNode + " -> " + nextNode;
            if(edgeCount.getOrDefault(edge, 0) + 1 == 2){
                break;
            } else{
                edgeCount.put(edge, edgeCount.getOrDefault(edge, 0) + 1);
            }

            // ѯ���û��Ƿ����
            while (true) {
                System.out.print("Do you want to continue (yes/no)? ");
                String userInput = scanner.nextLine().trim().toLowerCase();
                if (userInput.equals("yes")) {
                    break; // �û�ѡ�����
                } else if (userInput.equals("no")) {
                    System.out.println("Random walk stopped.");
                    break; // �û�ѡ��ֹͣ
                } else {
                    System.out.println("Invalid input, please enter 'yes' or 'no'.");
                }
            }
        }
    }

    // ���ɴ���ͼ�ε� DOT �����ַ���
    public String generateGraphvizDotString() {
        StringBuilder dotBuilder = new StringBuilder("digraph G {\n");
        dotBuilder.append("    rankdir=LR;\n"); // ���ò��ַ���
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            String node = entry.getKey();
            for (Map.Entry<String, Integer> neighborEntry : entry.getValue().entrySet()) {
                String neighbor = neighborEntry.getKey();
                dotBuilder.append("    \"").append(node).append("\" -> \"").append(neighbor)
                        .append("\" [label=\"").append(neighborEntry.getValue()).append("\"];\n");
            }
        }
        dotBuilder.append("}");
        return dotBuilder.toString();
    }

    // ʹ�� Graphviz ����ͼƬ
    public void showDirectedGraph(Scanner scanner) {
        System.out.print("Enter the output image file path (e.g., C:\\path\\to\\image.png): ");
        String outputPath = scanner.nextLine();
        String dotString = generateGraphvizDotString();
        try {
            // �� DOT �ַ���д����ʱ�ļ�
            File tempDotFile = File.createTempFile("graph", ".dot");
            try (PrintWriter writer = new PrintWriter(tempDotFile, "UTF-8")) {
                writer.println(dotString);
            }

            // ���� Graphviz �� dot ��������ͼƬ
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", tempDotFile.getAbsolutePath(), "-o", outputPath);
            Process process = processBuilder.start();
            process.waitFor();

            // ɾ����ʱ DOT �ļ�
            tempDotFile.delete();

            System.out.println("Graph image generated at: " + outputPath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TextGraph textGraph = new TextGraph();
        System.out.println("Welcome to the Text Graph Application!");

        // ��ȡ�ı��ļ�����������ͼ
        System.out.print("Enter the file path: ");
        String filePath = scanner.nextLine();
        textGraph.buildGraph(filePath);

        boolean running = true;
        while (running) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Query Bridge Words");
            System.out.println("2. Generate New Text");
            System.out.println("3. Calculate Shortest Path");
            System.out.println("4. Perform Random Walk");
            System.out.println("5. Show Directed Graph");
            System.out.println("6. Exit");

            System.out.print("Enter your choice (1-6): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter word1: ");
                    String word1 = scanner.nextLine();
                    System.out.print("Enter word2: ");
                    String word2 = scanner.nextLine();
                    System.out.println(textGraph.queryBridgeWords(word1, word2));
                    break;
                case "2":
                    System.out.print("Enter input text: ");
                    String inputText = scanner.nextLine();
                    System.out.println(textGraph.generateNewText(inputText));
                    break;
                case "3":
                    System.out.print("Enter start word: ");
                    String startWord = scanner.nextLine();
                    System.out.print("Enter end word: ");
                    String endWord = scanner.nextLine();
                    System.out.println(textGraph.calcShortestPath(startWord, endWord));
                    break;
                case "4":
                    textGraph.randomWalk(scanner);
                    break;
                case "5":
                    textGraph.showDirectedGraph(scanner);
                    break;
                case "6":
                    running = false;
                    System.out.println("Exiting the application...");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }

        scanner.close();
    }
}